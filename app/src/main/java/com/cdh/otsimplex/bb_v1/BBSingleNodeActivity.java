package com.cdh.otsimplex.bb_v1;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cdh.otsimplex.R;
import com.cdh.otsimplex.bb.BBData;
import com.cdh.otsimplex.branch.BranchBound;
import com.cdh.otsimplex.branch.No;
import com.cdh.otsimplex.detail.DetailActivity;

import java.util.ArrayList;

import static com.cdh.otsimplex.MainActivity.SIZE;

public class BBSingleNodeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button x1_left, x1_right, x2_left, x2_right;
    private Button centerNode;
    private Button x_parent;

    private No current_no;

    private Drawable imp, cand, best, empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbsingle_node);

        current_no = BBData.root;

        getDrawables();

        getAllViews();
        setListener();
        updateViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getDrawables() {
        imp = getDrawable(R.drawable.node_imp_bbsinglenode);
        cand = getDrawable(R.drawable.node_cand_bbsinglenode);
        best = getDrawable(R.drawable.node_best_bbsinglenode);
        empty = getDrawable(R.drawable.node_bbsinglenode);
    }

    private void changeCenterNodeTS() {
        if (current_no.obtTS() == BranchBound.IMPOSS) {
            centerNode.setBackground(imp);
        } else if (current_no.obtTS() == BranchBound.CAND) {
            centerNode.setBackground(cand);
        } else if (current_no.obtTS() == BranchBound.OTIMO) {
            centerNode.setBackground(best);
        } else {
            centerNode.setBackground(empty);
        }
    }

    public void updateViews() {

        if (current_no != null) {

            // set content
            changeCenterNodeTS();
            centerNode.setText(current_no.toString());

            // set parent
            if (current_no.obtPai() == null) {
                x_parent.setVisibility(View.INVISIBLE);
            } else {
                x_parent.setVisibility(View.VISIBLE);
            }

            // set children
            No child;
            if ((child = current_no.obtNo(1, No.ESQ)) == null) {
                x1_left.setVisibility(View.INVISIBLE);
            } else {
                x1_left.setVisibility(View.VISIBLE);
                x1_left.setText(child.toStrUltRes());
            }
            if ((child = current_no.obtNo(1, No.DIR)) == null) {
                x1_right.setVisibility(View.INVISIBLE);
            } else {
                x1_right.setVisibility(View.VISIBLE);
                x1_right.setText(child.toStrUltRes());
            }
            if ((child = current_no.obtNo(2, No.ESQ)) == null) {
                x2_left.setVisibility(View.INVISIBLE);
            } else {
                x2_left.setVisibility(View.VISIBLE);
                x2_left.setText(child.toStrUltRes());
            }
            if ((child = current_no.obtNo(2, No.DIR)) == null) {
                x2_right.setVisibility(View.INVISIBLE);
            } else {
                x2_right.setVisibility(View.VISIBLE);
                x2_right.setText(child.toStrUltRes());
            }
        }
    }

    private void getAllViews() {

        x1_left = (Button) findViewById(R.id.x1_left);
        x2_left = (Button) findViewById(R.id.x2_left);
        x1_right = (Button) findViewById(R.id.x1_right);
        x2_right = (Button) findViewById(R.id.x2_right);

        x_parent = (Button) findViewById(R.id.x_parent);

        centerNode = (Button) findViewById(R.id.centerNode);

    }

    private void setListener() {

        x1_left.setOnClickListener(this);
        x1_right.setOnClickListener(this);
        x2_left.setOnClickListener(this);
        x2_right.setOnClickListener(this);

        x_parent.setOnClickListener(this);

        centerNode.setOnClickListener(this);

    }

    private void startDetail() {
//        step_by_step(BBData.createBundles(current_no.obtSimSCS(), );
    }

    private void step_by_step(ArrayList<Bundle> bundles) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(SIZE, bundles.size());
        for (int i = 0; i < bundles.size(); i++) {
            intent.putExtra(Integer.toString(i), bundles.get(i));
        }
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.x1_left:
                current_no = current_no.obtNo(1, No.ESQ);
                break;
            case R.id.x1_right:
                current_no = current_no.obtNo(1, No.DIR);
                break;
            case R.id.x2_left:
                current_no = current_no.obtNo(2, No.ESQ);
                break;
            case R.id.x2_right:
                current_no = current_no.obtNo(2, No.DIR);
                break;
            case R.id.x_parent:
                current_no = current_no.obtPai();
                break;
            case R.id.centerNode:
                startDetail();
                break;
            default:
                break;
        }

        updateViews();
    }
}
