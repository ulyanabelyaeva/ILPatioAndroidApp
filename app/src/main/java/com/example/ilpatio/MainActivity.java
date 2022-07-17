package com.example.ilpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CategoryAdapter;
import adapter.FoodAdapter;
import database.DbHelper;
import model.Category;
import model.Food;
import model.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "llllllllllll";

    private RecyclerView categoryRecycleView;
    private CategoryAdapter categoryAdapter;

    private RecyclerView hotfoodRecycleView;
    private RecyclerView pastaRecycleView;
    private RecyclerView pizzaRecycleView;
    private RecyclerView saladRecycleView;
    private RecyclerView drinkRecycleView;
    private RecyclerView soupRecycleView;

    private FoodAdapter foodAdapter;

    private static ScrollView scrollView;
    private static CardView hotfoddImage;
    private static CardView pastaImage;
    private static CardView pizzaImage;
    private static CardView saladImage;
    private static CardView drinkImage;
    private static CardView soupImage;

    private static List <Food> fullFoodList = new ArrayList<>();

    private DbHelper dbHelper;

    private TextView city_name;
    private String[] city ={"Москва", "Санкт-Петербург", "Владивосток", "Екатеринбург", "Обнинск", "Краснодар", "Красноярск",
            "Махачкала", "Нижневартовск", "Нижний Новгород", "Новосибирск", "Пенза", "Пермь", "Пятигорск", "Сергиев Посад", "Серпухов", "Ставрополь",
            "Тверь", "Ханты-Мансийск", "Череповец", "Электросталь"};

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private String id;
    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initdb();
    }

    private void initdb() {
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
                            city_name = findViewById(R.id.city_name);
                            city_name.setText(curUser.getCity());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static List<Food> getFullFoodList(){ //для корзины
        return fullFoodList;
    }

    public void init(){

        scrollView = findViewById(R.id.myScrollView);
        hotfoddImage = findViewById(R.id.hotfoodImage);
        pastaImage = findViewById(R.id.pastaImage);
        pizzaImage = findViewById(R.id.pizzaImage);
        saladImage = findViewById(R.id.saladImage);
        drinkImage = findViewById(R.id.drinkImage);
        soupImage = findViewById(R.id.soupImage);

        List<Food> hotFood = new ArrayList<>();
        List<Food> pasta = new ArrayList<>();
        List <Food> pizza = new ArrayList<>();
        List <Food> salad = new ArrayList<>();
        List <Food> drink = new ArrayList<>();
        List <Food> soup = new ArrayList<>();

        dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor1 = database.rawQuery("SELECT * FROM food", null);
        cursor1.moveToFirst();
        while (!cursor1.isAfterLast()) {
            int id = cursor1.getColumnIndex("_id");
            int category = cursor1.getColumnIndex("category");
            int cost = cursor1.getColumnIndex("cost");
            int image = cursor1.getColumnIndex("image");
            int title = cursor1.getColumnIndex("title");
            int description = cursor1.getColumnIndex("description");
            Food food = new Food(cursor1.getInt(id),
                    cursor1.getInt(cost),
                    cursor1.getInt(category),
                    cursor1.getString(image),
                    cursor1.getString(title),
                    cursor1.getString(description));
            cursor1.moveToNext();
            fullFoodList.add(food);
        }
        cursor1.close();

        List<Category> categoryList = new ArrayList<>();

        Cursor cursor2 = database.rawQuery("SELECT * FROM category", null);
        cursor2.moveToFirst();
        while (!cursor2.isAfterLast()) {
            Category category = new Category(
                    cursor2.getInt(0),
                    cursor2.getString(1)
            );
            cursor2.moveToNext();
            categoryList.add(category);
        }
        cursor2.close();
        dbHelper.close();

        setCategoryList(categoryList);

        for (int i = 0; i < fullFoodList.size(); i++) {
            switch (fullFoodList.get(i).getCategory()){
                case 1:
                    hotFood.add(fullFoodList.get(i));
                case 2:
                    pasta.add(fullFoodList.get(i));
                case 3:
                    pizza.add(fullFoodList.get(i));
                case 4:
                    salad.add(fullFoodList.get(i));
                case 5:
                    drink.add(fullFoodList.get(i));
                case 6:
                    soup.add(fullFoodList.get(i));
            }
        }

        setFoodList1(hotFood);
        setFoodList2(pasta);
        setFoodList3(pizza);
        setFoodList4(salad);
        setFoodList5(drink);
        setFoodList6(soup);

    }

    public void chooseCity(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setItems(city, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                city_name = findViewById(R.id.city_name);
                city_name.setText(city[i]);
                dialogInterface.dismiss();
                if (mAuth.getCurrentUser() != null) {
                    id = mAuth.getCurrentUser().getUid();
                    Query userQuery = users.orderByChild("id").equalTo(id); //записи, у которых "id" = id
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) { // snapshot - бд users
                            for(DataSnapshot ds : snapshot.getChildren()){
                                Map<String, Object> map = new HashMap<>();
                                map.put("city", city[i]);
                                ds.getRef().updateChildren(map); //обновление записи
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void setFoodList6(List<Food> soup) {
        soupRecycleView = findViewById(R.id.soupRecycleView);
        foodAdapter = new FoodAdapter(this, soup);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        soupRecycleView.setLayoutManager(layoutManager);
        soupRecycleView.setAdapter(foodAdapter);
    }

    private void setFoodList5(List<Food> drink) {
        drinkRecycleView = findViewById(R.id.drinkRecycleView);
        foodAdapter = new FoodAdapter(this, drink);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        drinkRecycleView.setLayoutManager(layoutManager);
        drinkRecycleView.setAdapter(foodAdapter);
    }

    private void setFoodList4(List<Food> salad) {
        saladRecycleView = findViewById(R.id.saladRecycleView);
        foodAdapter = new FoodAdapter(this, salad);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        saladRecycleView.setLayoutManager(layoutManager);
        saladRecycleView.setAdapter(foodAdapter);
    }

    private void setFoodList3(List<Food> pizza) {
        pizzaRecycleView = findViewById(R.id.pizzaRecycleView);
        foodAdapter = new FoodAdapter(this, pizza);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        pizzaRecycleView.setLayoutManager(layoutManager);
        pizzaRecycleView.setAdapter(foodAdapter);
    }

    private void setFoodList2(List<Food> pasta) {
        pastaRecycleView = findViewById(R.id.pastaRecycleView);
        foodAdapter = new FoodAdapter(this, pasta);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        pastaRecycleView.setLayoutManager(layoutManager);
        pastaRecycleView.setAdapter(foodAdapter);
    }

    private void setFoodList1(List<Food> hotFood) {
        hotfoodRecycleView = findViewById(R.id.hotFoodRecycleView);
        foodAdapter = new FoodAdapter(this, hotFood);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        hotfoodRecycleView.setLayoutManager(layoutManager);
        hotfoodRecycleView.setAdapter(foodAdapter);
    }

    private void setCategoryList(List<Category> categoryList) {
        categoryRecycleView = findViewById(R.id.categoryRecycleView);
        categoryAdapter = new CategoryAdapter(this, categoryList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        categoryRecycleView.setLayoutManager(layoutManager);
        categoryRecycleView.setAdapter(categoryAdapter);
    }

    public static void showItemsByCategory(int category){ //плавная прокрутка
        switch(category){
            case 1:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)hotfoddImage.getX(), (int)hotfoddImage.getY());
                    }
                });
                break;
            case 2:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)pastaImage.getX(), (int)pastaImage.getY());
                    }
                });
                break;
            case 3:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)pizzaImage.getX(), (int)pizzaImage.getY());
                    }
                });
                break;
            case 4:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)saladImage.getX(), (int)saladImage.getY());
                    }
                });
                break;
            case 5:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)drinkImage.getX(), (int)drinkImage.getY());
                    }
                });
                break;
            case 6:
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo((int)soupImage.getX(), (int)soupImage.getY());
                    }
                });
                break;
        }
    }

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
    public void goToContacts(View view){
        Intent intent = new Intent(this, ContactActivity.class);
        if (city_name != null)
            intent.putExtra("city", city_name.getText());
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