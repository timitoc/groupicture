package com.timitoc.groupic.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.SaveLocalManager;

/**
 * Created by timi on 01.07.2016.
 */
public class DeleteImageOnLocalDialogBox extends DialogFragment {

    boolean isSet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Do you want to remove this image from phone storage");

        builder.setMultiChoiceItems(new CharSequence[]{"Remove without asking"}, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                isSet = b;
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Global.setConfirm_delete_image_in_local(!isSet);
                SaveLocalManager.deletePrepared();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteImageOnLocalDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}