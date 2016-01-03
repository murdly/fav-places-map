package com.example.arkadiuszkarbowy.maps.places;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 29/09/15.
 */
public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int EMPTY_STATE_VIEW = 0;
    private final int CONTENT_VIEW = 1;
    private List<MyPlace> mMyPlaces;
    private OnRecyclerInteraction mListener;

    public PlacesAdapter(List<MyPlace> myPlaces, OnRecyclerInteraction listener) {
        mMyPlaces = myPlaces;
        mListener = listener;
    }

    public MyPlace getItemAt(int position) {
        return mMyPlaces.get(position);
    }

    public interface OnRecyclerInteraction {
        void onEmptyStateClicked();

        void moveToPlaceMarker(int position);

        void onItemDeleteClicked(long id);
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnRecyclerInteraction mListener;

        public EmptyViewHolder(View v, OnRecyclerInteraction listener) {
            super(v);
            //todo must be set in onBindViewHolder!
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onEmptyStateClicked();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mAddress;
        private TextView mLocation;
        private ImageView mDelete;
        private OnRecyclerInteraction mListener;

        public ViewHolder(View v, OnRecyclerInteraction listener) {
            super(v);
            //todo must be set in onBindViewHolder!
            v.setOnClickListener(this);
            mListener = listener;
            mTitle = (TextView) v.findViewById(R.id.title);
            mAddress = (TextView) v.findViewById(R.id.address);
            mLocation = (TextView) v.findViewById(R.id.location);
            mDelete = (ImageView) v.findViewById(R.id.delete);
        }

        @Override
        public void onClick(View v) {
            mListener.moveToPlaceMarker(this.getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMyPlaces.isEmpty() ? EMPTY_STATE_VIEW : CONTENT_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case EMPTY_STATE_VIEW:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_places,
                        viewGroup, false);
                viewHolder = new EmptyViewHolder(view, mListener);
                break;

            case CONTENT_VIEW:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.places_item,
                        viewGroup, false);
                viewHolder = new ViewHolder(view, mListener);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) vh;
            final MyPlace place = mMyPlaces.get(position);
            holder.mTitle.setText(place.getTitle());
            holder.mAddress.setText(place.getAddress());
            holder.mLocation.setText(place.getLocationString());
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemDeleteClicked(place.getId());
                    removeItemAt(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return !mMyPlaces.isEmpty() ? mMyPlaces.size() : emptyStateCount();
    }

    private int emptyStateCount() {
        return 1;
    }

    private void removeItemAt(int position) {
        mMyPlaces.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMyPlaces.size());
    }
}