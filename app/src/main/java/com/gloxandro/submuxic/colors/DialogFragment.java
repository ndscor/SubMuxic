package com.gloxandro.submuxic.colors;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;



/**
 * Created by HaKr on 12/06/16.
 */

public class DialogFragment extends AppCompatDialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!getShowsDialog()){
            return;
        }
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                try{
                    tintButtons(getDialog());
                } catch (Exception e){
                }
            }
        });
    }

    public static void tintButtons(Dialog dialog){
        TintUtils.tintButton(getButton(dialog, DialogInterface.BUTTON_POSITIVE));
        TintUtils.tintButton(getButton(dialog, DialogInterface.BUTTON_NEGATIVE));
        TintUtils.tintButton(getButton(dialog, DialogInterface.BUTTON_NEUTRAL));
    }

    private static Button getButton(Dialog dialog, int which){
        return ((AlertDialog)dialog).getButton(which);
    }
}
