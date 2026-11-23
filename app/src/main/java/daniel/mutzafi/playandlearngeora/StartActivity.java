package daniel.mutzafi.playandlearngeora;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final VideoView globusVideo = findViewById(R.id.globus_video);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.globus;
        Uri uri = Uri.parse(videoPath);
        globusVideo.setVideoURI(uri);
        globusVideo.start();

        ValueAnimator sizeAnimator = ValueAnimator.ofInt(100, 300);
        sizeAnimator.setDuration(5000);
        sizeAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            globusVideo.getLayoutParams().width = (int) (value * getResources().getDisplayMetrics().density);
            globusVideo.getLayoutParams().height = (int) (value * getResources().getDisplayMetrics().density);
            globusVideo.requestLayout();
        });
        sizeAnimator.start();

        // Get the two ImageView instances from the layout.
        final ImageView backgroundOne = findViewById(R.id.clouds_background1);
        final ImageView backgroundTwo = findViewById(R.id.clouds_background2);

        // The animation is started in a post() call to ensure that the views have been measured and have a width.
        backgroundOne.post(() -> {
            // Get the width of the screen (which is the width of the ImageView).
            final int width = backgroundOne.getWidth();

            // Create a ValueAnimator that animates a float from 0.0 to 1.0.
            final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            // Set the animation to repeat indefinitely.
            animator.setRepeatCount(ValueAnimator.INFINITE);
            // Use a LinearInterpolator to ensure the animation speed is constant.
            animator.setInterpolator(new LinearInterpolator());
            // Set the duration of one full animation cycle (e.g., 20 seconds).
            animator.setDuration(20000L);

            // Add an update listener to the animator to move the ImageViews on each frame.
            animator.addUpdateListener(animation -> {
                // Get the current progress of the animation (a value from 0.0 to 1.0).
                final float progress = (float) animation.getAnimatedValue();
                // Calculate the horizontal translation based on the progress and the width of the view.
                final float translationX = width * progress;
                // Move the first ImageView to the right.
                backgroundOne.setTranslationX(translationX);
                // Move the second ImageView to the right, placing it just before the first one.
                // This creates the seamless scrolling effect.
                backgroundTwo.setTranslationX(translationX - width);
            });
            // Start the animation.
            animator.start();
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(StartActivity.this, DeviceChoiceActivity.class);
            startActivity(intent);
            finish();
        }, 5000);
    }
}
