package com.example.ilpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Adapters.CartAdapter;
import Adapters.FoodAdapter;
import Models.Food;
import Models.Order;
import Models.User;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "llllllllllll";

    private RecyclerView cart_list;
    private CartAdapter cartAdapter;
    public static TextView count_cart;

    private List<Food> foodListAll = MainActivity.getFullFoodList();

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private String id;
    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void init(){
        count_cart = findViewById(R.id.count_cart);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        if (mAuth.getCurrentUser() != null){
            id = mAuth.getCurrentUser().getUid();
            //вытаскивание корзины из базы данных
            Query userQuery2 = users.orderByChild("id").equalTo(id);
            userQuery2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        curUser = ds.getValue(User.class);
                        Order.cart.clear();
                        if (curUser.getCart() != null)
                            Order.cart.addAll(curUser.getCart());

                        cart_list = findViewById(R.id.cart_list); //вывод корзины
                        cartAdapter = new CartAdapter(CartActivity.this, Order.cart);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CartActivity.this, RecyclerView.VERTICAL, false);
                        cart_list.setLayoutManager(layoutManager);
                        cart_list.setAdapter(cartAdapter);

                        if (Order.cart.size() > 0){ //подсчет
                            int cost = 0;
                            for (int i = 0; i < Order.cart.size(); i++){
                                for(int j = 0; j < foodListAll.size(); j++){
                                    if (foodListAll.get(j).getId() == Order.cart.get(i)){
                                        cost = cost + foodListAll.get(j).getCost();
                                        break;
                                    }
                                }
                            }
                            count_cart.setText(Order.cart.size() + " товаров на " + cost + " ₽");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            count_cart.setText("Авторизуйтесь, чтобы добавлять товары в корзину");
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

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}