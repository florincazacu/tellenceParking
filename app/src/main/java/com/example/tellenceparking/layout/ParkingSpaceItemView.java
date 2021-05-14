package com.example.tellenceparking.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.tellenceparking.R;
import com.xwray.groupie.kotlinandroidextensions.Item;
import com.xwray.groupie.kotlinandroidextensions.ViewHolder;

import java.util.Objects;

public class ParkingSpaceItemView extends Item {

    private final ParkingSpaceItem parkingSpaceItem;

    public ParkingSpaceItemView(ParkingSpaceItem parkingSpaceItem) {
        this.parkingSpaceItem = parkingSpaceItem;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.itemView.findViewById(R.id.item_number);
        textView.setText(parkingSpaceItem.getTitle());
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), parkingSpaceItem.getTitleColor()));

        ImageView imageView = viewHolder.itemView.findViewById(R.id.carImageView);
        imageView.setImageDrawable(getTintedDrawable(imageView.getContext(), R.drawable.car_icon, R.color.black));
        imageView.setVisibility(parkingSpaceItem.displayCarImage()? View.VISIBLE : View.INVISIBLE);
    }

    private Drawable getTintedDrawable(Context context, @DrawableRes int drawableRes, @ColorRes int colorRes) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        drawable = DrawableCompat.wrap(Objects.requireNonNull(drawable));
        DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, colorRes));
        return drawable;
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
