/**
* Esta classe utiliza o algoritmo Simplex
* para resolver os problemas lineares
* solicitados pelo usuario.
*
* @author Dehua Chen
* @author Felipe Belem
*/
package com.cdh.otsimplex.simplex;

import java.util.ArrayList;

public class Simplex {
	// Codigo referente a solucao parcial obtida
	private static final int NAO_OBTIDA = -1;
	// Codigo referente a solucao otima obtida
	private static final int OTIMA = 0;
	// Codigo referente a multiplas solucoes obtida
	private static final int MULT = 1;
	// Codigo referente a solucao impossivel obtida
	private static final int IMPOSS = 2;
	// Codigo referente a solucao ilimitada obtida
	private static final int ILIM = 3;

	// Lista de matrizes de resolucao
	private ArrayList<Matriz> list_mat;
	// Codigo da Solucao obtida
	private int codSol;
	
	/**
	* Construtor: Constroi uma Matriz a partir de uma
	* matriz de coeficientes em parametro.
	*
	* @param 	mat - Matriz de coeficientes
	*/
	public Simplex( double[][] mat ) {
		list_mat = new ArrayList<Matriz>();
		list_mat.add( new Matriz( mat ) );
		codSol = NAO_OBTIDA;
	}

	/**
	* Obtem a solucao utilizando o algoritmo
	* de Simplex proposto por Vettsel
	*/
	public void obtSolucao() {
		Matriz mat = null;

		while( codSol == NAO_OBTIDA ) {
			boolean fimFase = false;
			
			// Primeira Fase
			while( !fimFase ) {
				mat = list_mat.get( list_mat.size() - 1).clone();
				int indice = mat.obtVBMLNeg();

				if( indice != mat.NAO_EXISTE ) {
					indice = mat.obtCPNeg();
				
					if( indice != mat.NAO_EXISTE ) {
						indice = mat.obtLP();

						list_mat.add( mat.algTroca() );
					}
					else{ codSol = IMPOSS; fimFase = true; }
				}
				else { fimFase = true;}
			}

			fimFase = false;
			
			// Segunda fase
			while( !fimFase && codSol == NAO_OBTIDA ) {
				mat = list_mat.get( list_mat.size() - 1).clone();
				int indice = mat.obtVNBFXPos();

				if( indice != mat.NAO_EXISTE ) {
					indice = mat.obtVBPos();

					if( indice != mat.NAO_EXISTE ) {
						indice = mat.obtLP();

						list_mat.add( mat.algTroca() );
					}
					else { codSol = ILIM; fimFase = true; }
				}
				else{ 
					codSol = OTIMA;fimFase = true;
				}
			}
		}
	}

	/**
	* Imprime a solucao de acordo com o codigo
	* de solucao corrente
	*/
	public String imprimirSol() {
		String result = "";
		if( codSol == OTIMA ) { result = ("A solucao otima foi encontrada!");}
		else if( codSol == MULT ) { result = ("Uma das diversas solucoes otimas foi encontrada!");}
		else if( codSol == ILIM ) { result = ("A solucao deste problema e ilimitada!");}
		else if( codSol == IMPOSS ) { result = ("Nao existe solucao para este problema!"); }
		else { result = ("A solucao deste problema ainda nao foi encontrada!"); }

		int tam = list_mat.size();
		Matriz mat = list_mat.get( tam - 1 );		
		String valores = "\n";

		if( codSol == OTIMA || codSol == MULT ) { 
			valores += String.format( "Z = %.3f\n", Math.abs( mat.obtX_(0) ) );
			valores += String.format( "X1 = %.3f\n", mat.obtX_(1) );
			valores += String.format( "X2 = %.3f\n", mat.obtX_(2) );
		}

		result += valores;

		return result;
	}

	/**
	* Obtem as matrizes de SCS que foram adicionadas para
	* chegar a solucao do problema
	*
	* @return ArrayList<Matriz> de Matrizes de SCS
	*/
	public ArrayList<Matriz> obtMatSCS()
	{
		return list_mat;
	}
}

