package com.diy.cmtdemoapp01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class utils_capture_positioning_view extends View {

    utils_capture_mask mask;

    public utils_capture_positioning_view(Context context, utils_capture_mask mask) {
        super(context);
        this.mask = mask;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (utils_capture_mask_box box : mask.boxes) {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor(box.color));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            float boxWidth = (float)(box.width_vs_canvas_width_in_percent) / 100 * canvas.getWidth();
            float xMargin = (canvas.getWidth() - boxWidth) / 2;
            float boxHeight = boxWidth / box.width_vs_height_ratio;
            float yMarging = (canvas.getHeight() - boxHeight) / 2;
            canvas.drawRect(xMargin, yMarging, (xMargin + boxWidth), (yMarging + boxHeight), paint);
        }
        for (utils_capture_mask_oval oval : mask.ovals) {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor(oval.color));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            float centerX = (float)(oval.center_x_vs_canvas_width_in_percent) /100 * canvas.getWidth();
            float centerY = (float)(oval.center_y_vs_canvas_height_in_percent) / 100 * canvas.getHeight();
            float ovalWidth = (float)(oval.width_vs_canvas_width_in_percent) / 100 * canvas.getWidth();
            float ovalHeight = ovalWidth / oval.width_vs_height_ratio;
            RectF r = new RectF(centerX-ovalWidth/2, centerY-ovalHeight/2, centerX+ovalWidth/2, centerY+ovalHeight/2);
            canvas.drawOval(r, paint);
        }
    }

}

//    private final float         RADIUS = 35.0f;
//    private final int           COLOR = 0x7FFF0000;
//    private Point[]             corners = new Point[2];                // topLeft, bottomRight
//    private Point               _circleCenter;
//    private Paint               _boundsPaint;
//
//           corners[0] = new Point(
//                    (int) xMargin,
//                    (int) yMarging
//            );
//            corners[1] = new Point(
//                    (int) (xMargin + boxWidth),
//                    (int) (yMarging + boxHeight)
//            );

//		_bounds = new Point[2];
//		_bounds[0] = new Point(20, 500);
//		_bounds[1] = new Point(canvas.getWidth()-20, canvas.getHeight()-500);
//		canvas.drawRect(_bounds[0].x, _bounds[0].y, _bounds[1].x, _bounds[1].y, _boundsPaint);
//        _circleCenter = new Point(canvas.getWidth()/4, canvas.getHeight() / 2);
//        //canvas.drawCircle(_circleCenter.x, _circleCenter.y, RADIUS, _boundsPaint);
//        int ovalWidth = 350;
//        int ovalHeight = 400;
//        RectF r = new RectF(_circleCenter.x-ovalWidth/2, _circleCenter.y-ovalHeight/2, _circleCenter.x+ovalWidth/2, _circleCenter.y+ovalHeight/2);
//        canvas.drawOval(r, _boundsPaint);