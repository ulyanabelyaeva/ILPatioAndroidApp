package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilpatio.CartActivity;
import com.example.ilpatio.MainActivity;
import com.example.ilpatio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Food;
import Models.Order;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static final String TAG = "llllllllllll";
    private Context context;
    private List<Integer> cart_list;

    private List<Food> foodListAll = MainActivity.getFullFoodList();

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private String id;

    public CartAdapter(Context context, List<Integer> cart_list) {
        this.context = context;
        this.cart_list = cart_list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardItem = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(cardItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        if (mAuth.getCurrentUser() != null){
            id = mAuth.getCurrentUser().getUid();
        }

        Food tempFood = null; //восстановление названия и цены по id товара (в адаптер передаются id)
        for (int j = 0; j < foodListAll.size(); j++){
            if (foodListAll.get(j).getId() == cart_list.get(position)){
                tempFood = foodListAll.get(j);
            }
        }
        if (tempFood != null){
            holder.name_cart_item.setText(tempFood.getTitle());
            holder.cost_cart_item.setText(tempFood.getCost() + " ₽");
        }

        holder.btn_delete_item.setOnClickListener(new View.OnClickListener() { //удаление товара из корзины и из бд
            @Override
            public void onClick(View view) {
                cart_list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                if (Order.cart.size() > 0){
                    int cost = 0;
                    for (int i = 0; i < Order.cart.size(); i++){
                        for(int j = 0; j < foodListAll.size(); j++){
                            if (foodListAll.get(j).getId() == Order.cart.get(i)){
                                cost = cost + foodListAll.get(j).getCost();
                                break;
                            }
                        }
                    }
                    CartActivity.count_cart.setText(Order.cart.size() + " товаров на " + cost + " ₽");
                } else {
                    CartActivity.count_cart.setText("Ваша корзина пуста");
                }

                Query userQuery = users.orderByChild("id").equalTo(id); //записи, у которых "id" = id
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // snapshot - бд users
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Map<String, Object> map = new HashMap<>();
                            map.put("cart", Order.cart);
                            ds.getRef().updateChildren(map); //обновление записи
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cart_list.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{

        private TextView name_cart_item, cost_cart_item;
        private Button btn_delete_item;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name_cart_item = itemView.findViewById(R.id.name_cart_item);
            cost_cart_item = itemView.findViewById(R.id.cost_cart_item);
            btn_delete_item = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
