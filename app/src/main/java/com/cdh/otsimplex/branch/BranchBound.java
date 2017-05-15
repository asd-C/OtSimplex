package com.cdh.otsimplex.branch;

import com.cdh.otsimplex.simplex.Matriz;
import com.cdh.otsimplex.simplex.Simplex;

import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class BranchBound
{
	public static final int NULO = 0;
	public static final int IMPOSS = 1;
	public static final int OTIMO = 2;
	public static final int CAND = 3;

	private No raiz;
	private No otimo;
	private int tipo;
	private int numNos;

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

	private void obtSol() 
	{
		Stack<No> pilha = new Stack<No>();

		pilha.add(raiz);

		while( !pilha.empty() )
		{
			No atual = pilha.pop();

			System.out.println( "\nNo ATUAL " + atual.obtID() );
			System.out.println( atual.toStrUltRes() + "\n" );
			System.out.println( "ORIGEM-------------------------");
			Matriz m = atual.obtSrcMat();
			for( String[] l : m.toStringSCS() )
			{
				for( String pos : l )
				{
					System.out.print( pos + "\t");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println( "SOLUCAO-------------------------");
			m = atual.obtSolMat();
			for( String[] l : m.toStringSCS() )
			{
				for( String pos : l )
				{
					System.out.print( pos + "\t");
				}
				System.out.println();
			}
			System.out.println("-----------------------------");

			int codSimplex = atual.obtCodSim();

			if( codSimplex == Simplex.IMPOSS || codSimplex == Simplex.ILIM )
			{
				System.out.println( "IMPOSS ou ILIM!");
				atual.defTSCod(IMPOSS);
			}
			else
			{
				Matriz mat = atual.obtSolMat();

				double x1Esq = Math.floor( (float)mat.obtX_(1) );
				double x1Dir = Math.ceil( (float)mat.obtX_(1) );

				double x2Esq = Math.floor( (float)mat.obtX_(2) );
				double x2Dir = Math.ceil( (float)mat.obtX_(2) );

				if( x1Esq == x1Dir && x2Esq == x2Dir )
				{
					System.out.println( "INTEIRO!");
					cmpOtimo( atual );
					if( atual == otimo )
					{
						System.out.println( "OTIMO");
					}

					Matriz srcMat = atual.obtSrcMat();
				}
				else
				{
					int cont = 1;
					String[] resEsq, resDir;
					Matriz srcMat = atual.obtSrcMat();

					if( x1Esq != x1Dir )
					{	
						System.out.println( "No " + ( numNos ) + " e No " + ( numNos + 1 ) );
						System.out.println( "x1 <= " + x1Esq + " e x1 >=" + x1Dir );
						resEsq = new String[]{ x1Esq + "" , "1" , "0" , "<="};
						atual.defNo( 1 , No.ESQ, srcMat , resEsq , numNos++ );
						No esq = atual.obtNo( 1 , No.ESQ );

						resDir = new String[]{ x1Dir + "" , "1" , "0" , ">="};
						atual.defNo( 1 , No.DIR, srcMat , resDir , numNos++ );
						No dir = atual.obtNo( 1 , No.DIR );

						pilha.add( esq ); pilha.add( dir );
					}

					if( x2Esq != x2Dir )
					{
						System.out.println( "No " + ( numNos ) + " e No " + ( numNos + 1 ) );
						System.out.println( "x2 <= " + x2Esq + " e x2 >=" + x2Dir );

						resEsq = new String[]{ x2Esq + "" , "0" , "1" , "<="};
						atual.defNo( 2 , No.ESQ, srcMat , resEsq , numNos++ );
						No esq = atual.obtNo( 2 , No.ESQ );

						resDir = new String[]{ x2Dir + "" , "0" , "1" , ">="};
						atual.defNo( 2 , No.DIR, srcMat , resDir , numNos++ );
						No dir = atual.obtNo( 2 , No.DIR );

						pilha.add( esq ); pilha.add( dir );
					}
				}
			}
		}

		if( otimo != null )
		{
			otimo.defTSCod(OTIMO);
		}
	}

	private void cmpOtimo( No no )
	{
		if( otimo == null )
		{
			otimo = no;
		}
		else 
		{
			Matriz matOtm = otimo.obtSolMat();
			double zOtimo = matOtm.obtZ();

			Matriz matNo = no.obtSolMat();
			double zNo = matNo.obtZ();

			// SE MAX -> tipo = 1
			// 	x > otimo
			// 		TRUE : > 0 <-
			// 		FALSE : < 0
			// SE MIN -> tipo = -1
			// 	x > otimo
			// 		TRUE : < 0 
			// 		FALSE : > 0 <-
			double diff = ( zNo - zOtimo ) * tipo;

			if( diff > 0 )
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

	public ArrayList<No> toList()
	{
		ArrayList<No> lista = new ArrayList<No>();
		Queue<No> pilha = new LinkedList<No>();

		pilha.add(raiz);

		while( !pilha.isEmpty() )
		{
			No atual = pilha.remove();
			lista.add( atual );

			No x1Esq = atual.obtNo( 1 , No.ESQ );
			if( x1Esq != null )
			{
				pilha.add( x1Esq );
			}

			No x1Dir = atual.obtNo( 1 , No.DIR );
			if( x1Dir != null )
			{
				pilha.add( x1Dir );
			}

			No x2Esq = atual.obtNo( 2 , No.ESQ );
			if( x2Esq != null )
			{
				pilha.add( x2Esq );
			}

			No x2Dir = atual.obtNo( 2 , No.DIR );
			if( x2Dir != null )
			{
				pilha.add( x2Dir );
			}
		}

		return lista;
	}

	public No obtRaiz()
	{
		return raiz;
	}

}