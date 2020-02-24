package com.inatel.avaliao_pratica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CandidatoAdapter extends RecyclerView.Adapter<CandidatoAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Candidato> cand;
    private ArrayList<Candidato> backup;
    public  CandidatoAdapter(Context c,ArrayList<Candidato> cand){
        this.context = c;
        this.cand = cand;
        backup = new ArrayList<Candidato>(cand);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidato_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Candidato cand = this.cand.get(position);
        holder.nome.setText(cand.nome);
        holder.email.setText(cand.email);
        holder.tel.setText(cand.tel);
        holder.prinhab.setText(cand.princHab);
        Picasso.with(context)
                .load(cand.fotoDoCandidato)
                .into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return cand.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome,email,tel,prinhab;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
         nome= itemView.findViewById(R.id.txNome);
         email = itemView.findViewById(R.id.txtemai);
         tel = itemView.findViewById(R.id.txtTel);
         prinhab = itemView.findViewById(R.id.txtPrincHan);
         foto = itemView.findViewById(R.id.Candidato);
        }
    }
    @Override
    public Filter getFilter() {
        return filtro;
    }
    private Filter filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Candidato> listaFiltrada = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                listaFiltrada.addAll(backup);
            }else{
                String filterPatter = constraint.toString().toLowerCase().trim();
                for (Candidato can: backup){
                    if(can.getNome().toLowerCase().contains(filterPatter)){
                        listaFiltrada.add(can);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = listaFiltrada;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
         cand.clear();
         cand.addAll((List)results.values);
         notifyDataSetChanged();
        }
    };
}
