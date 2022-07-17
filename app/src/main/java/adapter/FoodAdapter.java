package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import model.Food;
import model.Order;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    Context context;
    List<Food> foodList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private String id;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View foodItems = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(foodItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, @SuppressLint("RecyclerView") int position) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        if (mAuth.getCurrentUser() != null)
            id = mAuth.getCurrentUser().getUid();

        holder.title.setText(foodList.get(position).getTitle());
        holder.description.setText(foodList.get(position).getDescription());
        String cost = String.valueOf(foodList.get(position).getCost());
        holder.cost.setText(cost);

        int imageId =context.getResources().getIdentifier(foodList.get(position).getImg(), "drawable", context.getPackageName());
        holder.image.setBackgroundResource(imageId);

        holder.btn_addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null){ //добавление товара в корзину и в БД
                    Order.cart.add(foodList.get(position).getId());

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

                    Toast.makeText(context, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Авторизуйтесь, чтобы добавлять товары в корзину", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        private TextView title, description, cost;
        private RelativeLayout image;
        private Button btn_addToCart;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleFood);
            description = itemView.findViewById(R.id.descriptionFood);
            cost = itemView.findViewById(R.id.costFood);
            image = itemView.findViewById(R.id.imageFood);
            btn_addToCart = itemView.findViewById(R.id.btn_addToCart);
        }
    }
}
