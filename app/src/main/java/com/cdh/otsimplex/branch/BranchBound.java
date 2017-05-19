package com.cdh.otsimplex.branch;

import com.cdh.otsimplex.simplex.Matriz;
import com.cdh.otsimplex.simplex.Simplex;

import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.math.*;


/**
* Esta classe remete ao método Branch And Bound
* para obtenção de soluções de problemas lineares.
* Possui, somente, o ponteiro do no raiz, e do no otimo
* encontrado ate o momento. Tipo remete a grandeza da funcao,
* se e de maximizacao ou de minimizacao, e numNos, o numero de nos
* existentes na arvore.
*
* @author Dehua Chen
* @author Felipe Belem
*/
public class BranchBound
{
	// Tipo da solução, onde NULO não é inteiro, e CAND,
	// candidato a otimo.
	public static final int NULO = 0;
	public static final int IMPOSS = 1;
	public static final int OTIMO = 2;
	public static final int CAND = 3;

	private static final int MAX_NIVEIS = 3;

	private No raiz;
	private No otimo;
	private int tipo;
	private int numNos;

	/**
	* Determina a matriz originaria do BaB e qual a sua grandeza
	*
	* @param m: Matriz inicial do BaB
	* @param tipo: Grandeza da função (MAX ou MIN)
	*/
	public BranchBound(Matriz m , String tipo )
	{
		raiz = new No( m );
		otimo = null;
		numNos = 1;

		if( tipo.equalsIgnoreCase("max") )
		{
			this.tipo = 1;
		}
		else
		{
			this.tipo = -1;
		}

		obtSol();
	}

	/**
	* Obtem a solucao do problema
	*/
	private void obtSol()
	{
		Stack<No> pilha = new Stack<No>();

		pilha.add(raiz);

		// Para cada no que deve ser expandido
		while( !pilha.empty() )
		{
			No atual = pilha.pop();

			int codSimplex = atual.obtCodSim();

			// Não há como expandir esse no
			if( codSimplex == Simplex.IMPOSS || codSimplex == Simplex.ILIM )
			{
				atual.defTSCod(IMPOSS);
			}
			else
			{
				// Verificar se a solucao obtida e inteira
				Matriz mat = atual.obtSolMat();

				BigDecimal x1 = mat.obtX_(1);
				BigDecimal x2 = mat.obtX_(2);

				BigDecimal x1Esq = x1.setScale(0 , RoundingMode.FLOOR);
				BigDecimal x1Dir = x1.setScale(0 , RoundingMode.CEILING);

				BigDecimal x2Esq = x2.setScale(0 , RoundingMode.FLOOR);
				BigDecimal x2Dir = x2.setScale(0 , RoundingMode.CEILING);

				boolean x1_int = x1Esq.compareTo( x1Dir ) == 0;
				boolean x2_int = x2Esq.compareTo( x2Dir ) == 0;

				if( x1_int && x2_int )
				{
					// Uma vez que o teto e o piso são iguais, então é inteiro
					cmpOtimo( atual );
				}
				else if( atual.obtNivel() < MAX_NIVEIS )
				{
					String[] resEsq, resDir;
					Matriz srcMat = atual.obtSrcMat();

					// Caso x2 não seja inteiro
					if( !x1_int )
					{
						resEsq = new String[]{ x1Esq.toString() , "1" , "0" , "<="};
						//						  v----------------- Esse parametro foi adicionado (atual e o no pai)
						atual.defNo( 1 , No.ESQ, atual, srcMat , resEsq , numNos++ );
						No esq = atual.obtNo( 1 , No.ESQ );

						resDir = new String[]{ x1Dir.toString() , "1" , "0" , ">="};
						//						  v----------------- Esse parametro foi adicionado (atual e o no pai)
						atual.defNo( 1 , No.DIR, atual,  srcMat , resDir , numNos++ );
						No dir = atual.obtNo( 1 , No.DIR );

						pilha.add( esq ); pilha.add( dir );
					}

					// Caso x2 não seja inteiro
					if( !x2_int )
					{
						resEsq = new String[]{ x2Esq.toString() , "0" , "1" , "<="};
						//						  v----------------- Esse parametro foi adicionado (atual e o no pai)
						atual.defNo( 2 , No.ESQ, atual, srcMat , resEsq , numNos++ );
						No esq = atual.obtNo( 2 , No.ESQ );

						resDir = new String[]{ x2Dir.toString() , "0" , "1" , ">="};
						//						  v----------------- Esse parametro foi adicionado (atual e o no pai)
						atual.defNo( 2 , No.DIR, atual, srcMat , resDir , numNos++ );
						No dir = atual.obtNo( 2 , No.DIR );

						pilha.add( esq ); pilha.add( dir );
					}
				}
			}
		}

		// Encontrou pelo menos um candidato a otimo
		if( otimo != null )
		{
			otimo.defTSCod(OTIMO);
		}
	}

