package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.OrderAdapter;
import adapters.ProductAdapter;
import dmax.dialog.SpotsDialog;
import helpers.FirebaseConfig;
import helpers.FirebaseUserHelper;
import listeners.RecyclerItemClickListener;
import models.Order;
import models.Product;
import pedroadmn.ifoodclone.com.R;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orders = new ArrayList<>();

    private DatabaseReference firebaseRef;

    private String loggedUserId;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        firebaseRef = FirebaseConfig.getFirebase();

        loggedUserId = FirebaseUserHelper.getUserId();

        Toolbar toolbar = findViewById(R.id.idToolbar);
        toolbar.setTitle("Orders");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeComponent();

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setHasFixedSize(true);
        orderAdapter = new OrderAdapter(orders);
        rvOrders.setAdapter(orderAdapter);

        rvOrders.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvOrders,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Order order = orders.get(position);

                        order.setStatus("Finished");
                        order.updateStatus();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        getOrders();
    }

    private void initializeComponent() {
        rvOrders = findViewById(R.id.rvOrders);
    }

    private void getOrders() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading...")
                .setCancelable(false)
                .build();

        dialog.show();

        DatabaseReference ordersRef = firebaseRef.child("orders").child(loggedUserId);

        Query orderSeach = ordersRef.orderByChild("status")
                .equalTo("Confirmed");

        orderSeach.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    orders.add(ds.getValue(Order.class));
                }

                orderAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }
}