/*
 * MIT License
 *
 * Copyright (c) [2016] [Maia Grotepass]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.maiatoday.geotaur.ui;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.databinding.DialogAddGeoBinding;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddGeoDialogFragment.OnAddGeofenceListener} interface
 * to handle interaction events.
 * Use the {@link AddGeoDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGeoDialogFragment extends DialogFragment {

    private static final String ARG_RADIUS = "radius";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_ID = "id";
    private OnAddGeofenceListener mListener;
    private TextInputEditText mRadius;
    private TextInputEditText mLatitude;
    private TextInputEditText mLongitude;
    private float mRadiusValue;
    private TextInputEditText mTitle;
    private double mLongitudeValue;
    private double mLatitudeValue;
    private String mId;

    public AddGeoDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddGeoDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGeoDialogFragment newInstance(float radius, double latitude, double longitude, String id) {
        AddGeoDialogFragment fragment = new AddGeoDialogFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_RADIUS, radius);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRadiusValue = getArguments().getFloat(ARG_RADIUS);
            mLatitudeValue = getArguments().getDouble(ARG_LATITUDE);
            mLongitudeValue = getArguments().getDouble(ARG_LONGITUDE);
            mId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DialogAddGeoBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_geo, container, false);
        View view = binding.getRoot();
        mTitle = binding.title;
        mTitle.setText(mId);
        mRadius = binding.radius;
        mRadius.setText(Float.toString(mRadiusValue));
        mLatitude = binding.latitude;
        mLongitude = binding.longtitude;
        if (!TextUtils.isEmpty(mId)) {
            mLatitude.setText(Double.toString(mLatitudeValue));
            mLongitude.setText(Double.toString(mLongitudeValue));
        }
        Button okButton = binding.addGeoButton;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validEntries()) {
                    if (mListener != null) {
                        mListener.onAddGeofence(mTitle.getText().toString(),
                                mRadius.getText().toString(),
                                mLatitude.getText().toString(),
                                mLongitude.getText().toString());
                    }
                    AddGeoDialogFragment.this.dismiss();
                }
            }
        });
        Button useCurrent = binding.currentLocationButton;
        useCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLatitude.setText(Double.toString(mLatitudeValue));
                mLongitude.setText(Double.toString(mLongitudeValue));
            }
        });
        return view;
    }

    private boolean validEntries() {
        boolean dataOk = true;
        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError("Set a title for the point");
            dataOk = false;
        } else {
            mTitle.setError(null);
        }
        if (!floatOk(mLatitude.getText().toString())) {
            mLatitude.setError("Latitude must be decimal");
            dataOk = false;
        } else {
            mLatitude.setError(null);
        }
        if (!floatOk(mLongitude.getText().toString())) {
            mLongitude.setError("Longitude must be decimal");
            dataOk = false;
        } else {
            mLongitude.setError(null);
        }
        return dataOk;
    }

    private boolean floatOk(String floatString) {
        try {
            Float.parseFloat(floatString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddGeofenceListener) {
            mListener = (OnAddGeofenceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static String getFragmentTag() {
        return AddGeoDialogFragment.class.getSimpleName();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddGeofenceListener {
        void onAddGeofence(String title, String radius, String latitude, String longtitude);
    }
}
