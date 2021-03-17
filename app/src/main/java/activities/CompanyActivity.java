package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.ProductAdapter;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import listeners.RecyclerItemClickListener;
import models.Product;
import pedroadmn.ifoodclone.com.R;

public class CompanyActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseRef;

    private String loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        firebaseRef = FirebaseConfig.getFirebase();

        loggedUserId = FirebaseUserHelper.getUserId();

        initializeComponents();

        firebaseAuth = FirebaseConfig.getAuthFirebase();

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Ifood - company");
        setSupportActionBar(toolbar);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setHasFixedSize(true);
        productAdapter = new ProductAdapter(products, this);
        rvProducts.setAdapter(productAdapter);

        rvProducts.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvProducts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Product selectedProduct = products.get(position);
                        selectedProduct.remove();
                        Toast.makeText(CompanyActivity.this, "Product successfully removed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        getProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.company_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                logout();
                finish();
                break;
            case R.id.configMenu:
                goToSettings();
                break;
            case R.id.newProductMenu:
                openNewProduct();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        try {
            firebaseAuth.signOut();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void goToSettings() {
        startActivity(new Intent(this, SettingsCompanyActivity.class));
    }

    private void openNewProduct() {
        startActivity(new Intent(this, NewCompanyProductActivity.class));
    }

    private void initializeComponents() {
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void getProducts() {
        DatabaseReference productsRef = firebaseRef.child("products").child(loggedUserId);

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    products.add(ds.getValue(Product.class));
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}