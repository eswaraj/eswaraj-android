package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.AddDetailsActivity;
import com.eswaraj.app.eswaraj.activities.SelectTemplateActivity;
import com.eswaraj.app.eswaraj.adapters.TemplateListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.io.Serializable;
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

        TemplateListAdapter templateListAdapter = new TemplateListAdapter(getActivity(), R.layout.item_subcategory_list, templates);
        listView.setAdapter(templateListAdapter);
        listView.setDividerHeight(0);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent i = new Intent(getActivity(), AddDetailsActivity.class);
                i.putExtra("TEMPLATE", (Serializable) listView.getAdapter().getItem(pos));
                startActivity(i);
            }
        });
        return rootView;
    }


}
