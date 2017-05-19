package com.cdh.otsimplex.bb;

import android.os.Bundle;

import com.cdh.otsimplex.branch.BranchBound;
import com.cdh.otsimplex.branch.No;
import com.cdh.otsimplex.detail.DetailPageFragment;
import com.cdh.otsimplex.simplex.Matriz;

import java.util.ArrayList;

/**
 * Created by chendehua on 2017/5/16.
 */

public class BBData {
    public static BranchBound bb;
    public static No root;

    public static ArrayList<Bundle> createBundles(ArrayList<Matriz> matrizs, String[][] constraits) {
        // limpar os parametros da ultima operacao
        ArrayList<Bundle> bundles = new ArrayList<>();

        Bundle bundle;

        // para cada tabela intermediaria, extrai e construi os parametros
        for (int i = 0; i < matrizs.size(); i++) {
            bundle = new Bundle();

            // Obter o melhor ponto(x1, x2) temporal(parcial) e passar pra fragment
            // que vai desenhar grafico
            bundle.putDouble(DetailPageFragment.X1, matrizs.get(i).obtX_(1).doubleValue());
            bundle.putDouble(DetailPageFragment.X2, matrizs.get(i).obtX_(2).doubleValue());

            // Obter a tabela com todos elementos superiores
            String[][] matrizElems = matrizs.get(i).toStringSCS();

            // Converter para ArrayList<String> para inserir no bundle
            ArrayList<String> table = new ArrayList<>();
            for (String[] strs: matrizElems) {
                for (String str: strs) {
                    table.add(str);
                }
            }

            // Converte as restricoes para poder desenhar o grafico
            ArrayList<String> graph = new ArrayList<>();
            for (String[] strs: constraits) {
                for (String str: strs) {
                    graph.add(str);
                }
            }

            // Passar o tamanho da tabela pra fragment que vai desenhar tabela
            bundle.putInt(DetailPageFragment.table_row_size, matrizElems[0].length);

            // Passar a tabela pra fragment que vai desenhar tabela
            bundle.putStringArrayList(DetailPageFragment.TABLE, table);

            // Passar para fragment a quantidade de restricoes
            bundle.putInt(DetailPageFragment.number_of_restrictions, constraits.length);

            // Passar as restricoes para desenhar o grafico
            bundle.putStringArrayList(DetailPageFragment.GRAPH, graph);
            bundles.add(bundle);
        }

        return bundles;
    }
}
