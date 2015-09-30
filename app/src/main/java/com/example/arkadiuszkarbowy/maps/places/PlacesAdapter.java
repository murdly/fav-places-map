package com.example.arkadiuszkarbowy.maps.places;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.Place;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_STATE_VIEW = 0;
    private static final int CONTENT_VIEW = 1;
    private Context mContext;
    private List<Place> mPlaces;
    private ViewHolder.OnRecyclerItemClickListener mItemListener;
    private EmptyViewHolder.OnRecyclerEmptyStateClickListener mEmptyListener;

    public PlacesAdapter(Context context, List<Place> places, ViewHolder.OnRecyclerItemClickListener
            listener, EmptyViewHolder.OnRecyclerEmptyStateClickListener emptyListener) {
        mContext = context;
        mPlaces = places;
        mItemListener = listener;
        mEmptyListener = emptyListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mPlaces.isEmpty() ? EMPTY_STATE_VIEW : CONTENT_VIEW;
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private OnRecyclerEmptyStateClickListener mListener;
        public EmptyViewHolder(View v, OnRecyclerEmptyStateClickListener listener) {
            super(v);
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onAddPlaceClicked();
        }

        public interface OnRecyclerEmptyStateClickListener {
            void onAddPlaceClicked();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mAddress;
        private TextView mLocation;
        private ImageView mDelete;
        private OnRecyclerItemClickListener mListener;

        public ViewHolder(View v, OnRecyclerItemClickListener listener) {
            super(v);
            v.setOnClickListener(this);
            mListener = listener;
            mTitle = (TextView) v.findViewById(R.id.title);
            mAddress = (TextView) v.findViewById(R.id.address);
            mLocation = (TextView) v.findViewById(R.id.location);
            mDelete = (ImageView) v.findViewById(R.id.delete);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(v, this.getAdapterPosition());
        }

        public interface OnRecyclerItemClickListener {
            void onItemClicked(View view, int pos);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == EMPTY_STATE_VIEW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_places,
                    viewGroup, false);
            return new EmptyViewHolder(view, mEmptyListener);
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.places_item,
                viewGroup, false);
        return new ViewHolder(view, mItemListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) vh;
            Place place = mPlaces.get(position);
            holder.mTitle.setText(place.getTitle());
            holder.mAddress.setText(place.getAddress());
            holder.mLocation.setText(place.getLocationString());
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Places Adapter", position + " delete");
                }
            });
        }
        else{
            Log.d("Places Adapter", "empty state");
        }
    }

    @Override
    public int getItemCount() {
        return !mPlaces.isEmpty() ? mPlaces.size() : emptyStateCount();
    }

    private int emptyStateCount() {
        return 1;
    }
}