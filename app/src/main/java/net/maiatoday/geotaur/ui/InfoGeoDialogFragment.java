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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.databinding.DialogAddGeoBinding;
import net.maiatoday.geotaur.databinding.DialogInfoGeoBinding;
import net.maiatoday.geotaur.location.FenceAccess;
import net.maiatoday.geotaur.location.LocationConstants;

import javax.inject.Inject;


/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link InfoGeoDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoGeoDialogFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private String mId;
    @Inject
    FenceAccess fenceAccess;
    private FenceInfoReceiver fenceInfoReceiver;
    private DialogInfoGeoBinding binding;

    public InfoGeoDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddGeoDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoGeoDialogFragment newInstance(String id) {
        InfoGeoDialogFragment fragment = new InfoGeoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TaurApplication) getActivity().getApplication()).getComponent().inject(this);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
            fenceAccess.queryGeofence(getActivity(), mId);
        }
        fenceInfoReceiver = new FenceInfoReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fenceInfoReceiver,
                new IntentFilter(LocationConstants.BROADCAST_FENCE_INFO));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fenceInfoReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_info_geo, container, false);
        View view = binding.getRoot();
        Button okButton = binding.infoGeoOk;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    InfoGeoDialogFragment.this.dismiss();
            }
        });
        return view;
    }
    public class FenceInfoReceiver extends BroadcastReceiver {
        private static final String TAG = "FenceInfoReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(LocationConstants.INFO_MESSAGE)) {
                String message = intent.getExtras().getString(LocationConstants.INFO_MESSAGE);
                binding.infoGeoText.setText(message);
            }
        }
    }

}
