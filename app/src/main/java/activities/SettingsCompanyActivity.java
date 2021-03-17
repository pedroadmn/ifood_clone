package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import models.Company;
import pedroadmn.ifoodclone.com.R;

public class SettingsCompanyActivity extends AppCompatActivity {

    private CircleImageView civCompanyImage;
    private EditText etCompanyName;
    private EditText etCompanyCategory;
    private EditText etCompanyTime;
    private EditText etCompanyTax;
    private Button btSave;
    private static final int GALLERY_SELECTION = 200;

    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private String idLoggedUser;

    private String urlSelectedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        initializeComponents();

        storageReference = FirebaseConfig.getFirebaseStorage();
        firebaseRef = FirebaseConfig.getFirebase();

        idLoggedUser = FirebaseUserHelper.getUserId();

        civCompanyImage.setOnClickListener(v -> openCameraRoll());

        btSave.setOnClickListener(v -> saveCompany());

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Company Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCompanyInformation();
    }

    private void initializeComponents() {
        civCompanyImage = findViewById(R.id.civCompanyImage);
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyCategory = findViewById(R.id.etCompanyCategory);
        etCompanyTime = findViewById(R.id.etCompanyTime);
        etCompanyTax = findViewById(R.id.etCompanyTax);
        btSave = findViewById(R.id.btSave);
    }

    private void openCameraRoll() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            try {
                switch (requestCode) {
                    case GALLERY_SELECTION:
                        Uri localImage =  data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        break;
                }
            } catch (Exception exception) {

            }

            if (bitmap != null) {
                civCompanyImage.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();

                final StorageReference imageRef = storageReference.child("images")
                        .child("companies")
                        .child(idLoggedUser + ".jpeg");

                UploadTask uploadTask = imageRef.putBytes(imageData);

                uploadTask
                        .addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(SettingsCompanyActivity.this, "Success on upload image", Toast.LENGTH_SHORT).show();
                            imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                Uri url = task.getResult();
                                urlSelectedImage = url.toString();
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(SettingsCompanyActivity.this, "Error on upload image", Toast.LENGTH_SHORT).show());
            }

        }

    }

    private void validateCompanyData() {
        String name = etCompanyName.getText().toString();
        String category = etCompanyCategory.getText().toString();
        String time = etCompanyTime.getText().toString();
        String tax = etCompanyTax.getText().toString();

        if (!name.isEmpty()) {
            if (!category.isEmpty()) {
                if (!time.isEmpty()) {
                    if (!tax.isEmpty()) {
                        Company company = new Company();
                        company.setIdUser(idLoggedUser);
                        company.setName(name);
                        company.setCategory(category);
                        company.setTime(time);
                        company.setTax(Double.parseDouble(tax));
                        company.setUrlImage(urlSelectedImage);
                        company.save();
                        finish();
                    } else {
                        showToastMessage("Fill the company name!");
                    }
                } else {
                    showToastMessage("Fill the company category!");
                }
            } else {
                showToastMessage("Fill the company time!");
            }
        } else {
            showToastMessage("Fill the company tax!");
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(SettingsCompanyActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void saveCompany() {
        validateCompanyData();
    }

    private void getCompanyInformation() {
        DatabaseReference companiesRef = firebaseRef.child("companies")
                .child(idLoggedUser);

        companiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Company company = snapshot.getValue(Company.class);
                    etCompanyName.setText(company.getName());
                    etCompanyCategory.setText(company.getCategory());
                    etCompanyTime.setText(company.getTime());
                    etCompanyTax.setText(String.valueOf(company.getTax()));

                    urlSelectedImage = company.getUrlImage();

                    if (urlSelectedImage != null && !urlSelectedImage.equals("")) {
                        Picasso.get().load(urlSelectedImage).into(civCompanyImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}