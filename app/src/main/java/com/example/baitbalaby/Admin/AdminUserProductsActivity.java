package com.example.baitbalaby.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baitbalaby.R;
import com.example.baitbalaby.ViewHolder.CartViewHoleder;
import com.example.baitbalaby.model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList ;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference carttRef ;
    private String userID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        userID = getIntent().getStringExtra("uid");

        productsList = findViewById(R.id.product_List);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        carttRef = FirebaseDatabase.getInstance().getReference()
                .child("cart list")
                .child("Admin View")
                .child(userID)
                .child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart > options =
                new  FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(carttRef , Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartViewHoleder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHoleder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHoleder holder, int position, @NonNull Cart model) {

                        holder.textproducQuantity.setText("Quantity ="+model.getQuantity());
                        holder.textproducprice.setText("Price ="+model.getPrice());
                        holder.textproductName.setText("name ="+model.getPname());

                    }

                    @NonNull
                    @Override
                    public CartViewHoleder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHoleder holeder = new CartViewHoleder(view);
                        return holeder;
                    }
                };
        productsList.setAdapter(adapter);
        adapter.startListening();

    }
}
