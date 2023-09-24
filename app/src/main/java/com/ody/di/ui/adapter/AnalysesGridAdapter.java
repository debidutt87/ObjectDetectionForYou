package com.ody.di.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ody.di.R;
import com.ody.di.ui.model.AnalysesModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter class for displaying a grid of analyses items in a RecyclerView.
 * Each item in the grid consists of an image and associated text.
 *
 * @author Debidutt Prasad
 */
public class AnalysesGridAdapter extends RecyclerView.Adapter<AnalysesGridAdapter.ViewHolder> {

    private Context context;
    private List<AnalysesModel> analysesModels;
    private OnItemClickListener mListener;

    /**
     * Interface for handling item click events.
     */
    public interface OnItemClickListener {
        /**
         * Called when an item in the RecyclerView is clicked.
         *
         * @param position The position of the clicked item.
         */
        void onItemClick(int position);
    }

    /**
     * Sets the listener to handle item click events.
     *
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Constructor for the adapter.
     *
     * @param context        The context used to access application-specific resources.
     * @param analysesModels List of models representing the data for each item in the grid.
     */
    public AnalysesGridAdapter(@NonNull Context context, @NonNull List<AnalysesModel> analysesModels) {
        this.context = context;
        this.analysesModels = analysesModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(this.context).load(this.analysesModels.get(position).getImageUri()).into(holder.imageView);
        holder.textView.setText(this.analysesModels.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return analysesModels.size();
    }

    /**
     * ViewHolder class for holding and recycling views in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_analysed_name);
            imageView = itemView.findViewById(R.id.image_view_analysed);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
