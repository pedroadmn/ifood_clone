package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import models.Company;
import models.User;
import pedroadmn.ifoodclone.com.R;

public class UserSettingsActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etUserAddress;
    private Button btSaveUserInfo;

    private DatabaseReference firebaseRef;

    private String idLoggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        initializeComponents();

        firebaseRef = FirebaseConfig.getFirebase();

        idLoggedUser = FirebaseUserHelper.getUserId();

        btSaveUserInfo.setOnClickListener(v -> saveUserInfo());

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("User Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserInformation();
    }

    private void initializeComponents() {
        etUserName = findViewById(R.id.etUserName);
        etUserAddress = findViewById(R.id.etUserAddress);
        btSaveUserInfo = findViewById(R.id.btSaveUserInfo);
    }

    private void validateUserData() {
        String name = etUserName.getText().toString();
        String address = etUserAddress.getText().toString();

        if (!name.isEmpty()) {
            if (!address.isEmpty()) {
                User user = new User();
                user.setUserId(idLoggedUser);
                user.setName(etUserName.getText().toString());
                user.setAddress(etUserAddress.getText().toString());
                user.save();

                showToastMessage("User info successfully updated");
                finish();
            } else {
                showToastMessage("Fill the address!");
            }
        } else {
            showToastMessage("Fill the user name!");
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(UserSettingsActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void saveUserInfo() {
        validateUserData();
    }

    private void getUserInformation() {
        DatabaseReference usersRef = firebaseRef.child("users")
                .child(idLoggedUser);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    User user = snapshot.getValue(User.class);
                    etUserName.setText(user.getName());
                    etUserAddress.setText(user.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}