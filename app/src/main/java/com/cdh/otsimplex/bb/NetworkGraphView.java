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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chendehua on 2017/5/15.
 */

public class NetworkGraphView extends View {

    private Paint paint;
    private static float radius = 100;
    private HashMap<String, Point> data;

    public NetworkGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        data = new HashMap<>();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
    }

    public void updateData() {
        Point point = new Point();
        point.x = 500;
        point.y = 100;
        data.put("hi", point);

        point = new Point();
        point.x = getWidth()/2;
        point.y = getHeight()/2;
        data.put("hi2", point);


        point = new Point();
        point.x = 500;
        point.y = getHeight();
        data.put("hi3", point);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);

        Set<Map.Entry<String, Point>> datas = data.entrySet();

        Point last = null;
        for (Map.Entry<String, Point> tmp: datas) {

            drawNode(canvas, tmp.getValue());
            if (last != null) {
                drawEdge(canvas, last, tmp.getValue());
            }
            last = tmp.getValue();

//            canvas.drawCircle(tmp.getValue().x, tmp.getValue().y, radius, paint);
//            canvas.drawCircle(50, 500, radius, paint);

//            canvas.drawLine(width/2, 50 + radius, 50, 500 - radius, paint);
        }

    }

    private void drawEdge(Canvas canvas, Point point1, Point point2) {

        float textSize = 12 * getResources().getDisplayMetrics().density;

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(point1.x, point1.y + radius, point2.x, point2.y - radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        canvas.drawText("z=10", (point1.x + point2.x) / 2, (point1.y + point2.y) / 2, paint);
    }

    private void drawNode(Canvas canvas, Point point) {

        float textSize = 12 * getResources().getDisplayMetrics().density;
        float textX = point.x - radius / 2;

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(point.x, point.y, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);

        canvas.drawText("x=10", textX, point.y - textSize, paint);
        canvas.drawText("y=10", textX, point.y, paint);
        canvas.drawText("z=10", textX, point.y + textSize, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

        }

        return super.onTouchEvent(event);
    }
}
