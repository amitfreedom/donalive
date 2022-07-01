package com.mobile.donalive.agora.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile.donalive.databinding.ListUsersBinding;
import com.mobile.donalive.modelclass.GoLiveModelClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewerAdapter extends RecyclerView.Adapter<ViewerAdapter.ViewHolder> {

    Context context;
    private List<GoLiveModelClass> list;
    private String liveStatus = "";
    private Click click;


    public ViewerAdapter(Context context, List<GoLiveModelClass> list, String liveStatus, Click click) {
        this.context = context;
        this.list = list;
        this.liveStatus = liveStatus;
        this.click = click;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListUsersBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        try {
            Glide.with(context).load(list.get(position).getImage()).into(holder.binding.imgProfile);
        } catch (Exception e) {

        }

        holder.itemView.setOnClickListener(view -> {
            if (!liveStatus.equalsIgnoreCase("host")) {
                click.onBanned(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ListUsersBinding binding;

        public ViewHolder(@NonNull @NotNull ListUsersBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface Click {
        void onBanned(int position);
    }
}
