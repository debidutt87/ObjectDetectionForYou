package com.ody.di.ui;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An abstract BaseActivity that provides a double back press to exit feature.
 * When the back button is pressed, a Toast message will be displayed. If the back button
 * is pressed again within 2 seconds, the activity will be closed.
 * <p>
 * Activities that wish to use this behavior simply extend this BaseActivity.
 * </p>
 *
 * @author Debidutt Prasad
 */
public abstract class BaseActivity extends AppCompatActivity {

   /** Time of the last back press in milliseconds. */
   private long backPressedTime;

   /** The Toast message displayed to the user upon pressing back. */
   private Toast backToast;

   /**
    * Handles the back button press. If back button is pressed twice within 2 seconds,
    * the activity will exit. If pressed once, a Toast will prompt the user to press
    * again to exit.
    */
   @Override
   public void onBackPressed() {
      long currentTime = System.currentTimeMillis();

      if (backPressedTime + 2000 > currentTime) {
         if (backToast != null) {
            backToast.cancel();
         }
         super.onBackPressed();
      } else {
         backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
         backToast.show();
      }

      backPressedTime = currentTime;
   }
}

