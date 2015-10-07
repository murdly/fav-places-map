package com.example.arkadiuszkarbowy.maps.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.arkadiuszkarbowy.maps.R;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class MarkerTitleDialog extends DialogFragment {

    private TitleListener mOnTitleSetListener;

    public interface TitleListener{
        void onResult(String title);
    }

    static MarkerTitleDialog newInstance(TitleListener onClickListener) {
        MarkerTitleDialog dialog = new MarkerTitleDialog();
        dialog.setOnTitleSetListener(onClickListener);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.set_name));
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int mpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                .getDisplayMetrics());
        params.setMargins(mpx, 0, mpx, 0);
        final EditText input = new EditText(getActivity());
        layout.addView(input, params);
        builder.setView(layout);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOnTitleSetListener.onResult(input.getText().toString());
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        return builder.create();
    }

    private void setOnTitleSetListener(TitleListener onClickListener) {
        mOnTitleSetListener = onClickListener;
    }
}
