package com.engine.test.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engine.test.R;
import com.engine.test.databinding.RecyclerItemBinding;
import com.engine.test.itemdetail.ItemDetail;
import com.engine.test.itemdetail.ItemDetailBottomSheet;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerAdapterViewHolder>{

    ArrayList<Item> items;
    ArrayList<ItemDetail> itemDetails;
    Activity activity;
    FragmentManager fragmentManager;

    public RecyclerAdapter(Activity activity, ArrayList<Item> items, ArrayList<ItemDetail> itemDetails, FragmentManager fragmentManager) {
        this.items = items;
        this.activity = activity;
        this.itemDetails = itemDetails;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBinding itemBinding = RecyclerItemBinding .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerAdapterViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterViewHolder holder, int position) {
        holder.bind(items.get(position), itemDetails.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder {

        RecyclerItemBinding itemBinding;

        public RecyclerAdapterViewHolder(RecyclerItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(Item item, ItemDetail itemDetail)
        {
            itemBinding.itemTxt.setText(item.getName());
            setImg(item.getId());
            itemBinding.itemField.setOnClickListener(v->
            {
                ItemDetailBottomSheet itemDetailBottomSheet = new ItemDetailBottomSheet(itemDetail);
                itemDetailBottomSheet.show(fragmentManager, "Dialog");
            });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setImg(int id)
        {
            switch (id)
            {
                case 2: itemBinding.itemImg.setImageDrawable(activity.getDrawable(R.drawable.basketball));
                    break;
                case 3: itemBinding.itemImg.setImageDrawable(activity.getDrawable(R.drawable.cricket));
                    break;
                case 4: itemBinding.itemImg.setImageDrawable(activity.getDrawable(R.drawable.mma));
                    break;
                case 6: itemBinding.itemImg.setImageDrawable(activity.getDrawable(R.drawable.soccer));
                    break;
                default:itemBinding.itemImg.setImageDrawable(activity.getDrawable(R.drawable.football_regby));
                    break;

            }
        }
    }
}
