package com.example.chopinlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnProductCheckListener listener;

    public interface OnProductCheckListener {
        void onProductCheck(Product product, boolean isChecked);
    }

    public ProductAdapter(List<Product> productList, OnProductCheckListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.nameTextView.setText(product.getName());
        holder.quantityTextView.setText("Cantidad: " + product.getQuantity());
        holder.checkBox.setChecked(product.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onProductCheck(product, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView;
        CheckBox checkBox;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name);
            quantityTextView = itemView.findViewById(R.id.product_quantity);
            checkBox = itemView.findViewById(R.id.product_checkbox);
        }
    }
}

