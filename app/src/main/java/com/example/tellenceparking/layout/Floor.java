package com.example.tellenceparking.layout;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tellenceparking.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class Floor {

    private String title;
    private List<ParkingSpaceItemView> parkingSpaces = new ArrayList<>();

    public Floor(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ParkingSpaceItemView> getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(List<ParkingSpaceItemView> parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public static class Header extends Item<ViewHolder> {

        private final String title;

        public Header(String title) {
            this.title = title;
        }

        @Override
        public void bind(@NonNull com.xwray.groupie.ViewHolder viewHolder, int position) {
            ((TextView) viewHolder.itemView.findViewById(R.id.item_expandable_header_title)).setText(title);
        }

        @Override
        public int getLayout() {
            return R.layout.floor_header;
        }
    }
}
