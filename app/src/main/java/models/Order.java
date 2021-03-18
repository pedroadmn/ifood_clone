package models;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import helpers.FirebaseConfig;

public class Order {

    private String userId;
    private String companyId;
    private String orderId;
    private String name;
    private String address;
    private List<OrderItem> items;
    private Double total;
    private String status = "pendent";
    private int paymentType;
    private String observation;

    public Order() {
    }

    public Order(String userId, String companyId) {
        setUserId(userId);
        setCompanyId(companyId);

        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference ordersRef = firebaseRef
                .child("orders_users")
                .child(companyId)
                .child(userId);

        setOrderId(ordersRef.push().getKey());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public void remove() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference ordersUsersRef = firebaseRef.child("orders_users").child(getCompanyId()).child(getUserId());
        ordersUsersRef.removeValue();
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference ordersUsersRef = firebaseRef.child("orders_users").child(getCompanyId()).child(getUserId());
        ordersUsersRef.setValue(this);
    }

    public void confirm() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebase();
        DatabaseReference ordersRef = firebaseRef.child("orders").child(getCompanyId()).child(getOrderId());
        ordersRef.setValue(this);
    }
}
