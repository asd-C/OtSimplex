package com.cdh.otsimplex.bb;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cdh.otsimplex.R;

public class BBActivity extends AppCompatActivity {

    private NetworkGraphView networkGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bb);

        networkGraphView = (NetworkGraphView) findViewById(R.id.networkGraph);
        System.out.println(BBData.root.toString());
    }

    public void update(View v) {
        networkGraphView.updateData();
    }
}
