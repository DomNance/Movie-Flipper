package com.chstudios.oscarwatcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private List<AcademyAwards> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MovieListAdapter(Context context, List<AcademyAwards> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_winners, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AcademyAwards academyWinner = mData.get(position);

        holder.myTextViewTitle.setText(academyWinner.getMovieName());
        holder.myTextViewYear.setText(academyWinner.getYearFilm());
        holder.myTextViewAward.setText(academyWinner.getAwardCategory());

        holder.itemView.findViewById(R.id.winning_movie_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.findViewById(R.id.winning_movie_layout).getContext(), ResultActivty.class);
                intent.putExtra("movie_name", academyWinner.getMovieName());
                intent.putExtra("movie_year", academyWinner.getYearFilm());
                intent.putExtra("award_category", academyWinner.getAwardCategory());
                holder.itemView.findViewById(R.id.winning_movie_layout).getContext().startActivity(intent);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextViewTitle = itemView.findViewById(R.id.kaggle_movie_name);
        TextView myTextViewYear = itemView.findViewById(R.id.kaggle_movie_year);
        TextView myTextViewAward = itemView.findViewById(R.id.kaggle_movie_award);
        LinearLayout myLinLayout = itemView.findViewById(R.id.winning_movie_layout);
        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    AcademyAwards getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
