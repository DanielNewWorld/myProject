package ua.in.nets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FireMissilesDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("База даних оновлена!")
                .setPositiveButton("Добре", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
                //.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                //    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                //    }
                //});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
