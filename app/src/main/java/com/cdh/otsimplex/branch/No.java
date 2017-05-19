package com.cdh.otsimplex.branch;

import com.cdh.otsimplex.etc.Cnvt;
import com.cdh.otsimplex.simplex.Matriz;
import com.cdh.otsimplex.simplex.Simplex;

import java.util.ArrayList;

/**
* Esta classe representa um no qualquer da
* arvore Branch and bound. Armazena um identificador,
* o seu codigo de solucao (ts), seus filhos, os resultados
* de seu simplex e a ultima restricao adicionada em direcao
* a solucao.
*
* @author Dehua Chen
* @author Felipe Belem
*/
public class No
{
	// Numero de Variaveis
	private static final int NUM_VARS = 2;
	// Numero de Grandezas: <= e >=
	private static final int NUM_GRAND = 2;
	// Lado esquerdo do no
	public static final int ESQ = 0;
	// Lado direito do no
	public static final int DIR = 1;
	
	// Identificador do no (raiz == 0)
	private int id;
	// Codigo da solucao do no:
	// 0 - Nulo
	// 1 - Impossivel
	// 2 - Solucao otima
	// 3 - Solucao pior que a otima
	private int ts;
	// Nos Filhos
	private No[][] filhos;
	// Simplex
	private Simplex simplex;
	// Ultima restricao adicionada
	private String[] ultimaRes;

	private int nivel;

	private No pai; //Referencia ao no pai

    public No( No pai, Matriz mat, String[] novaRes, int id , int nivel )
    {//			^-- Esse parametro foi adicionado
        ultimaRes = novaRes;
        this.pai = pai; // <--------------------- Essa linha foi adicionada
        Matriz novaMat = new Matriz( mat, novaRes );
        simplex = new Simplex( novaMat );

        this.nivel = nivel;
        ts = 0;
        this.id = id;

        filhos = new No[NUM_VARS][NUM_GRAND];

        for( int i = 0; i < NUM_VARS; i++ )
        {
            for( int j = 0; j < NUM_GRAND; j++ )
            {
                filhos[i][j] = null;
            }
        }
    }
	/**
	* Construtor: Constroi um novo no a partir da matriz
	* inicial do pai, sendo acrescido de uma nova restricao,
	* e definindo seu identificador, passado pelo pai, pelo
	* valor em parametro.
	*
	* @param 	mat - Matriz inicial do pai
	* @param 	novaRes - Restricao a ser adicionada por este
	* @param 	id - Identificador deste no
	*/
	public No(Matriz mat, String[] novaRes, int id, int nivel )
	{
		ultimaRes = novaRes;

		Matriz novaMat = new Matriz( mat, novaRes );
		simplex = new Simplex( novaMat );

		this.nivel = nivel;
		ts = 0;
		this.id = id;

		filhos = new No[NUM_VARS][NUM_GRAND];

		for( int i = 0; i < NUM_VARS; i++ )
		{
			for( int j = 0; j < NUM_GRAND; j++ )
			{
				filhos[i][j] = null;
			}
		}
	}

	/**
	* Construtor: Constroi um no raiz a partir da matriz
	* inicial do problema. Seu identificador sera 0.
	*
	* @param 	mat - Matriz inicial do problema
	*/
    public No( Matriz mat )
    {
        ultimaRes = null;

        pai = null;
        simplex = new Simplex( mat );
        nivel = 0;
        id = 0;

        filhos = new No[NUM_VARS][NUM_GRAND];

        for( int i = 0; i < NUM_VARS; i++ )
        {
            for( int j = 0; j < NUM_GRAND; j++ )
            {
                filhos[i][j] = null;
            }
        }
    }

	public int obtNivel()
	{
		return nivel;
	}

	/**
	* Obtem o codigo TS deste no
	*
	* @return int com o código TS deste no
	*/
	public int obtTS()
	{
		return ts;
	}

