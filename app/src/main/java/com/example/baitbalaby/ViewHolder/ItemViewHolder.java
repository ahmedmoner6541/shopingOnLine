package com.example.baitbalaby.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitbalaby.Interface.ItemClickListner;
import com.example.baitbalaby.R;



public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductNameseller, txtProductDescriptionseller, txtProductPriceseller , textproductstatusseller;
    public ImageView imageView;
    public ItemClickListner listner;



    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView =   itemView.findViewById(R.id.product_seller_image);

        txtProductNameseller =   itemView.findViewById(R.id.product_seller_name);
        txtProductDescriptionseller =   itemView.findViewById(R.id.product_seller_description);
        txtProductPriceseller =  itemView.findViewById(R.id.product_seller_price);
        textproductstatusseller =  itemView.findViewById(R.id.product_seller_state);

    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}