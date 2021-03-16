package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import helpers.FirebaseConfig;
import pedroadmn.ifoodclone.com.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        firebaseAuth = FirebaseConfig.getAuthFirebase();

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);

        MenuItem item = menu.findItem(R.id.searchMenu);
        searchView.setMenuItem(item);

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSettings() {
        startActivity(new Intent(this, UserSettingsActivity.class));
    }

    private void logout() {
        try {
            firebaseAuth.signOut();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void initializeComponents() {
        searchView = findViewById(R.id.svMain);
    }
}