package com.ody.di.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ody.di.R;

import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

/**
 * An adapter class for the RecyclerView that displays detected objects.
 * Each row item in the RecyclerView consists of the detected item's name, confidence score, and a serial number.
 *
 * @author Debidutt Prasad
 */
public class DetectedObjectsRecyclerAdapter extends RecyclerView.Adapter<DetectedObjectsRecyclerAdapter.ViewHolder> {

    private static final String TAG = "DetectedObjectsRecyclerAdapter";
    private final List<Detector.Recognition> list;

    /**
     * Initializes the dataset of the Adapter.
     *
     * @param list List of {@link Detector.Recognition} containing the data to populate views used by the RecyclerView.
     */
    public DetectedObjectsRecyclerAdapter(List<Detector.Recognition> list) {
        this.list = list;
    }

    /**
     * Create new views. This is invoked by the layout manager.
     *
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType  The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_item, viewGroup, false);
        return new ViewHolder(v);
    }

    /**
     * Replace the contents of a view. This method is invoked by the layout manager.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getItemName().setText(list.get(position).getTitle());
        float confidenceScore = list.get(position).getConfidence() * 100;
        viewHolder.getConfidenceScore().setText(String.format(Locale.getDefault(), "%.0f", confidenceScore));
        String serialNumber = viewHolder.getSerialNumber().getContext().getString(R.string.serial_number, position + 1);
        viewHolder.getSerialNumber().setText(serialNumber);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView confidenceScore;
        private final TextView serialNumber;

        public ViewHolder(View v) {
            super(v);
            itemName = v.findViewById(R.id.item_name);
            confidenceScore = v.findViewById(R.id.confidence_score);
            serialNumber = v.findViewById(R.id.serial_number);
        }

        /**
         * Returns the TextView that displays the name of the detected item.
         *
         * @return The TextView displaying the item's name.
         */
        public TextView getItemName() {
            return itemName;
        }

        /**
         * Returns the TextView that displays the confidence score of the detected item.
         *
         * @return The TextView displaying the confidence score.
         */
        public TextView getConfidenceScore() {
            return confidenceScore;
        }

        /**
         * Returns the TextView that displays the serial number of the detected item.
         *
         * @return The TextView displaying the serial number.
         */
        public TextView getSerialNumber() {
            return serialNumber;
        }
    }
}

