package com.eswaraj.app.eswaraj.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.eswaraj.app.eswaraj.application.EswarajApplication;

public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getActivity().getApplication()).inject(this);
    }
}
