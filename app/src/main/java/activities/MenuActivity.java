package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapters.CompanyAdapter;
import adapters.ProductAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import listeners.RecyclerItemClickListener;
import models.Company;
import models.Product;
import pedroadmn.ifoodclone.com.R;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView rvMenu;
    private CircleImageView civMenuCompanyImage;
    private TextView tvMenuCompanyName;

    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();

    private DatabaseReference firebaseRef;

    private String loggedUserId;

    private Company company;

    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initializeComponents();

        firebaseRef = FirebaseConfig.getFirebase();

        loggedUserId = FirebaseUserHelper.getUserId();

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            company = (Company) bundle.getSerializable("company");

            companyId = company.getIdUser();

            tvMenuCompanyName.setText(company.getName());
            String urlImage = company.getUrlImage();

            if (urlImage != null && !urlImage.equals("")) {
                Picasso.get().load(urlImage).into(civMenuCompanyImage);
            }
        }

        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.setHasFixedSize(true);
        productAdapter = new ProductAdapter(products, this);
        rvMenu.setAdapter(productAdapter);

        rvMenu.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvMenu,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        getProducts();
    }

    private void initializeComponents() {
        rvMenu = findViewById(R.id.rvMenu);
        civMenuCompanyImage = findViewById(R.id.civMenuCompany);
        tvMenuCompanyName = findViewById(R.id.tvMenuCompanyName);
    }

    private void getProducts() {
        DatabaseReference productsRef = firebaseRef.child("products").child(companyId);

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