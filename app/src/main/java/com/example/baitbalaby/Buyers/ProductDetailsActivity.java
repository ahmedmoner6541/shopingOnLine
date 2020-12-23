package com.example.baitbalaby.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.baitbalaby.R;
import com.example.baitbalaby.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCarBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productprice, productDiscribtion, productNaem;
    private String productId = "" , state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

        numberButton = findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_detailes);
        productNaem = findViewById(R.id.product_name_detailes);
        productDiscribtion = findViewById(R.id.product_discribtion_detailes);
        productprice = findViewById(R.id.product_price_detailes);
        addToCarBtn = findViewById(R.id.pd_add_to_cart_button);

        getProductDetailes();

        addToCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state.equals("Order placed") || state.equals("Order shipped")){

                    Toast.makeText(ProductDetailsActivity.this, "you can add purchase more products , once your order is shapped or configration", Toast.LENGTH_LONG).show();
                }else {
                    addToCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ckeckOrserState();
    }

    private void addToCartList() {

        String saveCurrentTiem , saveCurrentDate ;

        Calendar calForData = Calendar.getInstance();

        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentData.format(calForData.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTiem = currentTime.format(calForData.getTime());

        final DatabaseReference cartlistRef = FirebaseDatabase.getInstance().getReference().child("cart list");
        final HashMap <String , Object> cartMap = new HashMap<>();

        cartMap.put("pid" , productId);
        cartMap.put("pname" , productNaem.getText().toString());
        cartMap.put("price" , productprice.getText().toString());
        cartMap.put("date" , saveCurrentDate);
        cartMap.put("time" , saveCurrentTiem);
        cartMap.put("quantity" , numberButton.getNumber());
        cartMap.put("discount" , "");

        cartlistRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          cartlistRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                  .child("Products").child(productId)
                                  .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  Toast.makeText(ProductDetailsActivity.this, "Added To Cart List", Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                  startActivity(intent);
                              }
                          });
                      }

                    }
                });

    }

    private void getProductDetailes() {
////////////////////////////////////////////////////////////////////////////////////////////////////Products
        final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    productNaem.setText(products.getPname());
                    Log.d("productNaem", products.getPname());

                    productprice.setText(products.getPrice());
                    Log.d("productprice", products.getPrice());

                    productDiscribtion.setText(products.getDescription());
                    Log.d("productDiscribtion", products.getDescription());

                    Picasso.get().load(products.getImage()).into(productImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ckeckOrserState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped")){
                      state = "Order shipped";
                    }else if (shippingState.equals("not shipped")){
                      state = "Order placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//                String shippingstate = snapshot.child("state").getValue().toString();
//
//                String userName = snapshot.child("name").getValue().toString();
//
//                if (shippingstate.equals("shipped")){
//
//                    txtTotalAmount.setText("Dear" + userName + "/n order is shipped successfully..");
//                    recyclerView.setVisibility(View.GONE);
//                    txtMsg1.setVisibility(View.VISIBLE);
//                    txtMsg1.setText("congratulation...... tour final order soon you well recived order. soon it will verified.");
//                    nextBtn.setVisibility(View.VISIBLE);
//                    Toast.makeText(CartActivity.this, "you can purchase more products , once you received your first order", Toast.LENGTH_SHORT).show();
//                }
//                else if (shippingstate.equals("not shipped")){
//
//                txtTotalAmount.setText("shipping state = Not shipped ");
//
//                recyclerView.setVisibility(View.GONE);
//                txtMsg1.setVisibility(View.VISIBLE);
//                nextBtn.setVisibility(View.GONE);
//                Toast.makeText(CartActivity.this, "you can purchase more products , once you received your first order", Toast.LENGTH_SHORT).show();
//
//         }
//        }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

}