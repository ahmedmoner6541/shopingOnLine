 package com.example.baitbalaby.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baitbalaby.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

 public class ReserPasswordActivity extends AppCompatActivity {

    private String check = "" ;
    private TextView pageTitle , titlequestion;
    private EditText phoneNumber ,question1,question2;
    private Button vetrifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reser_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        titlequestion = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id. question_1);
        question2 = findViewById(R.id. question_2);
        vetrifyBtn = findViewById(R.id. verify_btn);

    }

    @Override
    protected void onStart() {

        phoneNumber.setVisibility(View.GONE);

        super.onStart();
        if (check.equals("settings")){
                pageTitle.setText("Set Questions ");
                titlequestion.setText(" please set Answer  for the  the folloing securiry Question ");

                vetrifyBtn.setText("Set");
            displeyPreviousAnswer();
                vetrifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        setAnswers();

                    }
                });

        } else if (check.equals("login")){

            phoneNumber.setVisibility(View.VISIBLE);

            vetrifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    veriftUser();
                }
            });
        }
    }

     private void setAnswers() {

         String answer1 = question1.getText().toString().toLowerCase();
         String answer2 = question2.getText().toString().toLowerCase();

         if (question1.equals("")&& question2.equals("")){

             Toast.makeText(ReserPasswordActivity.this, "please answer both questions .", Toast.LENGTH_SHORT).show();

         }
         else {

             DatabaseReference ref = FirebaseDatabase.getInstance()
                     .getReference()
                     .child("Users")
                     .child(Prevalent.currentOnlineUser.getPhone());
             HashMap<String ,Object> map =  new HashMap<>();

             map.put("answer1", answer1);
             map.put("answer2",answer2);

             ref.child("security Questions").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         Toast.makeText(ReserPasswordActivity.this, "you have set secutity querstion successfully..", Toast.LENGTH_SHORT).show();
                         Intent intent  =  new Intent(ReserPasswordActivity.this, HomeActivity.class);
                         startActivity(intent);
                     }
                 }
             });

         }
    }
    private void displeyPreviousAnswer(){
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        ref.child("security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void veriftUser(){

        final String phone = phoneNumber.getText().toString();

        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("")&& !answer1.equals("") &&  !answer2.equals("")){
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){

                        String mphone = snapshot.child("phone").getValue().toString();

                        if (snapshot.hasChild("security Questions")){
                            String ans1 = snapshot.child("security Questions").child("answer1").getValue().toString();
                            String ans2 = snapshot.child("security Questions").child("answer2").getValue().toString();


                            if (!ans1.equals(answer1)){
                                Toast.makeText(ReserPasswordActivity.this, "your lst answer is wrong", Toast.LENGTH_SHORT).show();
                            }
                            else if (!ans2.equals(answer2)){
                                Toast.makeText(ReserPasswordActivity.this, "your 2st answer is wrong", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReserPasswordActivity.this);
                                builder.setTitle("new password ");

                                final EditText newpPssword= new EditText(ReserPasswordActivity.this);
                                newpPssword.setHint("write new password her ");
                                builder.setView(newpPssword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newpPssword.getText().toString().equals("")){
                                            ref.child("password").setValue(newpPssword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ReserPasswordActivity.this, "password change is successfully", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(ReserPasswordActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                                        
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });
                                builder.show();
                            }
                        }

                        else {                                               //this user phone number  is not exists
                            Toast.makeText(ReserPasswordActivity.this, "you have not set the security questions.please contact ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ReserPasswordActivity.this, "This phone number not existes", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

         else{
            Toast.makeText(this, "please complete the form", Toast.LENGTH_SHORT).show();
        }

    }
 }