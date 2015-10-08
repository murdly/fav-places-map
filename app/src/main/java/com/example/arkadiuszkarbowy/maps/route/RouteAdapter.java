package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arkadiuszkarbowy.maps.R;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private RouteList mRoute;
    private OnLegListener mRecyclerListener;

    public RouteAdapter(Context context, RouteList route, OnLegListener listener) {
        mContext = context;
        mRoute = route;
        mRecyclerListener = listener;
    }

    public interface OnLegListener {
        void onLegChoose(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPrefix;
        private TextView mTitle;
        private OnLegListener mListener;

        public ViewHolder(View v, OnLegListener listener) {
            super(v);
            v.setOnClickListener(this);
            mListener = listener;
            mPrefix = (TextView) v.findViewById(R.id.prefix);
            mTitle = (TextView) v.findViewById(R.id.leg);
        }

        @Override
        public void onClick(View v) {
                mListener.onLegChoose(this.getAdapterPosition());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_item,
                viewGroup, false);
        return new ViewHolder(view, mRecyclerListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        ViewHolder holder = (ViewHolder) vh;
        final RouteLeg leg = mRoute.getLegs().get(position);
        holder.mPrefix.setText(leg.getPrefix());
        holder.mTitle.setText(leg.getTitle());
    }

    @Override
    public int getItemCount() {
        return mRoute.getLegsCount();
    }
}