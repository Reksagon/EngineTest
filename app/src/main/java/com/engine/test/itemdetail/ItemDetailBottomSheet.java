package com.engine.test.itemdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.engine.test.R;
import com.engine.test.databinding.ItemDetailBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ItemDetailBottomSheet extends BottomSheetDialogFragment {

    ItemDetailBottomSheetBinding binding;
    ItemDetail itemDetail;

    public ItemDetailBottomSheet(ItemDetail itemDetail) {
        this.itemDetail = itemDetail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ItemDetailBottomSheetBinding.inflate(inflater, container, false);

        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.MyDialog);

        binding.nameTxt.setText(itemDetail.getName());
        binding.adresTxt.setText(itemDetail.getAddress());
        binding.phoneTxt.setText(itemDetail.getPhone());
        if(!itemDetail.getPrice().equals(""))
            binding.priceTxt.setText(itemDetail.getPrice());
        else
            binding.priceTxt.setVisibility(View.GONE);

        return binding.getRoot();
    }
}
