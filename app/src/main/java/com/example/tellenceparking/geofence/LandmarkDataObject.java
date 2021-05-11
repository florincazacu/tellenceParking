package com.example.tellenceparking.geofence;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class LandmarkDataObject {
    @NotNull
    private final String id;
    private final int name;
    @NotNull
    private final LatLng latLong;

    @NotNull
    public final String getId() {
        return this.id;
    }

    public final int getName() {
        return this.name;
    }

    @NotNull
    public final LatLng getLatLong() {
        return this.latLong;
    }

    public LandmarkDataObject(@NotNull String id, int name, @NotNull LatLng latLong) {
        super();
        this.id = id;
        this.name = name;
        this.latLong = latLong;
    }

    @NotNull
    public String toString() {
        return "LandmarkDataObject(id=" + this.id + ", name=" + this.name + ", latLong=" + this.latLong + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandmarkDataObject that = (LandmarkDataObject) o;
        return name == that.name &&
                id.equals(that.id) &&
                latLong.equals(that.latLong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latLong);
    }
}
