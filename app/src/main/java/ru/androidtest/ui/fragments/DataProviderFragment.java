package ru.androidtest.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.androidtest.dataProviders.AbstractDataProvider;
import ru.androidtest.dataProviders.DataProvider;
import ru.androidtest.sfClasses.SFBaseActivity;

public class DataProviderFragment extends Fragment {
    private AbstractDataProvider mDataProvider;

    public DataProviderFragment() {
        // Required empty public constructor
    }

    public static DataProviderFragment newInstance() {
        return new DataProviderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new DataProvider(((SFBaseActivity) getActivity()).getServiceHelper());
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataProvider = null;
    }
}
