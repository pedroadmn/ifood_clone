package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import models.Product;
import pedroadmn.ifoodclone.com.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{

    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_adapter, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Product product = products.get(i);
        holder.name.setText(product.getName());
        holder.description.setText(product.getDescription());
        holder.price.setText("R$ " + product.getPrice());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        TextView price;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvProductName);
            description = itemView.findViewById(R.id.tvProductDescription);
            price = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
