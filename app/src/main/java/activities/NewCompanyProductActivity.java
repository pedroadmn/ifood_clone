package activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import models.Company;
import models.Product;
import pedroadmn.ifoodclone.com.R;

public class NewCompanyProductActivity extends AppCompatActivity {

    private EditText etProductName;
    private EditText etProductDescription;
    private EditText etProductPrice;
    private Button btSaveProduct;

    private DatabaseReference firebaseRef;

    private String idLoggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company_product);

        initializeComponents();

        firebaseRef = FirebaseConfig.getFirebase();

        idLoggedUser = FirebaseUserHelper.getUserId();

        btSaveProduct.setOnClickListener(v -> saveProduct());

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("New Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeComponents() {
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        btSaveProduct = findViewById(R.id.btSaveProduct);
    }

    private void saveProduct() {
        validateProductData();
    }

    private void validateProductData() {
        String name = etProductName.getText().toString();
        String description = etProductDescription.getText().toString();
        String price = etProductPrice.getText().toString();

        if (!name.isEmpty()) {
            if (!description.isEmpty()) {
                if (!price.isEmpty()) {
                        Product product = new Product();
                        product.setUserId(idLoggedUser);
                        product.setName(name);
                        product.setDescription(description);
                        product.setPrice(Double.parseDouble(price));
                        product.save();
                        finish();
                        showToastMessage("Product successfully saved.");
                } else {
                    showToastMessage("Fill the product price!");
                }
            } else {
                showToastMessage("Fill the product description!");
            }
        } else {
            showToastMessage("Fill the product name!");
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(NewCompanyProductActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }
}