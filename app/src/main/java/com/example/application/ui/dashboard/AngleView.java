package com.example.application.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class AngleView extends View {

    public AngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = getWidth();       //获取宽度
        int y = getHeight();       //获取高度

        Paint paint = new Paint();
        paint.setColor(Color.argb(100,128,203,196));
        Path path = new Path();
        path.moveTo(0,0);
        path.moveTo(x,0);
        path.moveTo(0,y);
        path.close();

        canvas.drawPath(path, paint);       //绘制三角形
    }
}
