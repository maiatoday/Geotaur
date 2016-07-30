package net.maiatoday.geotaur.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.databinding.GeofenceItemBinding;
import net.maiatoday.geotaur.location.SimpleGeofence;

import java.util.List;
import java.util.Locale;

/**
 * Adapter to provide Geofence data from a list for the Recycler View
 * Created by maia on 2016/04/05.
 */
public class GeofenceListAdapter extends
        RecyclerView.Adapter<DataBoundViewHolder>{

    private static final String TAG = "GeofenceListAdapter";

    private List<SimpleGeofence> geofences;
    private LayoutInflater layoutInflater;
    private OnGeofenceItemAction listener;
    public GeofenceListAdapter(List<SimpleGeofence> geofences, OnGeofenceItemAction listener) {
        this.geofences = geofences;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return geofences.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.geofence_item;
    }

    @Override
    public void onBindViewHolder(final DataBoundViewHolder viewHolder, final int itemType) {
        viewHolder.bindTo(geofences.get(viewHolder.getAdapterPosition()), listener);
    }

    @Override
    public DataBoundViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return GeofenceViewHolder.create(layoutInflater, viewGroup);
    }

    public void remove(int adapterPosition) {
        String id = geofences.get(adapterPosition).getId();
        listener.onItemRemoved(id);
        geofences.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public static class GeofenceViewHolder extends DataBoundViewHolder {
        static GeofenceViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            final GeofenceItemBinding binding =
                    DataBindingUtil.inflate(inflater, R.layout.geofence_item, parent, false);
            return new GeofenceViewHolder(binding);
        }

        GeofenceViewHolder(final GeofenceItemBinding binding) {
            super(binding);
        }
    }
}
