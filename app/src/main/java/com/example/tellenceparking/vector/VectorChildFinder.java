package com.example.tellenceparking.vector;

import android.content.Context;
import android.widget.ImageView;

public class VectorChildFinder {

    private final VectorDrawableCompat vectorDrawable;

    /**
     * @param context Your Activity Context
     * @param vectorRes Path of your vector drawable resource
     * @param imageView ImaveView that are showing vector drawable
     */
    public VectorChildFinder(Context context, int vectorRes, ImageView imageView) {
        vectorDrawable = VectorDrawableCompat.create(context.getResources(),
                vectorRes, null);
        imageView.setImageDrawable(vectorDrawable);
    }

    /**
     * @param pathName Path name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VFullPath
     */
    public VectorDrawableCompat.VFullPath findPathByName(String pathName) {
        return (VectorDrawableCompat.VFullPath) vectorDrawable.getTargetByName(pathName);
    }

    /**
     * @param groupName Group name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VGroup
     */
    public VectorDrawableCompat.VGroup findGroupByName(String groupName) {
        return (VectorDrawableCompat.VGroup) vectorDrawable.getTargetByName(groupName);
    }

}
