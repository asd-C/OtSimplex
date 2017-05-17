package com.cdh.otsimplex;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.cdh.otsimplex.bb.BBActivity;
import com.cdh.otsimplex.bb.BBData;
import com.cdh.otsimplex.branch.BranchBound;
import com.cdh.otsimplex.detail.DetailActivity;
import com.cdh.otsimplex.detail.DetailPageFragment;
import com.cdh.otsimplex.simplex.Matriz;
import com.cdh.otsimplex.simplex.Simplex;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // elementos do Layout atual
    private Button btn_clear, btn_simplex, btn_sbs;
    private RadioGroup radioGroup;
    private ImageButton addRest, btn_remove;
    private LinearLayout main_layout;
    private EditText function_x1, function_x2;
    private ArrayList<LinearLayout> contraints;
    private TextView tv_output;

    // parametros para gerar tabela e grafico
    private ArrayList<Bundle> bundles;

    // quantidade de bundles (passos intermediarios)
    public static final String SIZE = "size";

    /**
     * Inicializando atributos necessarios.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // criar um array de restricoes
        contraints = new ArrayList<>();

        // criar um array de bundles que serao utilizados para
        // parametros para gerar tabela e graficos.
        bundles = new ArrayList<>();

        // recuperar referencias das entidades view
        getComponents();

        // setar Listener para detectar eventos
        setListener();
    }

    /**
     * Setar listeners para detectar eventos.
     */
    private void setListener() {
        btn_clear.setOnClickListener(this);
        btn_simplex.setOnClickListener(this);
        addRest.setOnClickListener(this);
        btn_sbs.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }

    /**
     * Recuperar referencias das entidades view.
     */
    private void getComponents() {
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        addRest = (ImageButton) findViewById(R.id.addRest);
        btn_simplex = (Button) findViewById(R.id.btn_simplex);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        btn_sbs = (Button) findViewById(R.id.btn_sbs);
        btn_remove = (ImageButton) findViewById(R.id.remove);
        tv_output = (TextView) findViewById(R.id.tv_output);

        function_x1 = (EditText) findViewById(R.id.function_x1);
        function_x2 = (EditText) findViewById(R.id.function_x2);
    }

    /**
     * O metodo que vai criar os parametros para gerar tabela e grafico.
     *
     *@param matrizs    matrizes de tabela intermediarias
     *@param constraits matriz dos coeficientes das restricoes
     */
    private void createBundles(ArrayList<Matriz> matrizs, String[][] constraits) {
        // limpar os parametros da ultima operacao
        bundles.clear();

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
    }

    /**
     * O metodo principal de simplex, basicamente resolve o problema e recupera
     * alguns resultados necessarios
     */
    private void simplex() {
        String[][] inputs = getAllInput();
        String result;

        Simplex s = new Simplex(new Matriz(inputs));
        result = s.imprimirSol();
        ArrayList<Matriz> matrizs = s.obtMatSCS();

        createBundles(matrizs, getConstraints(inputs));

        // mostrar resultado no textview
        tv_output.setText(result);
    }

    /**
     * A partir do problema, extrai as restricoes.
     * Basicamente tirando a primeira linha de funcao objetiva,
     * o restante e restricoes.
     *
     * @return      matriz de restricoes
     * @param input represtacao do problema na forma de matriz
     */
    private String[][] getConstraints(String[][] input) {
        int row = input.length;
        int col = input[0].length;
        String[][] result = new String[row-1][col];

        for (int i = 1; i < row; i++) {
            for (int j = 0; j < col; j++) {
                result[i-1][j] = input[i][j];
            }
        }
        return result;
    }

    // tipo de restricao
    private String opr;

    /**
     * Adicionar uma restricao, que tera que ser preenchida pelo usuario.
     */
    private void addRestriction() {
        opr = getString(R.string.equal);
        final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        final LinearLayout _spinner = (LinearLayout) inflater.inflate(R.layout.spinner, null);
        final Spinner spinner = (Spinner) _spinner.findViewById(R.id.spinner);

        new AlertDialog.Builder(this)
                .setTitle("Tipo de Restrição")
                .setView(_spinner)
                .setCancelable(false)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNewContraint(spinner, inflater);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Renderizar e adicionar o view de restricao na tela principal.
     */
    private void createNewContraint(Spinner spinner, LayoutInflater inflater) {
        opr = spinner.getSelectedItem().toString();
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.constraints, main_layout, false);
        ((TextView) view.findViewById(R.id.operation)).setText(opr);
        contraints.add(view);
        main_layout.addView(view);
    }

    /**
     * Recuperar todos inputs.
     *
     * @return  representacao do problema na forma de matriz
     */
    private String[][] getAllInput() {
        int n_constraints = contraints.size();
        String[][] resp = new String[n_constraints+1][4];
        LinearLayout ll;

        resp[0][0] = "0";
        resp[0][1] = clearText(function_x1.getText().toString());
        resp[0][2] = clearText(function_x2.getText().toString());

        if (R.id.max == radioGroup.getCheckedRadioButtonId()) {
            resp[0][3] = "Max".toLowerCase();
        } else if (R.id.min == radioGroup.getCheckedRadioButtonId()) {
            resp[0][3] = "Min".toLowerCase();
        }

        for (int i = 0; i < n_constraints; i++) {
            ll = contraints.get(i);
            resp[i+1][0] = clearText(((EditText) ll.findViewById(R.id.value)).getText().toString());
            resp[i+1][1] = clearText(((EditText) ll.findViewById(R.id.function_x1)).getText().toString());
            resp[i+1][2] = clearText(((EditText) ll.findViewById(R.id.function_x2)).getText().toString());
            resp[i+1][3] = clearText(((TextView) ll.findViewById(R.id.operation)).getText().toString());
        }

        return resp;
    }

    /**
     * Limpar input, remover espaco substituir , por .
     *
     * @return      input limpo
     * @param str   input nao processado.
     */
    private String clearText(String str) {
        return str.toString().toLowerCase().replace(",", ".").trim().replace(" ", "");
    }

    /**
     * Remover ultima restricao.
     */
    private void removeLast() {
        if (contraints.size() > 0)
            main_layout.removeView(contraints.remove(contraints.size()-1));
        else
            infobox("Nao ha restricao para excluir!");
    }

    /**
     * Iniciar activity para mostrar passo a passo.
     */
    private void step_by_step() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(SIZE, bundles.size());
        for (int i = 0; i < bundles.size(); i++) {
            intent.putExtra(Integer.toString(i), bundles.get(i));
        }
        startActivity(intent);
    }

    /**
     * Limpar todos inputs, e remove as restricoes.
     */
    private void clear() {
        function_x1.setText("");
        function_x2.setText("");

        while (!contraints.isEmpty())
            main_layout.removeView(contraints.remove(contraints.size()-1));
    }

    /**
     * Esconde teclado apos o usuario clicar no simplex.
     */
    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    /**
     * Checagem antes de executar simplex.
     * Verificar se todas condicoes estao presentes.
     *
     * @return      pronto para executar simplex ou nao
     */
    private boolean verifyInputs() {
        int n_constraints = contraints.size();
        LinearLayout ll;
        String tmp;

        tmp = function_x1.getText().toString();
        if (tmp.isEmpty() || tmp.equals(".")) return false;

        tmp = function_x2.getText().toString();
        if (tmp.isEmpty() || tmp.equals(".")) return false;

        if ((R.id.max != radioGroup.getCheckedRadioButtonId())
                && R.id.min != radioGroup.getCheckedRadioButtonId()) return false;

        for (int i = 0; i < n_constraints; i++) {
            ll = contraints.get(i);
            tmp = clearText(((EditText) ll.findViewById(R.id.value)).getText().toString());
            if (tmp.isEmpty() || tmp.equals(".")) return false;
            tmp = clearText(((EditText) ll.findViewById(R.id.function_x1)).getText().toString());
            if (tmp.isEmpty() || tmp.equals(".")) return false;
            tmp = clearText(((EditText) ll.findViewById(R.id.function_x2)).getText().toString());
            if (tmp.isEmpty() || tmp.equals(".")) return false;

            String t1 = clearText(((EditText) ll.findViewById(R.id.function_x1)).getText().toString()),
                    t2 = clearText(((EditText) ll.findViewById(R.id.function_x2)).getText().toString());
            if (Double.parseDouble(t1) == 0 && Double.parseDouble(t2) == 0)
                return false;
        }

        return true;
    }

    /**
     * Listener, designar tarefas para metodos correspondentes.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_simplex:
                if (verifyInputs()) {
                    simplex();
                } else {
                    infobox("Erro! Verifique seus dados!");
                }
                break;

            case R.id.btn_clear:
                clear();
                break;

            case R.id.addRest:
                addRestriction();
                break;

            case R.id.remove:
                removeLast();
                break;

            case R.id.btn_sbs:
                if (bundles.size() == 0) {
                    infobox("Nao ha tabela para mostrar!");
                } else {
                    step_by_step();
                }
                break;

            default:
                break;
        }
        dismissKeyboard(this);
    }

    public void branchbound() {
        String[][] inputs = getAllInput();
        String result;

        BranchBound bb = new BranchBound(new Matriz(inputs), inputs[0][3]);
        result = bb.toStrSolMsg();
        BBData.root = bb.obtRaiz();

//        infobox(result);
    }

    public void bb(View v) {
        if (verifyInputs()) {
            branchbound();
            startActivity(new Intent(this, BBActivity.class));
        } else {
            infobox("Erro! Verifique seus dados!");
        }
        dismissKeyboard(this);
    }

    private void infobox(String info) {
        tv_output.setText(info);
    }
}
