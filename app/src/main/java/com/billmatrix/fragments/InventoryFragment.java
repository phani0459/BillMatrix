package com.billmatrix.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.billmatrix.R;
import com.billmatrix.adapters.InventoryAdapter;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.interfaces.OnItemClickListener;
import com.billmatrix.models.Inventory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment implements OnItemClickListener {

    private Context mContext;
    private BillMatrixDaoImpl billMatrixDaoImpl;
    @BindView(R.id.inventoryList)
    public RecyclerView inventoryRecyclerView;
    public InventoryAdapter inventoryAdapter;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();

        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Inventory.InventoryData> inventoryDatas = new ArrayList<>();

        inventoryAdapter = new InventoryAdapter(inventoryDatas, this);
        inventoryRecyclerView.setAdapter(inventoryAdapter);


        return v;
    }

    @Override
    public void onItemClick(int caseInt, int position) {

    }
}
