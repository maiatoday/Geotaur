package net.maiatoday.geotaur.helpers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import net.maiatoday.quip.Quip;

import java.util.List;

/**
 * Helper class to keep all Firebase info together
 * Created by maia on 2016/08/03.
 */

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    public static final String QUIP_ENTER_KEY = "quip_enter";
    public static final String QUIP_EXIT_KEY = "quip_exit";
    public static final String QUIP_WALK_KEY = "quip_walk";
    public static final String GEO_LURE_KEY = "geo_lure";


    public static void getQuipsFromFirebase(final Quip result, String key) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference childref = database.child(key);
        childref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get values
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> values = dataSnapshot.getValue(t);
                        if (values != null) {
                            String[] newQuips = values.toArray(new String[values.size()]);
                            result.setQuips(newQuips);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "quips walk:onCancelled", databaseError.toException());
                    }
                });
    }
}
