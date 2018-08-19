package com.kk2ak.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {
    private ArrayList<BookDetails> unfilteredDataset;
    private ArrayList<BookDetails> filteredDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public Button mButton;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.book_name);
            mImageView = v.findViewById(R.id.book_image);
            mButton = v.findViewById(R.id.book_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public myAdapter(ArrayList<BookDetails> mDataset, Context context) {
        this.unfilteredDataset = new ArrayList<>(mDataset);
        this.filteredDataset = new ArrayList<>(mDataset);
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public myAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void filter(String genre) {
        filteredDataset.clear();
        if (genre.equals("All")) {
            filteredDataset.addAll(unfilteredDataset);
        } else {
            for (BookDetails currBook : unfilteredDataset) {
                if (currBook.genre.equals(genre)) {
                    filteredDataset.add(currBook);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final BookDetails currBook = filteredDataset.get(position);
        holder.mTextView.setText(currBook.name + "\n" + currBook.author + "\n" + currBook.genre);
        Glide.with(this.context).load(this.context.getString(R.string.url) + "/library/images/images/" + currBook.imageURL).apply(new RequestOptions().override(400,400)).into(holder.mImageView);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(holder.mTextView.getContext(), DetailedList.class);
                i1.putExtra("bookid", currBook.bookid);
                holder.mTextView.getContext().startActivity(i1);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

    public void updateDataSet(ArrayList<BookDetails> mDataset) {
        this.unfilteredDataset = new ArrayList<>(mDataset);
        this.filteredDataset = new ArrayList<>(mDataset);
        this.notifyDataSetChanged();
    }
}
