package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilpatio.MainActivity;
import com.example.ilpatio.R;

import java.util.List;

import model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final String TAG = "llllllllllll";

    Context context; //страница с RecycleView
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //дизайн элементов
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(categoryItems);
        /*switch (viewType) {
            case 0:
                View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
                return new CategoryViewHolder(categoryItems);
            case 1:
            default:
                View categoryItems2 = LayoutInflater.from(context).inflate(R.layout.category_item_all, parent, false);
                return new CategoryViewHolder(categoryItems2);
        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //int viewType = getItemViewType(position);
        holder.getCategory().setText(categories.get(position).getTitle());

        holder.getCategory().setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                MainActivity.showItemsByCategory(categories.get(position).getId());
            }
        });
    }

    @Override
    public int getItemViewType(int position) { //тип дизайна
        if (position == 0) return 1;
        return 0;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder { //поиск элемента на макете

        private Button btn_category;
        public Button getCategory() { return btn_category; }

        public CategoryViewHolder(@NonNull View itemView) { //itemView - каждая категория
            super(itemView);
            btn_category = itemView.findViewById(R.id.btn_category);
        }
    }
}
