package com.example.gps;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.HashMap;

public class StaticData{
    private static HashMap<String, PointF> points;
    public static final int PERMISSIONS_CODE = 100;
    public static final String BEACON_TYPE = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public static Paint LINE = new Paint();


    static {
        points = new HashMap<>();
        points.put("33333333-3333-3333-3333-333333333333", new PointF(0, 0));
        points.put("22222222-2222-2222-2222-222222222222", new PointF(0, 2f));
        points.put("11111111-1111-1111-1111-111111111111", new PointF(2f, 0));
        LINE.setStyle(Paint.Style.FILL_AND_STROKE);
        LINE.setStrokeWidth(2);
        LINE.setColor(Color.WHITE);

    }

    public static HashMap<String, PointF> getPoints() {
        return points;
    }

    public static PointF getCoordinates(PointF point1, double distance1, PointF point2, double distance2, PointF point3, double distance3) {

        double firstVarX = point2.x - point1.x, firstVarY = point2.y - point1.y,
                firstSum = 0.5 * (point2.x * point2.x - point1.x * point1.x + point2.y * point2.y - point1.y * point1.y + distance1 * distance1 - distance2 * distance2);
        double secondVarX = point3.x - point1.x, secondVarY = point3.y - point1.y,
                secondSum = 0.5 * (point3.x * point3.x - point1.x * point1.x + point3.y * point3.y - point1.y * point1.y + distance1 * distance1 - distance3 * distance3);


        double prevFirstVarX = firstVarX, prevSum = firstSum;

        if(firstVarY != 0) {
            secondVarY /= firstVarY;

            prevFirstVarX *= secondVarY;
            prevSum *= secondVarY;
        }
        else{
            secondVarX = 0;
            secondSum = 0;
        }


        double lastX = prevFirstVarX - secondVarX;
        double lastSum = prevSum - secondSum;

        double responseX = lastSum / lastX;
        double responseY = (firstSum - responseX * firstVarX) / firstVarY;


        return new PointF((float)responseX, (float)responseY);
    }


}
