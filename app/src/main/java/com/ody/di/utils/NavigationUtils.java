package com.ody.di.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;

/**
 * Provides utility methods for navigating between activities.
 *
 * @author Debidutt Prasad
 */
public class NavigationUtils {

    /**
     * Navigates to the specified activity. Allows for clearing the task and passing extras.
     *
     * @param currentActivity     The current activity from which navigation is initiated.
     * @param targetActivityClass The class of the target activity to navigate to.
     * @param clearTask           True if the current task should be cleared, false otherwise.
     * @param extras              Varargs of key-value pairs to be put as extras in the intent.
     */
    public static void navigate(Activity currentActivity,
                                Class<?> targetActivityClass,
                                boolean clearTask,
                                Pair<String, String>... extras) {
        Intent intent = new Intent(currentActivity, targetActivityClass);

        // Add extras to the intent
        for (Pair<String, String> extra : extras) {
            intent.putExtra(extra.first, extra.second);
        }

        if (clearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        currentActivity.startActivity(intent);

        if (clearTask) {
            currentActivity.finishAffinity();
        }
    }
}
