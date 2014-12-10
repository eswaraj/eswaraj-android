package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.TemplateListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.List;

public class TemplatesFragment extends BaseFragment {

    private List<CategoryWithChildCategoryDto> templates;
    private ListView listView;

    public TemplatesFragment() {

    }

    public void setTemplate(List<CategoryWithChildCategoryDto> templates) {
        this.templates = templates;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_templates, container, false);
        listView = (ListView) rootView.findViewById(R.id.tList);

        TemplateListAdapter templateListAdapter = new TemplateListAdapter(getActivity(), android.R.layout.simple_list_item_1, templates);
        listView.setAdapter(templateListAdapter);
        return rootView;
    }


}
