package com.example.ilpatio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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

import Adapters.CategoryAdapter;
import Adapters.FoodAdapter;
import Models.Category;
import Models.Food;
import Models.Order;
import Models.User;

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

        List<Category> categoryList = new ArrayList<>();
        /*categoryList.add(new Category(0, ""));*/
        categoryList.add(new Category(1, "Горячие блюда"));
        categoryList.add(new Category(2, "Паста"));
        categoryList.add(new Category(3, "Пицца"));
        categoryList.add(new Category(4, "Салаты и закуски"));
        categoryList.add(new Category(5, "Напитки"));
        categoryList.add(new Category(6, "Супы и хлеб"));
        setCategoryList(categoryList);

        List<Food> hotFood = new ArrayList<>();
        hotFood.add(new Food(1, 675, 1, "hotfood1", "Скалопини из курицы", "Нежное куриное филе в сливочно-грибном соусе подается с обжаренными дольками картофеля."));
        hotFood.add(new Food(2, 675, 1, "hotfood2", "Миланезе де Полло", "Большая котлета из рубленой куриной грудки в панировке запекается с томатным соусом под сыром моцарелла и подается с картофельными дольками, карамелизированной морковью."));
        hotFood.add(new Food(3, 675, 1, "hotfood3", "Медальоны аль базилико", "Медальоны из свинины в сливочном соусе с песто. Подаём с картофельным пюре и томатами черри – очень удачная компания."));
        setFoodList1(hotFood);

        List<Food> pasta = new ArrayList<>();
        pasta.add(new Food(4, 699, 2, "pasta1", "Пенне IL Патио в сливочном соусе", "Фирменный сливочный соус с креветками, помидорами и перцем чили."));
        pasta.add(new Food(5, 585, 2, "pasta2", "Пенне Верде", "Цвет настроения – зелёный. Пенне со шпинатом, цукини, спаржей, брокколи и бобами эдамаме в соусе с песто."));
        pasta.add(new Food(6, 699, 2, "pasta3", "Пенне IL Патио в томатном соусе", "Наша гордость и любовь – паста с тигровыми креветками, щупальцами кальмара в томатном соусе с вялеными томатами и базиликом."));
        pasta.add(new Food(7, 599, 2, "pasta4", "Пенне с цыплёнком и песто", "Сливочный соус с добавлением шпината и песто, маринованной куриной грудкой BBQ, помидорами черри и сыром."));
        pasta.add(new Food(8, 399, 2, "pasta5", "Пенне Арабьята ", "Классическая паста с пикантным томатным соусом, шампиньонами, чесноком и петрушкой. Арабьята в переводе с итальянского означает «сердитый», намек на то, что паста острая."));
        pasta.add(new Food(9, 599, 2, "pasta6", "Пенне с ветчиной и грибами", "Паста с шампиньонами и ветчиной в сливочном соусе."));
        pasta.add(new Food(10, 599, 2, "pasta7", "Мясная лазанья", "Паста с соусом болоньезе, запечённая с моцареллой и пармезаном. Сытно, чётко, насыщенно."));
        setFoodList2(pasta);

        List <Food> pizza = new ArrayList<>();
        pizza.add(new Food(11, 699, 3, "pizza1", "Пиццетта Кватро Формаджи", "Классическая итальянская пиццетта с пятью видами сыра. Buon appetito!"));
        pizza.add(new Food(12, 699, 3, "pizza2", "IL Патио", "Фирменная пицца с сыром моцарелла, ветчиной, шампиньонами, пикантной колбасой пепперони, беконом,мюнхенскими колбасками и сладким перцем."));
        pizza.add(new Food(13, 699, 3, "pizza3", "Фрутти ди Маре с моцареллой", "Для особых случаев. Креветки, кольца и щупальца кальмара, фирменный томатный соус, моцарелла, черри и руккола."));
        pizza.add(new Food(14, 899, 3, "pizza4", "Барбекю", "Пицца с сочным цыпленком и свининой BBQ, беконом, шампиньонами, луком бальзамик, моцареллой."));
        pizza.add(new Food(15, 699, 3, "pizza5", "Кватро Формаджи", "Классическая итальянская пиццетта с пятью видами сыра. Buon appetito!"));
        pizza.add(new Food(16, 599, 3, "pizza6", "Мексикана", "Острая пицца с пепперони, соусом болоньез, томатным соусом, перчиком халапеньо и луком, сыром моцарелла, беконом, тушеным луком и петрушкой."));
        pizza.add(new Food(17, 599, 3, "pizza7", "Примавера", "Настроение: весна (и какая разница, что там за окном). Черри, артишоки, цукини, микс сладкого перца, песто, лук, грибы и томатный соус."));
        pizza.add(new Food(18, 599, 3, "pizza8", "Ветчина и грибы", "Шампиньоны, моцарелла, фирменный томатный соус, орегано и ветчина. Ничего лишнего, только любимое."));
        setFoodList3(pizza);

        List <Food> salad = new ArrayList<>();
        salad.add(new Food(19, 699, 4, "salad1", "Салат с ростбифом", "Салат с запечёной говядиной, артишоками, вялеными томатами и листьями салата в пряном соусе с чили. Заслуживает пристального внимания."));
        salad.add(new Food(20,  695, 4, "salad2", "Цезарь с креветками", "Отборные листья Ромейна и Айсберга, помидоры черри, перепелиные яйца, гренки, под фирменным соусом Цезарь. А еще креветки - настоящий афродизиак. Цезарь бы оценил!"));
        salad.add(new Food(21, 599, 4, "salad3", "Цезарь с куриной грудкой", "Вкус этого салата знают во всем мире: листья Ромейна и Айсберга, гренки из тосканского хлеба, кусочки сочной куриной грудки, перепелиные яйца, пармезан и пикантный соус. Да здравствует Цезарь!"));
        salad.add(new Food(22, 585, 4, "salad4", "Зеленый салат с эдамаме и моцареллой", "Заряд витаминов: свежий шпинат, бобы эдамаме, брокколи, кабачки, огурцы и моцарелла под соусом из маракуйи."));
        salad.add(new Food(23, 579, 4, "salad5", "Греческий салат", "Легенда. Спелые томаты, сладкий перец, огурцы, красный лук, оливки и сыр фета. Заправляется оливковым маслом и бальзамико."));
        setFoodList4(salad);

        List <Food> drink = new ArrayList<>();
        drink.add(new Food(24, 495, 5, "drink1", "Морковный фреш", "Свежевыжатый морковный сок."));
        drink.add(new Food(25, 495, 5, "drink2", "Апельсиновый фреш", "Свежевыжатый апельсиновый сок."));
        drink.add(new Food(26, 365, 5, "drink3", "Кокосовый смузи с Манго", "Нежный смузи на основе спелого манго, банана, кокосового молока и ананасового сока."));
        drink.add(new Food(27, 365, 5, "drink4", "Смузи Манго и Мята 0,4 л", "Манго, свежая мята, банан и яблочный сок."));
        drink.add(new Food(28, 259, 5, "drink5", "Лимонад Крем Сода 0,5 л", "Лимонад с нежным сливочным вкусом, который образуется благодаря сочетанию апельсина, лимона, содовой и ванильно-карамельного сиропа."));
        drink.add(new Food(29, 259, 5, "drink6", "Лимонад Тархун 0,5 л", "Пряный тархун в сочетании с лимонным и яблочным соками и газированной водой."));
        drink.add(new Food(30, 259, 5, "drink7", "Имбирный лимонад 0,5 л", "Утоляющее жажду сочетание имбиря, лимона и спрайта."));
        drink.add(new Food(31, 195, 5, "drink8", "Сок Яблочный 200 мл", ""));
        drink.add(new Food(32, 195, 5, "drink9", "Сок Персиковый 200 мл", ""));
        drink.add(new Food(33, 195, 5, "drink10", "Сок Апельсиновый 200 мл", ""));
        drink.add(new Food(34, 195, 5, "drink11", "Сок Томатный 200 мл", ""));
        drink.add(new Food(35, 195, 5, "drink12", "Сок Вишневый 200 мл ", ""));
        setFoodList5(drink);

        List <Food> soup = new ArrayList<>();
        soup.add(new Food(36, 195, 6, "soup1", "Суп с чечевицей и овощами", "Классический итальянский суп с красной фасолью и овощами, который мы заправляем соусом песто и щепоткой волшебства по рецепту любимой бабушки шеф-повара. Подходит для вегетарианцев."));
        soup.add(new Food(37, 595, 6, "soup2", "Домашняя похлебка с морепродуктами", "Наваристый бульон с кальмарами, мидиями, креветками, каперсами и овощами. Все на борт!"));
        soup.add(new Food(38, 525, 6, "soup3", "Семейный очаг ", "Сытный мясной суп с говядиной, курицей, свининой и овощами. Заботливо согревает и обволакивает."));
        soup.add(new Food(39, 299, 6, "soup4", "Фокачча", "Лепешка, выпеченная в дровяной печи из нежнейшего теста на основе муки твердых сортов с добавленем чеснока, соуса песто и ароматного сыра."));
        soup.add(new Food(40, 225, 6, "soup5", "Чесночный хлеб", "Хрустящий багет, запеченный с чесночным маслом и зеленью."));
        setFoodList6(soup);

        fullFoodList.addAll(hotFood);
        fullFoodList.addAll(pasta);
        fullFoodList.addAll(pizza);
        fullFoodList.addAll(salad);
        fullFoodList.addAll(drink);
        fullFoodList.addAll(soup);
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