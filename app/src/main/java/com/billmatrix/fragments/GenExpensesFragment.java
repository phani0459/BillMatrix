package com.billmatrix.fragments;


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
public class GenExpensesFragment extends Fragment {


    public GenExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gen_expns, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

}
