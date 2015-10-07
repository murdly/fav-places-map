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
    public static final int FILTER_RADIUS_500m = 500;
    public static final int FILTER_RADIUS_1000m = 1000;
    public static final int FILTER_RADIUS_2000m = 2000;
    public static final int FILTER_RADIUS_5000m = 5000;
    public static final int FILTER_RADIUS_DISABLED = -1;

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
                .setItems(getResources().getStringArray(R.array.filter_values), mOnFilterListener);
        return builder.create();
    }

    private void setOnFilterListener(DialogInterface.OnClickListener listener) {
        mOnFilterListener = listener;
    }
}
