package com.example.arabicSopping.Seller;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.arabicSopping.Buyers.MainActivity;
import com.example.arabicSopping.R;


import com.example.arabicSopping.ViewHolder.ItemViewHolder;
import com.example.arabicSopping.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SallerHomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedproduct;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saller_home);

        BottomNavigationView bottomNavigationView =  findViewById(R.id.nav_view_butttonnn);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intenthome = new Intent(SallerHomeActivity.this, SallerHomeActivity.class);

                        startActivity(intenthome);
                        return true;

                    case R.id.navigation_addd:

                        Intent intentadd = new Intent(SallerHomeActivity.this, SellerProductCategoryActivity.class);

                        startActivity(intentadd);
                        return true;



                    case R.id.navigation_logout:
                        Toast.makeText(SallerHomeActivity.this, "تسجيل الخروج", Toast.LENGTH_SHORT).show();

                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();

                        Intent intentlogout = new Intent(SallerHomeActivity.this, MainActivity.class);
                        intentlogout.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentlogout);
                        finish();


                        return true;

                }
                return true;
            }
        });

        unverifiedproduct = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView =findViewById(R.id.sellery_home_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unverifiedproduct
                                .orderByChild("sid").equalTo(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Products model) {

                holder.txtProductNameseller.setText(model.getPname());
                holder.txtProductDescriptionseller.setText(model.getDescription());


                holder.textproductstatusseller.setText("الحاله :"+model.getProductstate());
                holder.txtProductPriceseller.setText("السعر = " + model.getPrice() + "$");

                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String producID = model.getPid();
                        CharSequence options [] = new CharSequence[]
                                {
                                        "نعم "
                                        ,"لا"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SallerHomeActivity.this);
                        builder.setTitle("هل تريد حذف هذا المنتج. هل أنت واثق؟");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0 ){

                                  deletproduct(producID);

                                }if (which == 1){

                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                ItemViewHolder holder = new ItemViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deletproduct(String producID) {

        unverifiedproduct.child(producID)
            .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
  Toast.makeText(SallerHomeActivity.this, "تم حذف هذا العنصر بنجاح ، وهو الآن متاح للبيع من البائع\n" , Toast.LENGTH_SHORT).show();
                    }
                });

     }
}


