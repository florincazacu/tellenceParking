package com.example.tellenceparking.geofence;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.tellenceparking.R;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public final class GeofenceViewModel extends ViewModel {

    private final MutableLiveData<Integer> geofenceIndex;
    private final MutableLiveData<Integer> hintIndex;
    @NotNull
    private final LiveData<Integer> geofenceHintResourceId;
    @NotNull
    private final LiveData<Integer> geofenceImageResourceId;

    @NotNull
    public final LiveData<Integer> getGeofenceIndex() {
        return geofenceIndex;
    }

    @NotNull
    public final LiveData<Integer> getGeofenceHintResourceId() {
        return geofenceHintResourceId;
    }

    @NotNull
    public final LiveData<Integer> getGeofenceImageResourceId() {
        return geofenceImageResourceId;
    }

    public final void updateHint(int currentIndex) {
        this.hintIndex.setValue(currentIndex + 1);
    }

    public final void geofenceActivated() {
        this.geofenceIndex.setValue(this.hintIndex.getValue());
    }

    public final boolean geofenceIsActive() {
        return Intrinsics.areEqual(this.geofenceIndex.getValue(), this.hintIndex.getValue());
    }

    public final int nextGeofenceIndex() {
        Integer var10000 = this.hintIndex.getValue();
        if (var10000 == null) {
            var10000 = 0;
        }

        return var10000;
    }

    public GeofenceViewModel(@NotNull SavedStateHandle state) {
        super();
        Intrinsics.checkParameterIsNotNull(state, "state");
        this.geofenceIndex = state.getLiveData("geofenceIndex", -1);
        this.hintIndex = state.getLiveData("hintIndex", 0);
        LiveData var2 = Transformations.map(this.getGeofenceIndex(), (new Function() {
            // $FF: synthetic method
            // $FF: bridge method
            public Object apply(Object var1) {
                return this.apply((Integer) var1);
            }

            public final int apply(Integer it) {
                Integer var3;
                label24:
                {
                    LiveData<Integer> geofenceIndex = GeofenceViewModel.this.getGeofenceIndex();
                    if (geofenceIndex != null) {
                        var3 = geofenceIndex.getValue();
                        if (var3 != null) {
                            break label24;
                        }
                    }

                    var3 = -1;
                }

                Intrinsics.checkExpressionValueIsNotNull(var3, "geofenceIndex?.value ?: -1");
                int index = var3;
                int var4;
                if (index < 0) {
                    var4 = R.string.not_started_hint;
                } else if (index < GeofencingConstants.INSTANCE.getNUM_LANDMARKS()) {
                    LandmarkDataObject[] var5 = GeofencingConstants.INSTANCE.getLANDMARK_DATA();
                    Object var10001 = GeofenceViewModel.this.getGeofenceIndex().getValue();
                    if (var10001 == null) {
                        Intrinsics.throwNpe();
                    }

                    Intrinsics.checkExpressionValueIsNotNull(var10001, "geofenceIndex.value!!");
                    var4 = var5[((Number) var10001).intValue()].getHint();
                } else {
                    var4 = 1900091;
                }

                return var4;
            }
        }));
        Intrinsics.checkExpressionValueIsNotNull(var2, "Transformations.map(geof…ence_over\n        }\n    }");
        this.geofenceHintResourceId = var2;
        var2 = Transformations.map(this.getGeofenceIndex(), (Function) (new Function() {
            // $FF: synthetic method
            // $FF: bridge method
            public Object apply(Object var1) {
                return this.apply((Integer) var1);
            }

            public final int apply(Integer it) {
                Integer var10000 = (Integer) GeofenceViewModel.this.getGeofenceIndex().getValue();
                if (var10000 == null) {
                    var10000 = -1;
                }

                Intrinsics.checkExpressionValueIsNotNull(var10000, "geofenceIndex.value ?: -1");
                int index = var10000;
                return index < GeofencingConstants.INSTANCE.getNUM_LANDMARKS() ? 700057 : 700085;
            }
        }));
        Intrinsics.checkExpressionValueIsNotNull(var2, "Transformations.map(geof…_treasure\n        }\n    }");
        this.geofenceImageResourceId = var2;
    }
}

