package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.billmatrix.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerTransFragment extends Fragment {

    private Context mContext;

    public CustomerTransFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cust_trans, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        return v;
    }

}
