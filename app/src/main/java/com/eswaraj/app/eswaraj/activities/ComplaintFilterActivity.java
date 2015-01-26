package com.eswaraj.app.eswaraj.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.FilterListAdapter;
import com.eswaraj.app.eswaraj.base.BaseActivity;
import com.eswaraj.app.eswaraj.base.BaseFragmentActivity;
import com.eswaraj.app.eswaraj.models.ComplaintFilter;
import com.eswaraj.app.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.ArrayList;

import javax.inject.Inject;

public class ComplaintFilterActivity extends BaseFragmentActivity {

    @Inject
    GlobalSessionUtil globalSession;

    private GridView categoryList;
    private GridView statusList;
    private TextView none;

    private ArrayList<ComplaintFilter> categoryFilterItems = new ArrayList<ComplaintFilter>();
    private ArrayList<ComplaintFilter> statusFilterItems = new ArrayList<ComplaintFilter>();

    private FilterListAdapter categoryAdapter;
    private FilterListAdapter statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_filter);

        categoryList = (GridView) findViewById(R.id.cfCategoryList);
        statusList = (GridView) findViewById(R.id.cfStatusList);
        none = (TextView) findViewById(R.id.cfNone);

        ComplaintFilter filter = new ComplaintFilter();
        filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.STATUS);
        filter.setStatus("Pending");
        filter.setDisplayText("Open");
        statusFilterItems.add(filter);

        filter = new ComplaintFilter();
        filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.STATUS);
        filter.setStatus("Done");
        filter.setDisplayText("Closed");
        statusFilterItems.add(filter);

        for(CategoryWithChildCategoryDto categoryDto : globalSession.getCategoryDtoList()) {
            filter = new ComplaintFilter();
            filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.CATEGORY);
            filter.setCategoryId(categoryDto.getId());
            filter.setDisplayText(categoryDto.getName());
            statusFilterItems.add(filter);
        }

        categoryAdapter = new FilterListAdapter(this, android.R.layout.simple_list_item_1, categoryFilterItems);
        statusAdapter = new FilterListAdapter(this, android.R.layout.simple_list_item_1, statusFilterItems);

        categoryList.setAdapter(categoryAdapter);
        statusList.setAdapter(statusAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("FILTER", categoryAdapter.getItem(position));
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });

        statusList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("FILTER", statusAdapter.getItem(position));
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });

        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComplaintFilter complaintFilter = new ComplaintFilter();
                complaintFilter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.NONE);
                Intent data = new Intent();
                data.putExtra("FILTER", complaintFilter);
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });
    }

}
