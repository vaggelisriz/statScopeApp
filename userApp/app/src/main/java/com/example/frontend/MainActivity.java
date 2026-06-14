package com.example.frontend;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FrameLayout swipeContainer;
    private ImageView swipeButton;
    private float dX; // Αποθήκευση της θέσης Χ κατά το άγγιγμα

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewComponents();
        setupSwipeLogic();
        startInitialBounceAnimation();
    }

    /**
     * Αρχικοποίηση των νέων UI components
     */
    private void initViewComponents() {
        swipeContainer = findViewById(R.id.swipe_container);
        swipeButton = findViewById(R.id.iv_swipe_button);
    }

    /**
     * Λογική για το σύρσιμο (Swipe) και το Intent
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupSwipeLogic() {
        swipeButton.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Αποθηκεύουμε την αρχική διαφορά θέσης
                    dX = view.getX() - event.getRawX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dX;

                    if (newX < 4) { // 4 είναι το layout_marginStart που βάλαμε στο XML
                        newX = 4;
                    }

                    float maxRight = swipeContainer.getWidth() - view.getWidth() - 4;
                    if (newX > maxRight) {
                        newX = maxRight;
                    }

                    // Μετακίνηση του κουμπιού
                    view.setX(newX);
                    break;

                case MotionEvent.ACTION_UP:
                    float finalRightBound = swipeContainer.getWidth() - view.getWidth() - 20; // 20px περιθώριο για το τέρμα

                    if (view.getX() >= finalRightBound) {
                        // Αν ο χρήστης έφτασε στο τέρμα δεξιά -> Άνοιξε τη FanActivity
                        navigateToFanActivity();

                        // Επαναφορά του κουμπιού στην αρχή (ώστε αν γυρίσει πίσω η οθόνη να είναι έτοιμο)
                        view.setX(4);
                    } else {
                        // Αν το άφησε στη μέση, επιστροφή στην αρχή με εφέ Bounce (αναπήδηση)
                        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", view.getX(), 4f);
                        animator.setDuration(400);
                        animator.setInterpolator(new BounceInterpolator());
                        animator.start();
                    }
                    break;

                default:
                    return false;
            }
            return true;
        });
    }

    /**
     * Εφέ αναπήδησης κατά την εκκίνηση για να καταλάβει ο χρήστης ότι σέρνεται
     */
    private void startInitialBounceAnimation() {
        swipeButton.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(swipeButton, "translationX", 0f, 60f, 0f);
            animator.setDuration(1200);
            animator.setInterpolator(new BounceInterpolator());
            animator.start();
        });
    }

    /**
     * Πλοήγηση απευθείας στη FanActivity
     */
    private void navigateToFanActivity() {
        Intent intent = new Intent(MainActivity.this, FanActivity.class);
        startActivity(intent);
    }
}