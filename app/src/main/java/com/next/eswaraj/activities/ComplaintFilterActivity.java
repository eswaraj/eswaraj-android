package com.next.eswaraj.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.FilterListAdapter;
import com.next.eswaraj.base.BaseFragmentActivity;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.util.GlobalSessionUtil;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

import java.util.ArrayList;

import javax.inject.Inject;

public class ComplaintFilterActivity extends BaseFragmentActivity {

    @Inject
    GlobalSessionUtil globalSession;

    private GridView categoryList;
    private GridView statusList;
    private TextView none;
    private Button apply;

    private ArrayList<ComplaintFilter> categoryFilterItems = new ArrayList<ComplaintFilter>();
    private ArrayList<ComplaintFilter> statusFilterItems = new ArrayList<ComplaintFilter>();

    private FilterListAdapter categoryAdapter;
    private FilterListAdapter statusAdapter;

    private ComplaintFilter selected;
    private ComplaintFilter currentSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_filter);
        //setTitle("Select filter");

        //selected = (ComplaintFilter) getIntent().getSerializableExtra("FILTER");
        //currentSelection = selected;

        categoryList = (GridView) findViewById(R.id.cfCategoryList);
        statusList = (GridView) findViewById(R.id.cfStatusList);
        none = (TextView) findViewById(R.id.cfNone);
        apply = (Button) findViewById(R.id.cfApply);

        ComplaintFilter filter = new ComplaintFilter();
        filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.STATUS);
        filter.setStatus(getResources().getString(R.string.filterStatusPending));
        filter.setDisplayText(getResources().getString(R.string.filterDisplayTextOpen));
        /*
        if(selected != null && selected.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.STATUS && selected.getStatus().equals("Pending")) {
            filter.setHighlight(true);
        }
        else {
            filter.setHighlight(false);
        }
        */
        statusFilterItems.add(filter);

        filter = new ComplaintFilter();
        filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.STATUS);
        filter.setStatus(getResources().getString(R.string.filterStatusDone));
        filter.setDisplayText(getResources().getString(R.string.filterDisplayTextClosed));
        /*
        if(selected != null && selected.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.STATUS && selected.getStatus().equals("Done")) {
            filter.setHighlight(true);
        }
        else {
            filter.setHighlight(false);
        }
        */
        statusFilterItems.add(filter);

        for(CategoryWithChildCategoryDto categoryDto : globalSession.getCategoryDtoList(this)) {
            filter = new ComplaintFilter();
            filter.setComplaintFilterType(ComplaintFilter.ComplaintFilterType.CATEGORY);
            filter.setCategoryId(categoryDto.getId());
            filter.setDisplayText(categoryDto.getName());
            /*
            if(selected != null && selected.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.CATEGORY && selected.getCategoryId().equals(categoryDto.getId())) {
                filter.setHighlight(true);
            }
            else {
                filter.setHighlight(false);
            }
            */
            categoryFilterItems.add(filter);
        }

        categoryAdapter = new FilterListAdapter(this, R.layout.item_filter_citizenservices_list, categoryFilterItems);
        statusAdapter = new FilterListAdapter(this, R.layout.item_filter_complaintstatus_list, statusFilterItems);

        categoryList.setAdapter(categoryAdapter);
        statusList.setAdapter(statusAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelection = (ComplaintFilter) categoryAdapter.getItem(position);
                categoryAdapter.setSelection(position);
                categoryAdapter.notifyDataSetChanged();
                statusAdapter.setSelection(-1);
                statusAdapter.notifyDataSetChanged();
            }
        });

        statusList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelection = (ComplaintFilter) statusAdapter.getItem(position);
                statusAdapter.setSelection(position);
                statusAdapter.notifyDataSetChanged();
                categoryAdapter.setSelection(-1);
                categoryAdapter.notifyDataSetChanged();
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

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("FILTER", currentSelection);
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
