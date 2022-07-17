package com.example.ilpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "llllllllllll";
    private WebView web;
    private String city = "Москва";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private String id;
    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        if (mAuth.getCurrentUser() != null){
            id = mAuth.getCurrentUser().getUid();
            //вытаскивание города из базы данных
            Query userQuery2 = users.orderByChild("id").equalTo(id);
            userQuery2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        curUser = ds.getValue(User.class);
                        if (curUser.getCity() != null){
                            city = curUser.getCity();
                            web = findViewById(R.id.ww_map);
                            WebSettings webSettings = web.getSettings();
                            webSettings.setJavaScriptEnabled(true);

                            web.setWebViewClient(new WebViewClient());
                            web.loadUrl("https://www.google.com/maps/search/?api=1&query=ILPatio+" + city);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    public void goToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
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