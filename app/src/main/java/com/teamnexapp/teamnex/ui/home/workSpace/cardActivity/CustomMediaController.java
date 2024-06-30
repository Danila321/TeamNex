package com.teamnexapp.teamnex.ui.home.workSpace.cardActivity;

import android.content.Context;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {
    Context context;

    public CustomMediaController(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void show(int timeout) {
        super.show(0);
    }

    @Override
    public void hide() {

    }
}