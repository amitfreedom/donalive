package com.mobile.donalive.agora.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile.donalive.R;
import com.mobile.donalive.databinding.ListUserLiveJoinedBinding;
import com.mobile.donalive.modelclass.GoLiveModelClass;
import com.mobile.donalive.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserJoinedAdapter extends RecyclerView.Adapter<UserJoinedAdapter.ViewHolder> {

    private Context context;
    private List<GoLiveModelClass> goLiveModelClasses;
    private Click click;


    public UserJoinedAdapter(Context context, List<GoLiveModelClass> goLiveModelClasses, Click click) {
        this.context = context;
        this.goLiveModelClasses = goLiveModelClasses;
        this.click = click;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListUserLiveJoinedBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Glide.with(context).load(goLiveModelClasses.get(position).getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.binding.imgProfile);

        if (position == 0) {
            holder.binding.txtName.setText("Name: " + goLiveModelClasses.get(position).getName() + " (Host)");
            holder.binding.rlAccept.setVisibility(View.VISIBLE);
        } else {
            if (CommonUtils.Companion.getUserId().equalsIgnoreCase(goLiveModelClasses.get(position).getUserID())) {
                holder.binding.rlAccept.setVisibility(View.GONE);
                holder.binding.txtName.setText("Name: " + goLiveModelClasses.get(position).getName() + " (You)");
            } else {
                holder.binding.rlAccept.setVisibility(View.VISIBLE);
                holder.binding.txtName.setText("Name: " + goLiveModelClasses.get(position).getName());
            }
        }

        holder.binding.txtUserName.setText("ID: " + goLiveModelClasses.get(position).getUserName());

        holder.binding.rlAccept.setOnClickListener(view -> {
            click.setOnSendGiftListener(goLiveModelClasses.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return goLiveModelClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ListUserLiveJoinedBinding binding;

        public ViewHolder(@NonNull @NotNull ListUserLiveJoinedBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface Click {
        void setOnSendGiftListener(GoLiveModelClass goLiveModelClass);
    }
}
