package com.example.baitbalaby.ViewHolder;

import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitbalaby.Interface.ItemClickListner;
import com.example.baitbalaby.R;

public class CartViewHoleder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textproductName , textproducprice , textproducQuantity ;
    private ItemClickListner itemClickListner ;

    public CartViewHoleder(@NonNull View itemView) {
        super(itemView);

        textproductName = itemView.findViewById(R.id.cart_product_name);
        textproducprice = itemView.findViewById(R.id.cart_product_price);
        textproducQuantity = itemView.findViewById(R.id.cart_product_quantity);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v , getAdapterPosition() , false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
