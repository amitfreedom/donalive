package com.mobile.donalive.agora.firebase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mobile.donalive.databinding.ListGiftBinding;
import com.mobile.donalive.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    private Context context;
    private List<GiftsListModel.Detail> giftList;
    private Click click;


    public GiftAdapter(Context context, List<GiftsListModel.Detail> giftList, Click click) {
        this.context = context;
        this.giftList = giftList;
        this.click = click;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListGiftBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.binding.rlAll.setOnClickListener(view -> {
            click.setOnSendGiftListener(giftList.get(position));
        });

        Glide.with(context).load(giftList.get(position).getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.binding.progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.binding.progress.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.binding.imgGifts);
        holder.binding.txtAmount.setText(CommonUtils.Companion.prettyCount(Integer.valueOf(giftList.get(position).getAmount())));


    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ListGiftBinding binding;

        public ViewHolder(@NonNull @NotNull ListGiftBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface Click {
        void setOnSendGiftListener(GiftsListModel.Detail position);
    }
}
