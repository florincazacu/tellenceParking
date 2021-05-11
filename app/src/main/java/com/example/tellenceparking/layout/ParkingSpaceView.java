package com.example.tellenceparking.layout;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tellenceparking.R;
import com.xwray.groupie.kotlinandroidextensions.ViewHolder;

public class ParkingSpaceView extends com.xwray.groupie.kotlinandroidextensions.Item {

    private final int color;
    private final String number;

    public ParkingSpaceView(int color, String number) {
        this.color = color;
        this.number = number;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.itemView.findViewById(R.id.item_cardView).setBackgroundColor(color);
        ((TextView) viewHolder.itemView.findViewById(R.id.item_number))
                .setText(number);
    }

    @Override
    public int getLayout() {
        return R.layout.items;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount / 3;
    }
}
