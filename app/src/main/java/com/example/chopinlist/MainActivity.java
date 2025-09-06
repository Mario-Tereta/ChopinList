package com.example.chopinlist;

import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ProductDatabaseHelper dbHelper;
    private List<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new ProductDatabaseHelper(this);
        productList = loadProducts();
        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList, (product, isChecked) -> {
            product.setChecked(isChecked);
            updateProductChecked(product);
        });
        recyclerView.setAdapter(adapter);

        Button btnAddProduct = findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        Button btnFinalize = findViewById(R.id.btn_finalize);
        btnFinalize.setOnClickListener(v -> finalizeProducts());

        Button btnPendientes = findViewById(R.id.btn_pendientes);
        btnPendientes.setOnClickListener(v -> showPendientes());
        Button btnRealizadas = findViewById(R.id.btn_realizadas);
        btnRealizadas.setOnClickListener(v -> showRealizadas());
    }

    private List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_QUANTITY));
            boolean checked = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CHECKED)) == 1;
            boolean purchased = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_PURCHASED)) == 1;
            String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_PURCHASE_DATE));
            products.add(new Product(id, name, quantity, checked, purchased, purchaseDate));
        }
        cursor.close();
        db.close();
        return products;
    }

    private void saveProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductDatabaseHelper.COLUMN_NAME, product.getName());
        values.put(ProductDatabaseHelper.COLUMN_QUANTITY, product.getQuantity());
        values.put(ProductDatabaseHelper.COLUMN_CHECKED, product.isChecked() ? 1 : 0);
        values.put(ProductDatabaseHelper.COLUMN_PURCHASED, product.isPurchased() ? 1 : 0);
        values.put(ProductDatabaseHelper.COLUMN_PURCHASE_DATE, product.getPurchaseDate());
        long id = db.insert(ProductDatabaseHelper.TABLE_PRODUCTS, null, values);
        product.setId((int) id);
        db.close();
    }

    private void updateProductChecked(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductDatabaseHelper.COLUMN_CHECKED, product.isChecked() ? 1 : 0);
        db.update(ProductDatabaseHelper.TABLE_PRODUCTS, values, ProductDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(product.getId())});
        db.close();
    }

    private void updateProductStatus(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductDatabaseHelper.COLUMN_PURCHASED, product.isPurchased() ? 1 : 0);
        values.put(ProductDatabaseHelper.COLUMN_PURCHASE_DATE, product.getPurchaseDate());
        db.update(ProductDatabaseHelper.TABLE_PRODUCTS, values, ProductDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(product.getId())});
        db.close();
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editName = dialogView.findViewById(R.id.edit_product_name);
        EditText editQuantity = dialogView.findViewById(R.id.edit_product_quantity);
        Button btnAdd = dialogView.findViewById(R.id.btn_add_product);

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String quantityStr = editQuantity.getText().toString().trim();
            if (!name.isEmpty() && !quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                Product product = new Product(name, quantity);
                saveProduct(product);
                productList.add(product);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void finalizeProducts() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String date = sdf.format(new Date());
        for (Product product : productList) {
            if (product.isChecked()) {
                product.setPurchased(true);
                product.setPurchaseDate(date);
            } else {
                product.setPurchased(false);
                product.setPurchaseDate(null);
            }
            updateProductStatus(product);
        }
        adapter.notifyDataSetChanged();
    }

    private void showPendientes() {
        productList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS, null,
                ProductDatabaseHelper.COLUMN_PURCHASED + "=0", null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_QUANTITY));
            boolean checked = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CHECKED)) == 1;
            boolean purchased = false;
            String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_PURCHASE_DATE));
            productList.add(new Product(id, name, quantity, checked, purchased, purchaseDate));
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void showRealizadas() {
        productList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS, null,
                ProductDatabaseHelper.COLUMN_PURCHASED + "=1", null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_QUANTITY));
            boolean checked = cursor.getInt(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CHECKED)) == 1;
            boolean purchased = true;
            String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_PURCHASE_DATE));
            productList.add(new Product(id, name, quantity, checked, purchased, purchaseDate));
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        productList.clear();
        productList.addAll(loadProducts());
        adapter.notifyDataSetChanged();
    }
}