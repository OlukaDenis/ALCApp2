package com.mcdenny.alcapp2.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdenny.alcapp2.R;

public class TraveDealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView title, description, price;
    public ImageView image;
    public ItemClickListener itemClickListener;

    public TraveDealViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.mtitle);
        description = itemView.findViewById(R.id.description);
        price = itemView.findViewById(R.id.price);
        image = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.itemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
         this.itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}


