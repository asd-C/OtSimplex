package com.cdh.otsimplex.etc;

import java.math.*;


/**
* Esta classe e responsavel por converter os 
* parametros obtidos pela aplicacao em uma matriz
* valida para a classe Simplex
*
* @author Dehua Chen
* @author Felipe Belem
*/
public class Cnvt
{
	/**
	* Converte uma matriz de coeficientes, acoplado com sua
	* grandeza, em uma matriz double que servirá de entrada para
	* o algoritmo Simplex
	*
	* @param 	matriz - Matriz de String que contém os coeficientes
	*						e a grandeza de cada linha
	* @return	BigDecimal[][] que servirá de entrada para o algoritmo
	*			Simplex
	*/
	public static BigDecimal[][] cnvtMat( String[][] matriz )
	{
		int linhas = matriz.length;

		BigDecimal[][] cnvt = new BigDecimal[linhas][];

		for( int i = 0; i < linhas; i++ )
		{	
			cnvt[i] = cnvtLinha( matriz[i] );
		}

		return cnvt;
	}

	/**
	* Converte uma linha de coeficientes, acoplado com sua
	* grandeza, em um array double que constituira como uma das
	* linhas da matriz a ser executada no Simplex
	*
	* @param 	linha - Array de String que contém os coeficientes
	*						e a grandeza da linha
	* @return	double[] que servirá como uma linha da entrada do algoritmo
	*			Simplex
	*/
	public static BigDecimal[] cnvtLinha ( String[] linha )
	{
		int tam = linha.length - 1;

		BigDecimal[] cnvt = new BigDecimal[ tam ];
		int tipo = 1;

		if( linha[ tam ].equalsIgnoreCase("min")
			|| linha[ tam ].equalsIgnoreCase(">=") )
		{
			tipo = -1;
		}	

		for ( int i = 0; i < tam; i++ ) {
			cnvt[i] = new BigDecimal( linha[i] );
			cnvt[i] = cnvt[i].multiply( BigDecimal.valueOf( tipo ) );
		}

		return cnvt;
	}

	/**
	* Converte a restricao normalizada (nos moldes da aplicacao)
	* para sua forma original (como uma unica String)
	*
	* @param 	res - Restricao normalizada
	* @return	Forma original de res, em uma única String
	*/
	public static String cnvtRes( String[] res )
	{
		int tam = res.length;
		String vars = "";
		String eq = "";
		
		for( int i = 1; i < tam - 1; i++ )
		{
			String coef = res[i];
			if( !coef.isEmpty() )
			{
				double coef_val = Double.parseDouble( coef );
				
				if( coef_val != 0.0 )
				{

					if( coef_val == 1.0 || coef_val == -1.0 )
					{
						coef = "";
					}

					if( coef.isEmpty() )
					{
						vars += coef + "x_" + i;
					}
					else if( coef_val < 0)
					{
						vars += " " + coef + "x_" + i;
					}
					else
					{
						vars += " + " + coef + "x_" + i;
					}
				}
			}
		}
		eq = res[tam-1] + " " + res[0];
		
		return vars + " " + eq;
	}


	public static String[][] extrairDados( String[][] mat_str )
	{
		String[][] ret = new String[mat_str.length - 1][mat_str[0].length - 1];

		for( int i = 1; i < mat_str.length; i++ )
		{
			for( int j = 0; j < mat_str[i].length - 1 ; j++ )
			{
				ret[ i - 1 ][ j ] = mat_str[ i ][ j ];
			}
		}

		return ret;
	}
}