package com.brebalki.friendme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Alexander on 11/18/2015.
 */
public class SelectDataDialogFragment extends DialogFragment {

    public ArrayList<String> mSelectedItems;
    public String items[], endItems[];
    public AlertDialog.Builder builder;

    public SelectDataDialogFragment() {
        super();
        builder = new AlertDialog.Builder(getActivity());
        mSelectedItems = new ArrayList<>();
        items = new String[5];
        items[0] = "Name";
        items[1] = "Phone Number";
        items[2] = "Email Address";
        items[3] = "Twitter";
        items[4] = "Facebook";
        //Set up the dialog
        //Set dialog title
        builder.setTitle("Select the information to receive");
        //Set the dialog buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        //http://developer.android.com/guide/topics/ui/dialogs.html
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return builder.create();
    }
}
