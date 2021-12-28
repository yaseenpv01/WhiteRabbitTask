package com.yaseen.whiterabbittask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    List<ModelClass> arrayList;

    public CustomAdapter(MainActivity context, List<ModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlistrow,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        ModelClass modelClass = arrayList.get(position);

        holder.name.setText(modelClass.getName());
        holder.companyName.setText(modelClass.getCompanyName());

        Glide.with(context)
                .load(modelClass.getImage())
                .into(holder.imageProfile);

    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, companyName;
        public ImageView imageProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.textViewName);
            companyName=itemView.findViewById(R.id.textViewCompanyName);
            imageProfile=itemView.findViewById(R.id.imageView);




        }
    }
}
