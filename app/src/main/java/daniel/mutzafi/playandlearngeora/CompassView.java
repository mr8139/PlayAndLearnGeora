package daniel.mutzafi.playandlearngeora;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CompassView extends View {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int sectorIndex);
    }

    private Paint paint;
    private RectF rectF;
    private float rotationAngle = 0;
    private float lastTouchAngle = 0;
    private OnSelectionChangedListener listener;
    
    // 3 sectors: 0 = Learn (Green), 1 = Info (Yellow), 2 = Game (Red)
    private final int[] sectorColors = {Color.parseColor("#4CAF50"), Color.parseColor("#FFC107"), Color.parseColor("#F44336")};
    private final String[] sectorLabels = {"LEARN", "INFO", "GAME"};

    public CompassView(Context context) {
        super(context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);
        int padding = 50;
        rectF = new RectF(padding, padding, size - padding, size - padding);
        // Center the rect
        float offsetX = (w - size) / 2f;
        float offsetY = (h - size) / 2f;
        rectF.offset(offsetX, offsetY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rectF == null) return;

        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float radius = rectF.width() / 2;

        // Save canvas state before rotation
        canvas.save();
        canvas.rotate(rotationAngle, cx, cy);

        // Draw sectors
        float sweepAngle = 360f / 3;
        for (int i = 0; i < 3; i++) {
            paint.setColor(sectorColors[i]);
            // Start angle: -90 (top) + i * sweep
            canvas.drawArc(rectF, -90 + i * sweepAngle, sweepAngle, true, paint);
            
            // Draw labels
            paint.setColor(Color.WHITE);
            // Angle to draw text: middle of the sector
            double angleRad = Math.toRadians(-90 + i * sweepAngle + sweepAngle / 2);
            float textX = (float) (cx + (radius * 0.7) * Math.cos(angleRad));
            float textY = (float) (cy + (radius * 0.7) * Math.sin(angleRad)) + 20; // +20 for vertical alignment
            
            // Rotate text to be readable? Let's keep it simple first
             canvas.drawText(sectorLabels[i], textX, textY, paint);
        }

        canvas.restore();

        // Draw center indicator (fixed arrow at top)
        paint.setColor(Color.BLACK);
        // Triangle at top
        float arrowSize = 40;
        // canvas.drawRect(cx - 5, rectF.top - 20, cx + 5, rectF.top + 20, paint); // Simple marker
        
        // Draw a pointer at the top
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        // Assuming 12 o'clock is the selection point
        canvas.drawCircle(cx, rectF.top, 20, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchAngle = (float) Math.toDegrees(Math.atan2(y - cy, x - cx));
                return true;
            case MotionEvent.ACTION_MOVE:
                float currentAngle = (float) Math.toDegrees(Math.atan2(y - cy, x - cx));
                float delta = currentAngle - lastTouchAngle;
                rotationAngle += delta;
                lastTouchAngle = currentAngle;
                invalidate();
                notifySelection();
                return true;
            case MotionEvent.ACTION_UP:
                // Snap to nearest sector? Optional.
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void notifySelection() {
        if (listener == null) return;
        
        // Calculate which sector is at the TOP (-90 degrees visual, or 270)
        // Normalize angle to 0-360 positive
        float normalizedAngle = (rotationAngle % 360 + 360) % 360;
        
        // The logic: 
        // If 0 rotation: Sector 0 is at top (spanning -150 to -30? No, -90 is start. So -90 to 30)
        // Wait, start angle is -90.
        // Sector 0: -90 to 30 (120 deg sweep). Center at -30.
        // Sector 1: 30 to 150. Center at 90.
        // Sector 2: 150 to 270. Center at 210.
        
        // If we rotate the WHEEL by X degrees clockwise, the content moves clockwise.
        // The pointer is stationary at TOP (270 or -90 deg).
        // Effectively, the pointer angle relative to the wheel is: PointerAngle - WheelRotation.
        // Pointer is at -90 (or 270).
        
        float relativePointerAngle = (-90 - rotationAngle) % 360;
        if (relativePointerAngle < 0) relativePointerAngle += 360;

        int selected = -1;
        // Sector 0: starts at -90 (270) ends at 30. So range [270, 360) U [0, 30]
        // But we draw: -90 + i*120.
        // i=0: -90 to 30 (270 to 30).
        // i=1: 30 to 150.
        // i=2: 150 to 270.
        
        if ((relativePointerAngle >= 270 && relativePointerAngle < 360) || (relativePointerAngle >= 0 && relativePointerAngle < 30)) {
            selected = 0;
        } else if (relativePointerAngle >= 30 && relativePointerAngle < 150) {
            selected = 1;
        } else if (relativePointerAngle >= 150 && relativePointerAngle < 270) {
            selected = 2;
        }
        
        listener.onSelectionChanged(selected);
    }
}
