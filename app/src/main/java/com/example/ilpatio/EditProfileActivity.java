package com.example.ilpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import model.User;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;

    private EditText personName;
    private EditText phoneNumber;
    private EditText emailAddress;
    private String id;
    private User curUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //сохранение имени и email в plain text из базы данных
        Query userQuery2 = users.orderByChild("id").equalTo(id);
        userQuery2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    curUser = ds.getValue(User.class);
                    personName.setText(curUser.getName());
                    emailAddress.setText(curUser.getEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void init(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        personName = findViewById(R.id.personName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        phoneNumber.setText(mAuth.getCurrentUser().getPhoneNumber());
        id = mAuth.getCurrentUser().getUid();
    }

    public void exit(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveAndGoBack(View view){
        //сохранение в бд
        Query userQuery = users.orderByChild("id").equalTo(id); //записи, у которых "id" = id
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // snapshot - бд users
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", personName.getText().toString());
                    map.put("email", emailAddress.getText().toString());
                    ds.getRef().updateChildren(map); //обновление записи
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("personName", personName.getText().toString());
        startActivity(intent);
    }
}