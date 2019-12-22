package com.example.xchange;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xchange.ui.home.HomeFragment;

import java.util.List;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {



    private Context mContext;
    private List<Item> listItem;

    public RecyclerViewAdapter(Context mContext, List<Item> listItem) {
        this.mContext = mContext;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(mContext);
        view = mInflator.inflate(R.layout.cardview_home,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
      // holder.tv_item_title.setText(mData.get(position));
      //  holder.img_item_thumbnail.setImageResource(mData.get(position));
        Item item = listItem.get(position);
        holder.tv_item_title.setText(item.getTitle());
        holder.tv_item_description.setText(item.getDescription());

        //loading image using library picasso
        List<String> urlPicture = item.getPhoto();
        String urlCover= urlPicture.get(0);
        Picasso.get()
                .load(urlCover)
                .fit()
                .centerCrop()
                .into(holder.img_item_thumbnail);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView tv_item_title;
        public TextView tv_item_description;
        public ImageView img_item_thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            tv_item_title = itemView.findViewById(R.id.item_title_id);
            tv_item_description = itemView.findViewById(R.id.item_des);
            img_item_thumbnail = itemView.findViewById(R.id.item_img_id);
        }
    }




}
