package com.cdh.otsimplex.detail;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cdh.otsimplex.R;
import com.cdh.otsimplex.etc.Ponto;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailPageFragment extends Fragment {

    // as chaves para recuperar parametros necessarios para construcao de tabela e grafico
    public static final String TABLE = "table";
    public static final String GRAPH = "GRAPH";
    public static final String table_row_size = "table_row_size";
    public static final String number_of_restrictions = "number_of_restrictions";
    public static final String X1 = "X1";
    public static final String X2 = "X2";

    /**
     * Instanciar uma instancia nova da propria classe.
     *
     * @return      nova instancia da classe
     */
    public static Fragment newInstance() {
        return new DetailPageFragment();
    }

    /**
     * Criar a tabela.
     *
     * @param inflater  layoutinflater
     * @param view      view pai (principal)
     * @param bundle    parametros para criar tabela
     */
    private void createTable(LayoutInflater inflater, View view, Bundle bundle) {
        ArrayList<String> elems = bundle.getStringArrayList(TABLE);
        int table_size = bundle.getInt(table_row_size);
        int n_row = elems.size()/table_size;

        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        TableRow row;
        TextView cell;

        for (int i=0; i<n_row; i++) {
            row = (TableRow) inflater.inflate(R.layout.table_row, table, false);
            for (int j = 0; j < table_size; j++) {
                cell = (TextView) inflater.inflate(R.layout.table_row_cell, row, false);
                cell.setText(elems.get(i*table_size+j));
                row.addView(cell);
            }
            table.addView(row);
        }
    }

    /**
     * Criar o grafico.
     *
     * @param view      view pai (principal)
     * @param bundle    parametros para criar grafico
     */
    private void createGraph(View view, Bundle bundle) {
        double maxX = 0, maxY = 0;
        GraphView graph = (GraphView) view.findViewById(R.id.graph_detail);
        double x1 = bundle.getDouble(X1);
        double x2 = bundle.getDouble(X2);
        int size = 0;
        int number_restriction = bundle.getInt(number_of_restrictions);

        ArrayList<String> elems = bundle.getStringArrayList(GRAPH);
        ArrayList<String> elems_critical = new ArrayList<>();
        int critical_size;

        if (number_restriction == 0) return;
        size = elems.size()/number_restriction;

        double tmpX, tmpY, tmpF;

        ArrayList<Ponto> pontos = new ArrayList<>();
        double maxX1 = Double.NEGATIVE_INFINITY;
        double maxX2 = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < number_restriction; i++) {
            tmpF = Double.parseDouble(elems.get(i*size + 0));
            tmpX = Double.parseDouble(elems.get(i*size + 1));
            tmpY = Double.parseDouble(elems.get(i*size + 2));

            ArrayList<Ponto> pts = Ponto.obterPontos(tmpF, tmpX, tmpY, Double.NEGATIVE_INFINITY);

            for (Ponto p: pts) {
                if (p.x > maxX1) maxX1 = p.x;
                if (p.y > maxX2) maxX2 = p.y;

                pontos.add(p);
            }
        }

        for (Ponto p: pontos) {
            if (p.x == Double.NEGATIVE_INFINITY) p.x = maxX1;
            if (p.y == Double.NEGATIVE_INFINITY) p.y = maxX2;
        }

        while (!pontos.isEmpty()) {
            Ponto p1 = pontos.remove(0);
            Ponto p2 = pontos.remove(0);

            Ponto tmp;
            if (p1.x < p2.x) {
                tmp = p1;
                p1 = p2;
                p2 = tmp;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(p2.x, p2.y),
                    new DataPoint(p1.x, p1.y)
            });
            graph.addSeries(series);
        }

        // desenhar as restricoes.
//        for (int i = 0; i < number_restriction; i++) {
//            tmpF = Double.parseDouble(elems.get(i*size + 0));
//            tmpX = Double.parseDouble(elems.get(i*size + 1));
//            tmpY = Double.parseDouble(elems.get(i*size + 2));
//
//            if (tmpX == 0 || tmpY == 0) {
//                elems_critical.add(elems.get(i*size + 0));
//                elems_critical.add(elems.get(i*size + 1));
//                elems_critical.add(elems.get(i*size + 2));
//                elems_critical.add(elems.get(i*size + 3));
////                continue;
//            }
//
//            ArrayList<Ponto> ps = Ponto.obterPontos(tmpF, tmpX, tmpY, 100);
//
//            if (maxX < ps.get(ps.size()-1).x) maxX = ps.get(ps.size()-1).x;
//            if (maxX < ps.get(0).x) maxX = ps.get(0).x;
//            if (maxY < ps.get(ps.size()-1).y) maxY = ps.get(ps.size()-1).y;
//            if (maxY < ps.get(0).y) maxY = ps.get(0).y;
//
//            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                    new DataPoint(ps.get(ps.size()-1).x, ps.get(ps.size()-1).y),
//                    new DataPoint(ps.get(0).x, ps.get(0).y)
//            });
//            graph.addSeries(series);
//        }
//
//        critical_size = elems_critical.size()/size;
//
//        double max = (maxX > maxY) ? maxX : maxY;
//
//        for (int i = 0; i < critical_size; i++) {
//            tmpF = Double.parseDouble(elems_critical.get(i*size + 0));
//            tmpX = Double.parseDouble(elems_critical.get(i*size + 1));
//            tmpY = Double.parseDouble(elems_critical.get(i*size + 2));
//
//            ArrayList<Ponto> ps = Ponto.obterPontos(tmpF, tmpX, tmpY, max);
//
//            if (max < ps.get(ps.size()-1).x) max = ps.get(ps.size()-1).x;
//            if (max < ps.get(0).x) max = ps.get(0).x;
//            if (max < ps.get(ps.size()-1).y) max = ps.get(ps.size()-1).y;
//            if (max < ps.get(0).y) max = ps.get(0).y;
//
//            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                    new DataPoint(ps.get(ps.size()-1).x, ps.get(ps.size()-1).y),
//                    new DataPoint(ps.get(0).x, ps.get(0).y)
//            });
//            graph.addSeries(series);
//        }

        // desenhar a solucao atual (parcial ou global)
        PointsGraphSeries<DataPoint> current_point = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(x1, x2)
        });
        current_point.setColor(Color.RED);

        graph.addSeries(current_point);
        graph.getViewport().setMaxX(maxX1);
        graph.getViewport().setMaxY(maxX2);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    /**
     * Criar o view principal.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_page, container, false);

        Bundle bundle = getArguments();

        createTable(inflater, view, bundle);
        createGraph(view, bundle);

        return view;
    }

}
