package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import models.Order;
import models.OrderItem;
import pedroadmn.ifoodclone.com.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_adapter, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Order order = orders.get(i);
        holder.name.setText(order.getName());
        holder.address.setText("Endereço: " + order.getAddress());
        holder.observation.setText("Obs: " + order.getObservation());

        List<OrderItem> items = order.getItems();
        String itemDescription = "";

        int itemNumber = 1;
        Double total = 0.0;
        for (OrderItem orderItem : items) {

            int qtt = orderItem.getQuantity();
            Double price = orderItem.getPrice();
            total += (qtt * price);

            String name = orderItem.getProductName();
            itemDescription += itemNumber + ") " + name + " / (" + qtt + " x R$ " + price + ") \n";
            itemNumber++;
        }

        itemDescription += "Total: R$ " + total;
        holder.items.setText(itemDescription);

        int paymentMethod = order.getPaymentType();
        String payment = paymentMethod == 0 ? "Money" : "Credit Card";
        holder.payment.setText("Payment: " + payment);

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView payment;
        TextView observation;
        TextView items;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvOrderName);
            address = itemView.findViewById(R.id.tvOrderAddress);
            payment = itemView.findViewById(R.id.tvOrderPayment);
            observation = itemView.findViewById(R.id.tvOrderObs);
            items = itemView.findViewById(R.id.tvOrderItems);
        }
    }
}