	/**
	* Obtem o identificador deste no
	*
	* @return 	Identificador(int) do no
	*/
	public int obtID()
	{
		return id;
	}

	/**
	* Define o codigo TS deste no
	*
	* @param 	cod - Código TS deste no
	*/
	public void defTSCod( int cod )
	{
		ts = cod;
	}

	/**
	* Obtem o codigo de solucao do Simplex
	* deste no
	*
	* @return Codigo (int) de solucao do simplex
	*/
	public int obtCodSim()
	{
		return simplex.obtCodSol();
	}


	/**
	* Obtem a representacao, em String, da
	* ultima restricao adicionada. Isto so e
	* valido para nos que nao sao raiz, pois
	* esta nao adicionou nenhuma restricao a
	* mais se comparado a matriz original do
	* problema.
	*
	* @return Representacao (String) da ultima restricao
	*			adicionada.
	*/
	public String toStrUltRes()
	{
		String res = "";
		
		if( id > 0 )
		{
			res = Cnvt.cnvtRes( ultimaRes );
		}

		return res;
	}

	/**
	* Obtem as matrizes da solucao do Simplex
	*
	* @return Lista (ArrayList<Matriz>) das matrizes
	*			criadas para a solucao do Simplex
	*/
	public ArrayList<Matriz> obtSimSCS()
	{
		return simplex.obtMatSCS();
	}

	/**
	* Obtem a matriz que contem a solucao obtida
	* pelo Simplex
	*
	* @return Matriz que contem a solucao do Simplex
	*/
	public Matriz obtSolMat()
	{
		ArrayList<Matriz> scs = obtSimSCS();

		return scs.get( scs.size() -1 );
	}

	/**
	* Obtem a matriz originaria do resultado do Simplex
	*
	* @return Matriz origem a solucao do Simplex
	*/
	public Matriz obtSrcMat()
	{
		ArrayList<Matriz> scs = obtSimSCS();

		return scs.get( 0 );
	}

	/**
	* Obtem o no com a variavel id, em parametro,
	* do lado informado: ESQ ou DIR;
	*
	* @param 	id - Identificador da Variavel
	* @param 	lado - Lado:Grandeza (ESQ:<=
	*			e DIR:>= ), se existir; null, caso
	*			contrário.
	*/
	public No obtNo( int id , int lado )
	{
		return filhos[id-1][lado];
	}

	/**
	* Retorna todos os filhos deste no
	*
	* @return Array de nos (filhos)
	*/
	public No[][] obtFilhos()
	{
		return filhos;
	}

	/**
	* Define o no com a variavel id, em parametro,
	* do lado informado: ESQ ou DIR;
	*
	* @param 	id - Identificador da Variavel
	* @param 	lado - Lado:Grandeza (ESQ:<=
	*			e DIR:>= ), se existir; null, caso
	*			contrário.
	* @param 	mat - Matriz de origem
	* @param 	novaRes - Nova restricao
	* @param 	idNo - Identificador do No
	*/
    public void defNo( int id , int lado, No pai, Matriz mat , String[] novaRes , int idNo )
    {								//v-----^-------- Foram adicionados ao metodo
        filhos[id-1][lado] = new No( pai, mat , novaRes , idNo , nivel + 1 );
    }

    public No obtPai()
    {
        return pai;
    }

    /**
	* Cria uma representacao em String deste no
	*
	* @return Representacao (String) deste no
	*/
	public String toString()
	{
		String ret = "";
		if( ts != 1 )
		{
			Matriz sol = obtSolMat();

			ret += "No " + id + "\n";
			ret += String.format( "Z = %.3f\n", sol.obtZ() );
			ret += String.format( "X1 = %.3f\n", sol.obtX_(1) );
			ret += String.format( "X2 = %.3f\n", sol.obtX_(2) );

			if( ts != 0 )
			{
				ret += "--- TS" + ts + " ---\n";
			}
		} else {
            ret += "SOLUCAO IMPOSSIVEL";
        }

		return ret;
	}
}