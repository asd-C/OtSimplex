package com.cdh.otsimplex.bb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cdh.otsimplex.bb.entity.Point;
import com.cdh.otsimplex.branch.BranchBound;
import com.cdh.otsimplex.branch.No;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Created by chendehua on 2017/5/15.
 */

public class NetworkGraphView extends View {

    private Paint paint;
    private static float radius = 120;
    private static float spaceV = radius * 3;
    private static float spaceH = radius * 2.5f;
    private static float textSize = radius * 0.05f;

    public NetworkGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
    }

    public void updateData() {

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);

        Point p1 = new Point();
        p1.x = radius;
        p1.y = radius;
        No no = BBData.root;

        if (no != null) {
            drawNode(canvas, p1, no);
            recursiveDraw(no, p1, canvas);
        }
    }

    private void drawNetworkGraph(No no, Point p1, Canvas canvas) {
        int currentLevel = no.obtNivel();
        Queue<No> nos = new ArrayDeque<>();
    }

    private void recursiveDraw(No no, Point p1, Canvas canvas) {
        p1 = new Point(p1.x, p1.y);
        Point p2 = new Point();
        p2.y = p1.y + spaceV;
        p2.x = radius;
        System.out.println(no.toString());
        for (No[] tmps: no.obtFilhos()) {

            if (tmps == null) continue;

            for (No tmp: tmps) {

                if (tmp == null) continue;

                drawNode(canvas, new Point(p2.x, p2.y), tmp);
                drawEdge(canvas, new Point(p1.x, p1.y), new Point(p2.x, p2.y), tmp);
                p2.x += spaceH;

                recursiveDraw(tmp, p2, canvas);
            }
        }
    }

    private void drawEdge(Canvas canvas, Point point1, Point point2, No no) {

        float textSize = 12 * getResources().getDisplayMetrics().density;

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(point1.x, point1.y + radius, point2.x, point2.y - radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        canvas.drawText(no.toStrUltRes(), (point1.x + point2.x) / 2, (point1.y + point2.y) / 2, paint);
    }

    private void drawNode(Canvas canvas, Point point, No no) {

        String[] texts = no.toString().split("\n");
        float textSize = this.textSize * getResources().getDisplayMetrics().density;
        float textX = point.x - radius / 2;

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(point.x, point.y, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);

        if (no.obtTS() != BranchBound.IMPOSS) {
            canvas.drawText(texts[1], textX, point.y - textSize, paint);
            canvas.drawText(texts[2], textX, point.y, paint);
            canvas.drawText(texts[3], textX, point.y + textSize, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

        }

        return super.onTouchEvent(event);
    }
}
