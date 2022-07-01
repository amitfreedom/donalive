package com.mobile.donalive.agora.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile.donalive.R;
import com.mobile.donalive.databinding.ListRequestMultiliveBinding;
import com.mobile.donalive.modelclass.GoLiveModelClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequestMultiLiveAdapter extends RecyclerView.Adapter<RequestMultiLiveAdapter.ViewHolder> {

    private Context context;
    private List<GoLiveModelClass> goLiveModelClasses;
    private Click click;

    public RequestMultiLiveAdapter(Context context, List<GoLiveModelClass> goLiveModelClasses, Click click) {
        this.context = context;
        this.goLiveModelClasses = goLiveModelClasses;
        this.click = click;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListRequestMultiliveBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Glide.with(context).load(goLiveModelClasses.get(position).getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.binding.imgProfile);

        holder.binding.txtName.setText("Name: " + goLiveModelClasses.get(position).getName());
        holder.binding.txtUserName.setText("ID: " + goLiveModelClasses.get(position).getUserName());


        holder.binding.rlAccept.setOnClickListener(view -> {
            click.setOnRequestAcceptListener(goLiveModelClasses.get(position));
        });

        if (goLiveModelClasses.get(position).getRequestStatus().equalsIgnoreCase("1")) {
            holder.binding.txtAccept.setText("Joined");
            holder.binding.rlRejected.setVisibility(View.GONE);
            holder.binding.rlRemove.setVisibility(View.VISIBLE);

        } else {
            holder.binding.rlRejected.setVisibility(View.VISIBLE);
            holder.binding.rlRemove.setVisibility(View.GONE);

        }


        holder.binding.rlRemove.setOnClickListener(view -> {
            click.setOnRemoveUserListener(goLiveModelClasses.get(position));
        });

        holder.binding.rlRejected.setOnClickListener(view -> {
            if (goLiveModelClasses.get(position).getRequestStatus().equalsIgnoreCase("0")) {
                click.setOnRequestRejectedListner(goLiveModelClasses.get(position));
            }
        });

        holder.binding.rlAccept.setOnClickListener(view -> {
            if (goLiveModelClasses.get(position).getRequestStatus().equalsIgnoreCase("0")) {
                click.setOnRequestAcceptListener(goLiveModelClasses.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return goLiveModelClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListRequestMultiliveBinding binding;

        public ViewHolder(@NonNull @NotNull ListRequestMultiliveBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface Click {
        void setOnRequestAcceptListener(GoLiveModelClass goLiveModelClass);

        void setOnRequestRejectedListner(GoLiveModelClass goLiveModelClass);

        void setOnRemoveUserListener(GoLiveModelClass goLiveModelClass);


    }

}
