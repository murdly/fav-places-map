package com.example.arkadiuszkarbowy.maps.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.DatabaseManager;
import com.example.arkadiuszkarbowy.maps.search.AutocompleteAdapter;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    public static final int REQUEST_ROUTE = 3;
    public static final int RESULT_ROUTE_LEGS = 33;
    public static final String ROUTE_LEGS = "route";
    private RouteController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mController = new RouteController(this, DatabaseManager.getInstance(), findViewById(R.id.legs));
        mController.setUpView();


        Button mAddLeg = (Button) findViewById(R.id.addLeg);
        mAddLeg.setOnClickListener(mOnAddLegListener);
        Button mCreate = (Button) findViewById(R.id.createRoute);
        mCreate.setOnClickListener(mOnCreateListener);
    }

    private View.OnClickListener mOnAddLegListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mController.addLeg();
        }
    };

    private View.OnClickListener mOnCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mController.validate()) {
                Intent data = new Intent();
                data.putParcelableArrayListExtra(ROUTE_LEGS, mController.getRoute());
                setResult(RESULT_ROUTE_LEGS, data);
                finish();
            } else {
                mController.msg();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
