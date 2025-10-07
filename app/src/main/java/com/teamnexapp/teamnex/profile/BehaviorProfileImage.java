package com.teamnexapp.teamnex.profile;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.teamnexapp.teamnex.R;

public class BehaviorProfileImage extends CoordinatorLayout.Behavior<View> {
    private float startX, startY;
    private float finalX, finalY;

    private boolean initialized = false;

    public BehaviorProfileImage() {
    }

    public BehaviorProfileImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float finalScale = 0.34f;

        if (!initialized) {
            initialized = true;

            // Начальные координаты
            startX = child.getX();
            startY = child.getY();

            // Конечные координаты
            float marginX = 23 * parent.getResources().getDisplayMetrics().density;
            float marginY = (86 * parent.getResources().getDisplayMetrics().density - child.getWidth() * finalScale) / 2;
            finalX = parent.getWidth() - child.getWidth() * (1f - finalScale) / 2 - child.getWidth() * finalScale - marginX;
            finalY = child.getWidth() * (1f - finalScale) / 2 * -1 + marginY;

        }

        // Прогресс схлопывания AppBar
        float totalScrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        float progress = Math.min(1f, Math.abs(dependency.getY()) / totalScrollRange);

        // Плавный переход позиции
        float newX = startX + (finalX - startX) * progress;
        float newY = startY - (startY - finalY) * progress;
        float newScale = 1f - (1f - finalScale) * progress;

        // Применяем
        child.setX(newX);
        child.setY(newY);
        child.setScaleX(newScale);
        child.setScaleY(newScale);

        float newButtonAlpha = 1 - progress;

        Button editButton = parent.findViewById(R.id.imageSettingsButton);
        editButton.setAlpha(newButtonAlpha);

        return true;
    }
}