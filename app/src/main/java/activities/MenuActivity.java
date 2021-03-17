package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapters.CompanyAdapter;
import adapters.ProductAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import listeners.RecyclerItemClickListener;
import models.Company;
import models.Order;
import models.OrderItem;
import models.Product;
import models.User;
import pedroadmn.ifoodclone.com.R;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView rvMenu;
    private CircleImageView civMenuCompanyImage;
    private TextView tvMenuCompanyName;
    private TextView tvQtt;
    private TextView tvTotalPrice;

    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();
    private List<OrderItem> carItems = new ArrayList<>();

    private DatabaseReference firebaseRef;

    private String loggedUserId;

    private Company company;

    private String companyId;

    private AlertDialog dialog;

    private User user;

    private Order recoveredOrder;

    private int qttCarItems = 0;
    private Double totalCar = 0.0;

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
                        confirmQuantity(position);
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

        getUserData();
    }

    private void confirmQuantity(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantity");
        builder.setMessage("Type the quantity");

        EditText etQuantity = new EditText(this);
        etQuantity.setText("1");
        etQuantity.setSelection(etQuantity.getText().length());

        builder.setView(etQuantity);

        builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
            String quantity = etQuantity.getText().toString();

            Product selectedProduct = products.get(position);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(selectedProduct.getProductId());
            orderItem.setProductName(selectedProduct.getName());
            orderItem.setQuantity(Integer.parseInt(quantity));
            orderItem.setPrice(selectedProduct.getPrice());

            carItems.add(orderItem);

            if (recoveredOrder == null) {
                recoveredOrder = new Order(loggedUserId, companyId);
            }

            recoveredOrder.setName(user.getName());
            recoveredOrder.setAddress(user.getAddress());
            recoveredOrder.setItems(carItems);
            recoveredOrder.save();

        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getUserData() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading...")
                .setCancelable(false)
                .build();

        dialog.show();

        DatabaseReference usersRef =  firebaseRef.child("users")
                .child(loggedUserId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    user = snapshot.getValue(User.class);
                }

                getOrder();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOrder() {
        DatabaseReference companiesRef = firebaseRef.child("orders_users")
                .child(companyId)
                .child(loggedUserId);

        companiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qttCarItems = 0;
                totalCar = 0.0;
                carItems.clear();

                if (snapshot.getValue() != null) {
                    recoveredOrder = snapshot.getValue(Order.class);

                    carItems = recoveredOrder.getItems();

                    for(OrderItem orderItem: carItems) {
                        int qtt = orderItem.getQuantity();
                        Double price = orderItem.getPrice();

                        totalCar += (qtt * price);
                        qttCarItems += qtt;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                tvQtt.setText("qtt: " + qttCarItems);
                tvTotalPrice.setText("R$ " + df.format(totalCar));
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }

    private void initializeComponents() {
        rvMenu = findViewById(R.id.rvMenu);
        civMenuCompanyImage = findViewById(R.id.civMenuCompany);
        tvMenuCompanyName = findViewById(R.id.tvMenuCompanyName);
        tvQtt = findViewById(R.id.tvQtt);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirmOrderMenu:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}