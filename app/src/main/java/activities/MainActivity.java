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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import adapters.CompanyAdapter;
import adapters.ProductAdapter;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import listeners.RecyclerItemClickListener;
import models.Company;
import models.Product;
import pedroadmn.ifoodclone.com.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;

    private RecyclerView rvCompanies;
    private CompanyAdapter companyAdapter;
    private List<Company> companies = new ArrayList<>();

    private DatabaseReference firebaseRef;

    private String loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        firebaseAuth = FirebaseConfig.getAuthFirebase();

        firebaseRef = FirebaseConfig.getFirebase();

        loggedUserId = FirebaseUserHelper.getUserId();

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        rvCompanies.setHasFixedSize(true);
        companyAdapter = new CompanyAdapter(companies, this);
        rvCompanies.setAdapter(companyAdapter);

        rvCompanies.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvCompanies,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Company selectedCompany = companies.get(position);

                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("company", selectedCompany);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        getCompanies();

        searchView.setHint("Search companies");

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCompanies(newText);
                return true;
            }
        });
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
        rvCompanies = findViewById(R.id.rvCompanies);
    }

    private void getCompanies() {
        DatabaseReference companiesRef = firebaseRef.child("companies");

        companiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    companies.add(ds.getValue(Company.class));
                }

                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchCompanies(String searchText) {
        DatabaseReference companiesRef = firebaseRef.child("companies");
        Query query = companiesRef.orderByChild("name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companies.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    companies.add(ds.getValue(Company.class));
                }

                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}