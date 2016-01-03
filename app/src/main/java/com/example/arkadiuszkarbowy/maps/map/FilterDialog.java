package com.example.arkadiuszkarbowy.maps.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.arkadiuszkarbowy.maps.R;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class FilterDialog extends DialogFragment {


   private DialogInterface.OnClickListener mOnFilterListener;

    static FilterDialog newInstance(DialogInterface.OnClickListener onFilterListener) {
        FilterDialog dialog = new FilterDialog();
        dialog.setOnFilterListener(onFilterListener);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.filter_value))
                .setItems(FilterEnum.getTitlesArray(getActivity()), mOnFilterListener);
        return builder.create();
    }

    private void setOnFilterListener(DialogInterface.OnClickListener listener) {
        mOnFilterListener = listener;
    }
}
