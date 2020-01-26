package com.gloxandro.submuxic.colors;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class DialogBuilder extends DialogCommonBuilder {

    public DialogBuilder(Context context) {
        super(context);
    }

    @Override
    public void showDialog() {
        show();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public Dialog create() {
        return super.create();
    }
}
