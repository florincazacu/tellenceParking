package com.example.tellenceparking.geofence;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

public final class LandmarkDataObject {
    @NotNull
    private final String id;
    private final int hint;
    private final int name;
    @NotNull
    private final LatLng latLong;

    @NotNull
    public final String getId() {
        return this.id;
    }

    public final int getHint() {
        return this.hint;
    }

    public final int getName() {
        return this.name;
    }

    @NotNull
    public final LatLng getLatLong() {
        return this.latLong;
    }

    public LandmarkDataObject(@NotNull String id, int hint, int name, @NotNull LatLng latLong) {
        super();
        this.id = id;
        this.hint = hint;
        this.name = name;
        this.latLong = latLong;
    }

    @NotNull
    public final String component1() {
        return this.id;
    }

    public final int component2() {
        return this.hint;
    }

    public final int component3() {
        return this.name;
    }

    @NotNull
    public final LatLng component4() {
        return this.latLong;
    }

    @NotNull
    public final LandmarkDataObject copy(@NotNull String id, int hint, int name, @NotNull LatLng latLong) {
        Intrinsics.checkParameterIsNotNull(id, "id");
        Intrinsics.checkParameterIsNotNull(latLong, "latLong");
        return new LandmarkDataObject(id, hint, name, latLong);
    }

    // $FF: synthetic method
    public static LandmarkDataObject copy$default(LandmarkDataObject var0, String var1, int var2, int var3, LatLng var4, int var5, Object var6) {
        if ((var5 & 1) != 0) {
            var1 = var0.id;
        }

        if ((var5 & 2) != 0) {
            var2 = var0.hint;
        }

        if ((var5 & 4) != 0) {
            var3 = var0.name;
        }

        if ((var5 & 8) != 0) {
            var4 = var0.latLong;
        }

        return var0.copy(var1, var2, var3, var4);
    }

    @NotNull
    public String toString() {
        return "LandmarkDataObject(id=" + this.id + ", hint=" + this.hint + ", name=" + this.name + ", latLong=" + this.latLong + ")";
    }

    public int hashCode() {
        String var10000 = this.id;
        int var1 = (((var10000 != null ? var10000.hashCode() : 0) * 31 + this.hint) * 31 + this.name) * 31;
        LatLng var10001 = this.latLong;
        return var1 + (var10001 != null ? var10001.hashCode() : 0);
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof LandmarkDataObject) {
                LandmarkDataObject var2 = (LandmarkDataObject) var1;
                if (Intrinsics.areEqual(this.id, var2.id) && this.hint == var2.hint && this.name == var2.name && Intrinsics.areEqual(this.latLong, var2.latLong)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
