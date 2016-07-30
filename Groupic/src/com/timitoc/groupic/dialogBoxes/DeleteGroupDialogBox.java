package com.timitoc.groupic.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.timitoc.groupic.utils.interfaces.Consumer;

/**
 * Created by timi on 13.07.2016.
 */
public class DeleteGroupDialogBox extends DialogFragment {

    Consumer<Boolean> callback;

    public static DeleteGroupDialogBox newInstance(Consumer<Boolean> status) {
        DeleteGroupDialogBox f = new DeleteGroupDialogBox();
        Bundle args = new Bundle();
        args.putSerializable("consumer", status);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callback = (Consumer<Boolean>) getArguments().getSerializable("consumer");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Leave Group?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.accept(true);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.accept(false);
            }
        });
        return builder.create();
    }

}
