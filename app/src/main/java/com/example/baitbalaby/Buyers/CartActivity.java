package com.example.baitbalaby.Buyers;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baitbalaby.R;
import com.example.baitbalaby.ViewHolder.CartViewHoleder;
import com.example.baitbalaby.model.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button nextBtn;
    private TextView txtTotalAmount , txtMsg1;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_List);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn = findViewById(R.id.next_proccess_btn);
        txtTotalAmount = findViewById(R.id.page_title);
        txtMsg1 = findViewById(R.id.msgl);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  txtTotalAmount.setText("Total price = $" + overTotalPrice);/// case for crach
               txtTotalAmount.setText("Total price = $" + String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //////////////////

        ////////////////////////
        ckeckOrserState();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("cart list");
             FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class)
                .build();


        FirebaseRecyclerAdapter<Cart , CartViewHoleder> adapter
                 = new FirebaseRecyclerAdapter<Cart, CartViewHoleder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHoleder holder, int position, @NonNull final Cart model) {

                holder.textproducQuantity.setText("Quantity ="+model.getQuantity());
                holder.textproducprice.setText("Price ="+model.getPrice());
                holder.textproductName.setText("name ="+model.getPname());

               int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options [] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog .Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options :");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0 ){
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (which ==1 ){
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Items removed successfully..", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHoleder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHoleder holeder = new CartViewHoleder(view);
                return holeder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
    private void ckeckOrserState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){

                        txtTotalAmount.setText("Dear "+userName+"\n order is shipped successfully..");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setText("congratulation....to...shipped.\n" + "    . tour final order soon you well recived order . soon it will verified");
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products , once you received your first order", Toast.LENGTH_SHORT).show();

                    }else if (shippingState.equals("not shipped")){
                        txtTotalAmount.setText("shipping state = Not shipped ");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products , once you received your first order", Toast.LENGTH_SHORT).show();

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