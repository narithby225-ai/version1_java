package com.narith.aims.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SimpleBarChart extends View {
    private Paint barPaint;
    private List<Integer> data = new ArrayList<>();

    public SimpleBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#FF9800"));
        barPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(List<Integer> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float barWidth = width / (data.size() * 2f);
        float maxVal = 0;
        for (int val : data) if (val > maxVal) maxVal = val;
        
        if (maxVal == 0) maxVal = 1;

        for (int i = 0; i < data.size(); i++) {
            float barHeight = (data.get(i) / maxVal) * height;
            float left = (i * 2 * barWidth) + (barWidth / 2f);
            canvas.drawRect(left, height - barHeight, left + barWidth, height, barPaint);
        }
    }
}
