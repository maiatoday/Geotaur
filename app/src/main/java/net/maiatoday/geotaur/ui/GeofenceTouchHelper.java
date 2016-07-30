package net.maiatoday.geotaur.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by maia on 2016/04/05.
 */
public class GeofenceTouchHelper extends ItemTouchHelper.SimpleCallback {
    private GeofenceListAdapter mAdapter;

    public GeofenceTouchHelper(GeofenceListAdapter adapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.remove(viewHolder.getAdapterPosition());
    }
}
