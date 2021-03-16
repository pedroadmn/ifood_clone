package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import pedroadmn.ifoodclone.com.R;

public class AuthActivity extends AppCompatActivity {

    private Button btAccess;
    private EditText etEmail;
    private EditText etPassword;
    private Switch swAccess;
    private Switch swUserType;
    private LinearLayout llUserType;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initializeComponents();

        auth = FirebaseConfig.getAuthFirebase();

        swAccess.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                llUserType.setVisibility(View.VISIBLE);
            } else {
                llUserType.setVisibility(View.GONE);
            }
        });

        btAccess.setOnClickListener(v -> {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!email.isEmpty()) {
                if (!password.isEmpty()) {

                    //Verifica estado do switch
                    if (swAccess.isChecked()) {//Cadastro

                        auth.createUserWithEmailAndPassword(
                                email, password
                        ).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                                String userType = getUserType();
                                FirebaseUserHelper.updateUserType(userType);
                                openMainScreen(userType);
                            } else {
                                String erroExcecao = "";
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    erroExcecao = "Type a stronger password";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    erroExcecao = "Please, type a valid e-mail";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    erroExcecao = "This account is already registered";
                                } catch (Exception e) {
                                    erroExcecao = "Error ao cadastrar usuário: " + e.getMessage();
                                    e.printStackTrace();
                                }

                                Toast.makeText(AuthActivity.this, erroExcecao,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {//Login
                        auth.signInWithEmailAndPassword(
                                email, password
                        ).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this,
                                        "Successfully logged",
                                        Toast.LENGTH_SHORT).show();
                                String userType = task.getResult().getUser().getDisplayName();
                                openMainScreen(userType);
                            } else {
                                Toast.makeText(AuthActivity.this,
                                        "Error on login: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(AuthActivity.this,
                            "Fill the password!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AuthActivity.this,
                        "Fill the E-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeComponents() {
        btAccess = findViewById(R.id.btAccess);
        etEmail = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        swAccess = findViewById(R.id.switchAccess);
        swUserType = findViewById(R.id.switchUserType);
        llUserType = findViewById(R.id.llUserType);
    }

    private void verifyLoggedUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userType = currentUser.getDisplayName();
            openMainScreen(userType);
        }
    }

    private void openMainScreen(String userType) {
        if ("E".equals(userType)) {
            startActivity(new Intent(getApplicationContext(), CompanyActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private String getUserType() {
        return swUserType.isChecked() ? "E" : "U";
    }
}