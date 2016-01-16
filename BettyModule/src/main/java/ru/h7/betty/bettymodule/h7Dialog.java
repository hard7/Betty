package ru.h7.betty.bettymodule;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class h7Dialog extends DialogFragment implements DialogInterface.OnClickListener {
    private NoticeDialogListener listener;

    public enum DialogResponseType {Good, Bad, Neutral, Cancel};

    public interface NoticeDialogListener {
        public void dialogResponse(DialogResponseType response);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Hello User! How are you?");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(" ", this);
        dialogBuilder.setNegativeButton(" ", this);
        final AlertDialog dialog = dialogBuilder.create();
        setupImages(dialog);
        return dialog;
    }

    static private void setupImages(final AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setBackgroundResource(R.mipmap.positive);
                Button buttonNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonNegative.setBackgroundResource(R.mipmap.negative);
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case android.app.Dialog.BUTTON_POSITIVE:
                listener.dialogResponse(DialogResponseType.Good);
                break;
            case android.app.Dialog.BUTTON_NEGATIVE:
                listener.dialogResponse(DialogResponseType.Bad);
        }
    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        Toast.makeText(getActivity(), "onDismiss", Toast.LENGTH_SHORT).show();
//    }
//
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        listener.dialogResponse(DialogResponseType.Cancel);
    }


}
