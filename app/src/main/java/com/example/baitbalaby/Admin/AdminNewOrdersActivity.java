package com.example.baitbalaby.Admin;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitbalaby.R;
import com.example.baitbalaby.model.AdminOrsers;
import com.example.baitbalaby.prevalent.AdminOrdersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    RecyclerView orderlist ;
    DatabaseReference orderRef ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("orders");

        orderlist = findViewById(R.id.order_List);
        orderlist.setLayoutManager(new LinearLayoutManager(this));
//        orderlist.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrsers> options =
                new FirebaseRecyclerOptions.Builder<AdminOrsers>()
                .setQuery(orderRef , AdminOrsers.class)

                .build();

        FirebaseRecyclerAdapter<AdminOrsers , AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrsers, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull AdminOrsers model) {

                        holder.userName.setText("Name : "+model.getName());
                        holder.userPhoneNumber.setText("Phone : "+model.getPhone());
                        holder.userTotalPrice.setText("TotalAmount : "+model.getTotalAmount());
                        holder.userDataTime.setText("order at : "+model.getDate() + " " + model.getTime());
                        holder.userShippingAddress.setText("Sipping Address : "+model.getAddress());

                        holder.showOrders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID = getRef(position).getKey();///**********

                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option [] = new CharSequence[]
                                        {
                                              "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder =new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("have you shapped this ordr proucts ?");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which==0){
                                            String uID = getRef(position).getKey();///**********
                                                removeOrder(uID);
                                        }else {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);

                    }
                };
        orderlist.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeOrder(String uID) {

        orderRef.child(uID).removeValue();
    }
}
