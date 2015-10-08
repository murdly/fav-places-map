package com.example.arkadiuszkarbowy.maps.route;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class AvailablePlacesDialog extends DialogFragment {

    private List<MyPlace> mPlaces;
    private LegListener mOnLegChosenListener;

    public interface LegListener {
        void onLegChosen(int p);
    }

    static AvailablePlacesDialog newInstance(List<MyPlace> places, LegListener listener) {
        AvailablePlacesDialog dialog = new AvailablePlacesDialog();
        dialog.setDataSource(places);
        dialog.setOnLegChosenListener(listener);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.your_places))
                .setView(buildLayout());
        return builder.create();

    }

    private LinearLayout buildLayout() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int mpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                .getDisplayMetrics());
        params.setMargins(mpx, 0, mpx, mpx / 2);

        ListView list = (ListView) getActivity().getLayoutInflater().inflate(R.layout
                .dialog_places_available, null);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnLegChosenListener.onLegChosen(position);
                dismiss();
            }
        });
        list.setAdapter(new AvailablePlacesAdapter(getActivity(), mPlaces));
        layout.addView(list, params);
        return layout;
    }

    private void setDataSource(List<MyPlace> dataSource) {
        mPlaces = dataSource;
    }

    private void setOnLegChosenListener(LegListener onPlaceChooseListener) {
        mOnLegChosenListener = onPlaceChooseListener;
    }
}
