package com.cdh.otsimplex.etc;

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
	* @param 	entrada - Matriz de String que contém os coeficientes
	*						e a grandeza de cada linha
	* @return	double[][] que servirá de entrada para o algoritmo
	*			Simplex
	*/
	public static double[][] cnvtSimplex( String[][] entrada )
	{
		int linhas = entrada.length;
		int colunas = entrada[0].length - 1;

		double[][] entSimplex = new double[linhas][colunas];

		for( int i = 0; i < linhas; i++ )
		{	
			int tipo = 1;

			if( entrada[i][colunas].equalsIgnoreCase("min")
				|| entrada[i][colunas].equalsIgnoreCase(">="))
			{
				tipo = -1;
			}

			for( int j = 0; j < colunas; j++ )
			{
				entSimplex[i][j] = Double.parseDouble( entrada[i][j]) * tipo;
			}
		}

		return entSimplex;
	}
}