/**
* Esta classe representa a matriz utilizada
* pelo algoritmo Simplex. Possui VB x VNB celulas
* onde VB e o numero de variaveis basicas e VNB,
* o numero de variaveis nao basicas. Dadas as
* equacoes, ele ira gerar a matriz a partir de seus
* coeficientes.
*
* @author Dehua Chen
* @author Felipe Belem
*/
package com.cdh.otsimplex.simplex;

public class Matriz {
	// Codigo para identificar que o indice nao existe
	public static final int NAO_EXISTE = -1;

	// Quantidade de Variaveis Basicas
	private int qtd_vb;
	// Quantidade de Variaveis Nao Basicas
	private int qtd_vnb;
	// Linha Permissiva e Coluna Permissiva
	private int lp,cp;
	// Variavel Basica com Membro Livre Negativo
	private int vbmlneg;
	// Variavel Nao Basica com f(x) positivo
	private int vnbfxpos;
	// IDs das variaveis que compoem as
	// variaveis basicas
	private int[] vb_ids;
	// IDs das variaveis que compoem as 
	// variaveis nao basicas
	private int[] vnb_ids;
	// Matriz de sub celulas superiores
	private double[][] scs;
	// Matriz de sub celulas inferiores
	private double[][] sci;
	
	/**
	* Construtor: Este clona a matriz em parametro, para se
	* tornar sua matriz de SCS. Este construtor se considera
	* como a primeira matriz da solucao.
	*
	* @param 	mat - Matriz de SCS
	*/
	public Matriz( double[][] mat ) {
		qtd_vb = mat.length;
		qtd_vnb = mat[0].length;

		vb_ids = new int[qtd_vb];
		vnb_ids = new int[ qtd_vnb ];

		scs = new double[ qtd_vb ][ qtd_vnb ];
		sci = new double[ qtd_vb ][ qtd_vnb ];
		
		for( int i = 0; i < qtd_vb; i++ ) {
			vb_ids[ i ] = ( i - 1 ) + qtd_vnb;

			for( int j = 0; j < qtd_vnb; j++ ) {
				scs[i][j] = mat[i][j];
				sci[i][j] = 0.0;
			}
		}

		for( int j = 0; j < qtd_vnb; j++ ) { vnb_ids[ j ] = j; }

		vnb_ids[0] = 0;
		vb_ids[0] = 0;

		lp = -1;
		cp = -1;
		vbmlneg = -1;
		vnbfxpos = -1;
	}

	/**
	* Construtor: Este clona a matriz em parametro, para se
	* tornar sua matriz de SCS, e copia o mapeamento de ids
	* das variaveis basicas e nao basicas
	*
	* @param 	mat - Matriz de SCS
	* @param 	vnb_ids - Identificadores das VNBs correntes
	* @param 	vnb_ids - Identificadores das VBs correntes
	*/
	public Matriz( double[][] mat, int[] vnb_ids, int[] vb_ids ) {
		this( mat );

		for( int i = 0; i < qtd_vb; i++ ) { this.vb_ids[i] = vb_ids[i]; }
		for( int j = 0; j < qtd_vnb; j++ ) { this.vnb_ids[j] = vnb_ids[j]; }
	}

	/**
	* Clona, parcialmente, esta matriz
	*
	* @return Clone parcial desta Matriz
	*/
	public Matriz clone() {
		return new Matriz( scs , vnb_ids , vb_ids );
	}

	/**
	* Obtem a variavel basica cujo
	* membro livre e negativo.
	*
	* @return	-1: Caso não exista tal VB;
	*			indice: Indice da VB;
	*/
	public int obtVBMLNeg() {
		int indice = NAO_EXISTE;

		// Procura a VB com ML negativo
		for( int i = 1; i < qtd_vb && indice == -1; i++ ) {
			if( scs[i][0] < 0 ) { indice = i;}
		}

		vbmlneg = indice;

		return indice;
	}

	/**
	* Obtem a variavel nao basica cujo
	* f(x) e positivo.
	*
	* @return	-1: Caso não exista tal VNB;
	*			indice: Indice da VNB;
	*/
	public int obtVNBFXPos() {
		int indice = NAO_EXISTE;

		// Procura a VNB positiva
		for( int j = 1; j < qtd_vnb && indice == -1; j++ ) {
			if( scs[0][j] > 0 ) { indice = j; }
		}

		vnbfxpos = indice;

		return indice;
	}

	/**
	* Obtem a coluna permissiva considerando que
	* a variavel basica com membro livre negativo
	* ja tenha sido previamente encontrada. 
	*
	* @return	-1: Caso não exista CP;
	*			indice: Indice da CP;
	*/
	public int obtCPNeg() {
		int indice = NAO_EXISTE;

		// Procura o elemento negativo
		for( int j = 1; j < qtd_vnb && indice == -1; j++ ) {
			if( scs[vbmlneg][j] < 0 ) { indice = j; }
		}

		cp = indice;

		return indice;
	}

