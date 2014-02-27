package fr.tvbarthel.attempt.googlyzooapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 27/02/14.
 */
public class LicenseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.dialog_license, null);

        if (dialogView != null) {
            final TextView textViewContent = (TextView) dialogView.findViewById(R.id.dialog_license_content);
            if (textViewContent != null) {
                textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
                Linkify.addLinks(textViewContent, Linkify.WEB_URLS);
            }
        }

        dialogBuilder.setCancelable(true)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.dialog_ok), null);
        return dialogBuilder.create();
    }
}
