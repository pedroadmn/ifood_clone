package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import models.Company;
import pedroadmn.ifoodclone.com.R;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.MyViewHolder> {

    private List<Company> companies;
    private Context context;

    public CompanyAdapter(List<Company> companies, Context context) {
        this.companies = companies;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_adapter, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Company company = companies.get(i);
        holder.companyName.setText(company.getName());
        holder.category.setText(company.getCategory() + " - ");
        holder.time.setText(company.getTime() + " Min");
        holder.delivery.setText("R$ " + company.getTax());

        //Carregar imagem
        String urlImagem = company.getUrlImage();

        if (urlImagem != null && !urlImagem.equals("")) {
            Picasso.get().load( urlImagem ).into( holder.civCompany);
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView civCompany;
        TextView companyName;
        TextView category;
        TextView time;
        TextView delivery;

        public MyViewHolder(View itemView) {
            super(itemView);

            companyName = itemView.findViewById(R.id.tvCompanyName);
            category = itemView.findViewById(R.id.tvCompanyCategory);
            time = itemView.findViewById(R.id.tvCompanyTime);
            delivery = itemView.findViewById(R.id.tvCompanyDelivery);
            civCompany = itemView.findViewById(R.id.civCompany);
        }
    }
}
