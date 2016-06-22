package com.timitoc.groupic.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.SaveLocalManager;

/**
 * Created by timi on 22.06.2016.
 */
public class SaveImageOnLocalDialogBox extends DialogFragment {

    boolean isSet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure ?").setMessage("Do you want to download this image to your phone storage");

        builder.setMultiChoiceItems(new CharSequence[]{"Download without asking"}, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    isSet = true;
                }
                else
                    isSet = false;
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Global.confirm_save_image_on_local = !isSet;
                SaveLocalManager.savePrepared();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SaveImageOnLocalDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