	/**
	* Verifica se existe uma variavel basica
	* cuja variavel nao basica (com f(x) positivo),
	* possuinte de um valor positivo. Esta considera
	* que a VNB com f(x) positivo ja tenha sido
	* previamente encontrada.
	*
	* @return	-1: Caso não exista tal VB;
	*			indice: Indice da VB;
	*/
	public int obtVBPos() {
		int indice = NAO_EXISTE;

		cp = vnbfxpos;

		// Procura o elemento positivo
		for( int i = 1; i < qtd_vb && indice == -1; i++ ) {
			if( scs[i][cp] > 0 ) { indice = i;}
		}

		return indice;
	}

	/**
	* Obtem a linha permissiva considerando que
	* a coluna permissiva já tenha sido previamente
	* encontrada ( e existe ).
	*
	* @return	-1: Caso não exista LP;
	*			indice: Indice da LP;
	*/
	public int obtLP() {
		int indice = NAO_EXISTE;
		double menor = Double.MAX_VALUE;

		// Procura o menor quociente
		for( int i = 1; i < qtd_vb; i++ ) {
			double val = scs[i][0] / scs[i][cp];

			if( val > 0.0 && val < menor ) { menor = val; indice = i; }
		}

		lp = indice;

		return indice;
	}

	/**
	* Realiza o algoritmo de troca considerando que
	* a linha e a coluna permitida foram encontradas
	* e selecionadas previamente.
	*
	* @return	Matriz: A nova matriz de SCS apos a troca
	*/
	public Matriz algTroca() {
		double[][] nova_scs = new double[qtd_vb][qtd_vnb];

		// SCI do EP
		double ep_inv = 1/scs[lp][cp];
		sci[lp][cp] = ep_inv;

		// Multiplica a SCS da linha pela SCI do EP
		for( int j = 0; j < qtd_vnb; j++ ) {
			if( j != cp ) { sci[lp][j] = scs[lp][j] * ep_inv; }
		}

		// Multiplica a SCS da coluna por -SCI do EP
		for( int i = 0; i < qtd_vb; i++ ) {
			if( i != lp ) { sci[i][cp] = scs[i][cp] * (-ep_inv); }
		}

		// Multiplicar a SCS marcada em sua respectiva coluna
		// com a SCI marcada de sua respectiva linha
		for( int i = 0; i < qtd_vb; i++ ) {
			if( i != lp ) {
				for( int j = 0; j < qtd_vnb; j++ ) {
					if( j != cp ) {
						sci[i][j] = scs[lp][j] * sci[i][cp];
					}
				}
			}
		}

		// Troca-se o id da LP pelo CP
		int aux = vnb_ids[cp];
		vnb_ids[cp] = vb_ids[lp];
		vb_ids[lp] = aux;

		// Todas as SCI de LP e CP sao copiadas para suas
		// respectivas SCS na nova tabela
		for( int j = 0; j < qtd_vnb; j++ ) {
			nova_scs[lp][j] = sci[lp][j];
		}
		for( int i = 0; i < qtd_vb; i++ ) {
			nova_scs[i][cp] = sci[i][cp];
		}

		// Somam-se as SCI com as SCS das demais celulas
		// restantes e copiadas para a nova tabela
		for( int i = 0; i < qtd_vb; i++ ) {
			if( i != lp ) {
				for( int j = 0; j < qtd_vnb; j++ ) {
					if( j != cp ) {
						nova_scs[i][j] = scs[i][j] + sci[i][j];
					}
				}
			}
		}

		return new Matriz( nova_scs , vnb_ids, vb_ids );
	}
	
	/**
	* Obtem o valor atual da variavel com o identificador
	* em parametro
	*
	* @return	0, se nao constar como variavel basica na
	*			atual matriz de SCS; ou double, caso conste
	*/
	public double obtX_( int id )
	{
		double x_ = 0.0;

		for( int i = 0; i < vb_ids.length; i++ )
		{
			if( vb_ids[i] == id )
			{
				x_ = scs[ i ][0];
				break;
			}
		}

		return x_;
	}

	/**
	* Converte a matriz de subcelulas superiores em uma
	* matriz de String
	*/
	public String[][] toStringSCS() {

		String[][] str = new String[qtd_vb + 1][qtd_vnb + 1];
		
		str[0][0] = "-";
		str[0][1] = "ML";
		str[1][0] = "f(x)";

		// Escreve as variaveis nao basicas
		for( int i = 1; i < qtd_vnb; i++ ) {
			str[0][i+1] = "x_" + vnb_ids[i];
		}

		//Escreve as variaveis basicas
		for( int i = 1; i < qtd_vb; i++ ) {
			str[i+1][0] = "x_" + vb_ids[i];
		}

		// Escreve os coeficientes
		for( int i = 0; i < qtd_vb; i++ ) {
			for( int j = 0; j < qtd_vnb; j++ ) {
				str[i+1][j+1] = String.format( "%.3f",scs[i][j] );
			}
		}

		return str;
	}
}