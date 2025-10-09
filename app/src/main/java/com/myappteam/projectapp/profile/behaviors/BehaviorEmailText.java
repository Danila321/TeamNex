package com.myappteam.projectapp.profile.behaviors;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class BehaviorEmailText extends CoordinatorLayout.Behavior<TextView> {
    private float startY, finalY;

    private boolean initialized = false;

    public BehaviorEmailText(){

    }

    public BehaviorEmailText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        if (!initialized){
            initialized = true;

            startY = child.getY();

            finalY = 190;
        }

        float totalScrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        float progress = Math.min(1f, Math.abs(dependency.getY()) / totalScrollRange);

        float newY = startY - (startY - finalY) * progress;
        float newAlpha = 1 - progress;

        child.setY(newY);
        child.setAlpha(newAlpha);

        return true;
    }
}
