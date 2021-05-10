package com.example.tellenceparking.layout;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tellenceparking.R;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;
import com.xwray.groupie.kotlinandroidextensions.ViewHolder;

public class ExpandableHeaderItem extends com.xwray.groupie.kotlinandroidextensions.Item implements ExpandableItem {

    private final String title;
    private ExpandableGroup expandableGroup;

    public ExpandableHeaderItem(String title) {
        this.title = title;
    }

    @Override
    public void setExpandableGroup(@NonNull ExpandableGroup onToggleListener) {
        expandableGroup = onToggleListener;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        ((TextView) viewHolder.itemView.findViewById(R.id.item_expandable_header_title)).setText(title);
        ((ImageView) viewHolder.itemView.findViewById(R.id.item_expandable_header_icon)).setImageResource(getRotatedIconResId());
        viewHolder.itemView.findViewById(R.id.item_expandable_header_root).setOnClickListener(v -> {
            expandableGroup.onToggleExpanded();
            ((ImageView) viewHolder.itemView.findViewById(R.id.item_expandable_header_icon)).setImageResource(getRotatedIconResId());
        });

    }

    @Override
    public int getLayout() {
        return R.layout.item_expandable_header;
    }

    private int getRotatedIconResId() {
        if ( expandableGroup.isExpanded()) {
            return R.drawable.ic_keyboard_arrow_up_black_24dp;
        } else {
            return R.drawable.ic_keyboard_arrow_down_black_24dp;
        }
    }

}
