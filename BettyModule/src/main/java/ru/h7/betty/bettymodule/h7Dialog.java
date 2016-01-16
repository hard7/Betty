package ru.h7.betty.bettymodule;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

public class h7Dialog extends DialogFragment implements DialogInterface.OnClickListener {
    private NoticeDialogListener listener;
//    private DialogResponseFrom from;

    public enum DialogResponse {Good, Bad, Neutral, Cancel}
//    public enum DialogResponseFrom {Food, Sport};

    public interface NoticeDialogListener {
        void dialogResponse(DialogFragment fragment, DialogResponse response);
    }

    public void showFromFood() {
//        from = DialogResponseFrom.Food;
        show(getActivity().getFragmentManager(), "tag0");
    }

    public void showFromSport() {
//        from = DialogResponseFrom.Sport;
        show(getFragmentManager(), "tag");
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
//        dialogBuilder.setTitle("Hello User! How are you?");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(" ", this);
        dialogBuilder.setNegativeButton(" ", this);
        dialogBuilder.setNeutralButton(" ", this);
        final AlertDialog dialog = dialogBuilder.create();
        setupImages(dialog);
        return dialog;
    }

    static private void setupImages(final AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(R.mipmap.positive);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundResource(R.mipmap.negative);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundResource(R.mipmap.free_256);
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case android.app.Dialog.BUTTON_POSITIVE:
                listener.dialogResponse(this, DialogResponse.Good);
                break;
            case android.app.Dialog.BUTTON_NEGATIVE:
                listener.dialogResponse(this, DialogResponse.Bad);
            case Dialog.BUTTON_NEUTRAL:
                listener.dialogResponse(this, DialogResponse.Neutral);
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
        listener.dialogResponse(this, DialogResponse.Cancel);
    }


}
