package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.billmatrix.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendorsFragment extends Fragment {


    private static final String TAG = VendorsFragment.class.getSimpleName();
    private Context mContext;

    public VendorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vendors, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        return v;
    }

    public void searchClicked(String query) {
        Log.e(TAG, "searchClicked: " + query );
    }

}
