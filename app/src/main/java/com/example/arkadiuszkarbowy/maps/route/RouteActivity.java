package com.example.arkadiuszkarbowy.maps.route;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements RouteView {
    public static final int REQUEST_ROUTE = 3;
    public static final int RESULT_ROUTE_LEGS = 33;
    public static final String ROUTE_LEGS = "route";

    private RecyclerView mRouteListView;
    private RoutePresenter mPresenter;
    private RouteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button mAddLeg = (Button) findViewById(R.id.addLeg);
        mAddLeg.setOnClickListener(mOnAddLegListener);
        Button mCreate = (Button) findViewById(R.id.createRoute);
        mCreate.setOnClickListener(mOnCreateListener);

        mRouteListView = (RecyclerView) findViewById(R.id.legs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRouteListView.setLayoutManager(mLayoutManager);
        mRouteListView.addItemDecoration(new SpaceItemDecoration(24));

        mPresenter = new RoutePresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume(this);
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        }
    }

    private View.OnClickListener mOnAddLegListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.insertNextLeg();
        }
    };

    private View.OnClickListener mOnCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.onCreateRouteClicked();
        }
    };

    @Override
    public void setRouteList(ArrayList<Leg> routeLegs, RouteAdapter.OnLegListener legListener) {
        mAdapter = new RouteAdapter(routeLegs, legListener);
        mRouteListView.setAdapter(mAdapter);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishWithRoute(ArrayList<Leg> route) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(ROUTE_LEGS, route);
        setResult(RESULT_ROUTE_LEGS, data);
        finish();
    }

    @Override
    public void setAvailableDialogPlaces(List<MyPlace> places, RoutePresenterImpl.LegListener legListener) {
        AvailablePlacesDialog.newInstance(places, legListener).show(getFragmentManager(),
                getString(R.string.add_leg));
    }

    @Override
    public void onLegsStateChanged() {
        mAdapter.notifyDataSetChanged();
    }

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