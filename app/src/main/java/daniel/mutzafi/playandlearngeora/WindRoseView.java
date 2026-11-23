package daniel.mutzafi.playandlearngeora;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WindRoseView extends View {
    private Paint yellowCirclePaint, redCirclePaint;
    private int selectedDirection = -1; // Index of the red circle (0-15, -1 = none)
    private static final String[] DIRECTIONS = {
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    };
    private Bitmap compassRoseBitmap;

    public WindRoseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        compassRoseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass_rose_en);
    }

    private void initPaints() {
        yellowCirclePaint = new Paint();
        yellowCirclePaint.setAntiAlias(true);
        yellowCirclePaint.setColor(0xFFFFFF00); // Yellow
        yellowCirclePaint.setStyle(Paint.Style.FILL);

        redCirclePaint = new Paint();
        redCirclePaint.setAntiAlias(true);
        redCirclePaint.setColor(0xFFFF0000); // Red
        redCirclePaint.setStyle(Paint.Style.FILL);
    }

    public void setSelectedDirection(int direction) {
        this.selectedDirection = direction;
        invalidate(); // Redraw the view when direction changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Scale the image to the View size
        float scale = Math.min(getWidth(), getHeight()) / (float) Math.max(compassRoseBitmap.getWidth(), compassRoseBitmap.getHeight()); // Use max to ensure it fits
        int newWidth = (int) (compassRoseBitmap.getWidth() * scale);
        int newHeight = (int) (compassRoseBitmap.getHeight() * scale);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(compassRoseBitmap, newWidth, newHeight, true);
        float left = (getWidth() - newWidth) / 2f;
        float top = (getHeight() - newHeight) / 2f;
        canvas.drawBitmap(scaledBitmap, left, top, null);

        // Radius for circles (e.g., 80% of half the minimum dimension of the view)
        float radius = Math.min(getWidth(), getHeight()) / 2f * 0.8f;
        float circleRadius = radius * 0.1f; // Radius of the small direction circles
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Draw 16 direction circles
        for (int i = 0; i < 16; i++) {
            // Adjust angle so that N (index 0) is at the top (90 degrees in standard trig)
            // Each step is 22.5 degrees (360 / 16)
            float drawingAngleDegrees = 90.0f - (i * 22.5f);
            // Ensure the angle is positive for consistency if needed, though Math.toRadians handles negatives.
            // if (drawingAngleDegrees < 0) drawingAngleDegrees += 360;

            float rad = (float) Math.toRadians(drawingAngleDegrees);
            float circleX = centerX + (float) (Math.cos(rad) * radius);
            float circleY = centerY - (float) (Math.sin(rad) * radius); // Subtract because Y is inverted in Canvas

            Paint paint = (i == selectedDirection) ? redCirclePaint : yellowCirclePaint;
            canvas.drawCircle(circleX, circleY, circleRadius, paint);
        }
    }

    public String getDirection(int index) {
        return index >= 0 && index < DIRECTIONS.length ? DIRECTIONS[index] : "";
    }
}
