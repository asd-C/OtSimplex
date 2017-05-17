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
	public static final int NAO_OBTIDA = -1;
	// Codigo referente a solucao otima obtida
	public static final int OTIMA = 0;
	// Codigo referente a multiplas solucoes obtida
	public static final int MULT = 1;
	// Codigo referente a solucao impossivel obtida
	public static final int IMPOSS = 2;
	// Codigo referente a solucao ilimitada obtida
	public static final int ILIM = 3;

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
	public Simplex( Matriz mat ) {
		list_mat = new ArrayList<Matriz>();
		list_mat.add( mat.clone() );
		codSol = NAO_OBTIDA;

		obtSolucao();
	}

	/**
	* Obtem a solucao utilizando o algoritmo
	* de Simplex proposto por Vettsel
	*/
	private void obtSolucao() {
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
					else
					{ 
						codSol = IMPOSS; fimFase = true; 
					}
				}
				else { fimFase = true;}
			}

			fimFase = false;
			
			// Segunda fase
			while( !fimFase && codSol == NAO_OBTIDA ) {
				mat = list_mat.get( list_mat.size() - 1).clone();

				int indice = mat.obtVNBFXPos();

				if( indice != mat.NAO_EXISTE ) {
					mat.defCP( indice );

					indice = mat.obtVBPos();

					if( indice != mat.NAO_EXISTE ) {
						indice = mat.obtLP();

						list_mat.add( mat.algTroca() );
					}
					else { codSol = ILIM; fimFase = true; }
				}
				else{ 
					codSol = MULT;fimFase = true; 
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
		if( codSol == OTIMA || codSol == MULT ) { result += ("A solucao otima foi encontrada!");}
		else if( codSol == ILIM ) { result += ("A solucao deste problema e ilimitada!");}
		else if( codSol == IMPOSS ) { result += ("Nao existe solucao para este problema!"); }
		else { result += ("A solucao deste problema ainda nao foi encontrada!"); }

		int tam = list_mat.size();
		Matriz mat = list_mat.get( tam - 1 );		
		String valores = "";

		if( codSol == OTIMA || codSol == MULT ) { 
			valores += String.format( "\nZ = %.3f", mat.obtZ() );
			valores += String.format( "\nX1 = %.3f", mat.obtX_(1) );
			valores += String.format( "\nX2 = %.3f", mat.obtX_(2) );
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

	/**
	* Retorna o codigo da solucao obtido pelo Simplex
	*
	* @return Codigo (int) da solucao obtida por este.
	*/
	public int obtCodSol()
	{
		return codSol;
	}
}