	/**
	* Compara o no em parametro se ele é uma resposta
	* melhor do que a conhecida ate o momento. Caso seja,
	* este sera o novo otimo
	*
	* @param no: No a ser analisado
	*/
	private void cmpOtimo( No no )
	{
		if( otimo == null )
		{
			otimo = no;
		}
		else 
		{
			Matriz matOtm = otimo.obtSolMat();
			BigDecimal zOtimo = matOtm.obtZ();

			Matriz matNo = no.obtSolMat();
			BigDecimal zNo = matNo.obtZ();

			// SE MAX -> tipo = 1
			// 	x > otimo
			// 		TRUE : > 0 <-
			// 		FALSE : < 0
			// SE MIN -> tipo = -1
			// 	x > otimo
			// 		TRUE : < 0 
			// 		FALSE : > 0 <-
			BigDecimal diff = zNo.subtract( zOtimo ).multiply( BigDecimal.valueOf( tipo ) );
			boolean mai_zero = diff.compareTo( BigDecimal.ZERO ) > 0;

			if( mai_zero )
			{
				otimo.defTSCod(CAND);
				otimo = no;
			}
			else
			{
				no.defTSCod(CAND);
			}
		}
	}

	/**
	* Converte para String a mensagem referente a solucao
	* do BaB
	*
	* @return Mensagem da solucao do problema
	*/
	public String toStrSolMsg()
	{
		String msg = "Nao existe solucao inteira!";
		
		if( otimo != null )
		{	
			msg = "Existe solucao otima inteira!\n";
			
			Matriz sol = otimo.obtSolMat();
			msg += String.format( "Z = %.3f\n", sol.obtZ() );
			msg += String.format( "X1 = %.3f\n", sol.obtX_(1) );
			msg += String.format( "X2 = %.3f\n", sol.obtX_(2) );
		}
		
		return msg;
	}

	/**
	* Converte para uma lista todos os nos desta arvore
	* utilizando o conceito de busca em largura
	*
	* @return Lista contendo todos os nos dessa arvore
	*/
	public ArrayList<No> toList()
	{
		ArrayList<No> lista = new ArrayList<No>();
		Queue<No> fila = new LinkedList<No>();

		fila.add(raiz);

		while( !fila.isEmpty() )
		{
			No atual = fila.remove();
			lista.add( atual );

			No x1Esq = atual.obtNo( 1 , No.ESQ );
			if( x1Esq != null )
			{
				fila.add( x1Esq );
			}

			No x1Dir = atual.obtNo( 1 , No.DIR );
			if( x1Dir != null )
			{
				fila.add( x1Dir );
			}

			No x2Esq = atual.obtNo( 2 , No.ESQ );
			if( x2Esq != null )
			{
				fila.add( x2Esq );
			}

			No x2Dir = atual.obtNo( 2 , No.DIR );
			if( x2Dir != null )
			{
				fila.add( x2Dir );
			}
		}

		return lista;
	}

	/**
	* Retorna o no raiz desta arvore
	*
	* @return No raiz desta arvore
	*/
	public No obtRaiz()
	{
		return raiz;
	}

}