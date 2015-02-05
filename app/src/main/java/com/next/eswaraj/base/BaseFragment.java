package com.next.eswaraj.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.next.eswaraj.application.EswarajApplication;

public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EswarajApplication)getActivity().getApplication()).inject(this);
    }
}
