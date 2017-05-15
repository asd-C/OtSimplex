package com.cdh.otsimplex.etc;

import java.util.ArrayList;

/**
* Esta classe representa um ponto no plano cartesiano
* de duas dimensões (x e y)
*
* @author Dehua Chen
* @author Felipe Belem
*/
public class Ponto
{
	public double x;
	public double y;

	/**
	* Construtor: Instancia um novo ponto com as 
	* coordenadas em parametro
	*
	* @param 	x - Valor do ponto no eixo X
	* @param 	y - Valor do ponto no eixo Y
	*/
	public Ponto( double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	* Obtem os pontos da funcao em parametro.
	* A funcao e escrita da forma f = ax + by.
	*
	* @param 	f - Valor da função no ponto (x,y)
	* @param 	a - Coeficiente da variavel x
	* @param 	b - Coeficiente da variavel y
	* @param 	max_val - Maior valor possível
	*/
	public static ArrayList<Ponto> obterPontos( double f, double a, double b, double max_val )
	{
		// f(x) = ax + by
		ArrayList<Ponto> lista_pontos = new ArrayList<Ponto>();

		if( a == 0.0 )
		{
			double y = f/(double)b;

			lista_pontos.add( new Ponto( 0 , y ) );
			lista_pontos.add( new Ponto( max_val , y ) );
		}
		else if( b == 0.0 )
		{
			double y = f/(double)a;
			

			lista_pontos.add( new Ponto( y , 0 ) );
			lista_pontos.add( new Ponto( y, max_val) );
		}
		else
		{
			double x = ( f )/(double)(a);
			double y = ( f )/(double)(b);
			
			lista_pontos.add( new Ponto( x, 0) );
			lista_pontos.add( new Ponto( 0 , y ) );
		}

		return lista_pontos;
	}
}