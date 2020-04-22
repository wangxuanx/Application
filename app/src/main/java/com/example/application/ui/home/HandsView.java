package com.example.application.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.application.R;


public class HandsView extends View {
    private String password = "";

    public HandsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int X = getWidth()/4;        /**1/4画布宽度*/
        int Y = getHeight()/4;        /**1/4画布高度*/
        String passwrd;
        passwrd = getPassword();        //获取用户绘制的密码

        System.out.println(passwrd);

        /**
         各点坐标如下
         a: X,Y
         b: X,2*Y
         c: X,3*Y
         d: 2*X, Y
         e: 2*X, 2*Y
         f: 2*X, 3*Y
         g: 3*X, Y
         h: 3*X, 2*Y
         i: 3*X, 2*Y
         * */


        //canvas.drawColor(Color.RED);
        Paint paint = new Paint() ;
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(R.color.colorPrimary)); //直线的颜色

        for(int i = 0; i < passwrd.length() - 1; i++){        //循环绘制线条
            char x = passwrd.charAt(i);          //第一个点
            char y = passwrd.charAt(i + 1);        //第二个点

            int ax = 0, ay = 0, bx = 0, by = 0;

            if(x == 'a'){
                ax = X; ay = Y;
            } else if(x == 'b'){
                ax = 2*X; ay = Y;
            } else if(x == 'c'){
                ax = 3*X; ay = Y;
            } else if(x == 'd'){
                ax = X; ay = 2*Y;
            } else if(x == 'e'){
                ax = 2*X; ay = 2*Y;
            } else if(x == 'f'){
                ax = 3*X; ay = 2*Y;
            } else if(x == 'g'){
                ax = X; ay = 3*Y;
            } else if(x == 'h'){
                ax = 2*X; ay = 3*Y;
            } else if(x == 'i'){
                ax = 3*X; ay = 3*Y;
            }

            if(y == 'a'){
                bx = X; by = Y;
            } else if(y == 'b'){
                bx = 2*X; by = Y;
            } else if(y == 'c'){
                bx = 3*X; by = Y;
            } else if(y == 'd'){
                bx = X; by = 2*Y;
            } else if(y == 'e'){
                bx = 2*X; by = 2*Y;
            } else if(y == 'f'){
                bx = 3*X; by = 2*Y;
            } else if(y == 'g'){
                bx = X; by = 3*Y;
            } else if(y == 'h'){
                bx = 2*X; by = 3*Y;
            } else if(y == 'i'){
                bx = 3*X; by = 3*Y;
            }

            canvas.drawLine(ax, ay, bx, by, paint);
        }

        Paint point = new Paint();
        point.setColor(getResources().getColor(R.color.black));
        point.setStrokeWidth(20);
        for(int i = 1; i <= 3; i++){
            canvas.drawPoint(i*X, Y, point);
            canvas.drawPoint(i*X, 2*Y, point);
            canvas.drawPoint(i*X, 3*Y, point);
        }

        //canvas.drawLine(0,0,getWidth(),getHeight(), paint);//直线起点坐标（50,120），终点坐标（460,120）
        System.out.println(getWidth()+" "+getHeight());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
