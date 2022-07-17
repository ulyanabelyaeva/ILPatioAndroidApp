package com.example.ilpatio;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView nameUser;
    private Button btnAuth;
    private LinearLayout info_profile;

    private String id;
    private String phoneNumber;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private boolean flagPush = true;
    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }
    public void init(){
        mAuth = FirebaseAuth.getInstance();
        nameUser = findViewById(R.id.nameUser);
        btnAuth = findViewById(R.id.btnAuth);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        info_profile = findViewById(R.id.info_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null){

            nameUser.setText("Пользователь");
            nameUser.setTextSize(20);
            //изменяем высоту и ширину кнопки
            LinearLayout.LayoutParams MyParams = new LinearLayout.LayoutParams(100, 100);
            MyParams.rightMargin = 30;
            MyParams.topMargin = 30;
            MyParams.bottomMargin = 30;
            btnAuth.setLayoutParams(MyParams);
            info_profile.setVisibility(View.VISIBLE);

            btnAuth.setText("");
            btnAuth.setBackgroundResource(R.drawable.ripple_effect_settings);

            //добавление пользователя в базу данных
            id = mAuth.getCurrentUser().getUid();
            phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
            Query userQuery = users.orderByChild("id").equalTo(id);
            userQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        curUser = ds.getValue(User.class);
                        if (!curUser.getName().equals(""))
                            nameUser.setText(curUser.getName());
                        flagPush = false; //пользователь уже есть в бд
                    }
                    if (flagPush){
                        users.push().setValue(new User(id, "", phoneNumber, "", "Москва")); //пользователя нет в бд, создается запись
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } else {
            nameUser.setText("Войдите, чтобы совершать заказы");
            LinearLayout.LayoutParams MyParams = new LinearLayout.LayoutParams(250, 100);
            MyParams.rightMargin = 30;
            btnAuth.setLayoutParams(MyParams);
            btnAuth.setText("Войти");
            btnAuth.setBackgroundResource(R.drawable.ripple_effect);
        }
    }

    public void signIn(View view){
        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void goToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goToContacts(View view){
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goToCart(View view){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}