package com.timitoc.groupic.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Global;

/**
 * Created by timi on 01.05.2016.
 */
public class AddNewDialogBox extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose action")
                .setItems(R.array.add_new_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0 ){
                            System.out.println("Want to add folder");
                            AddNewDialogBox.this.getDialog().cancel();
                            new CreateFolderDialogBox().show(getFragmentManager(), "2");
                        }
                        else if (which == 1) {
                            System.out.println("Want to add image from gallery in folder " + Global.current_folder_id);
                            Global.addImage.run();
                        }
                        else {
                            Global.takePhoto.run();
                        }
                    }
                });
        return builder.create();

    }
}
