package com.next.eswaraj.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.GlobalSearchAdapter;
import com.next.eswaraj.base.BaseActivity;
import com.next.eswaraj.events.GlobalSearchResultEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.GlobalSearchResponseDto;
import com.next.eswaraj.widgets.CustomProgressDialog;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SearchableActivity extends BaseActivity {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ListView resultList;
    private GlobalSearchAdapter globalSearchAdapter;
    private CustomProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        setTitle("Search results");
        eventBus.register(this);
        resultList = (ListView) findViewById(R.id.sResultList);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            middlewareService.globalSearch(this, query);
        }

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalSearchResponseDto globalSearchResponseDto = (GlobalSearchResponseDto) resultList.getAdapter().getItem(position);
                if(globalSearchResponseDto.getType().equals("Location")) {
                    Intent i = new Intent(getBaseContext(), ConstituencySnapshotActivity.class);
                    i.putExtra("ID", globalSearchResponseDto.getId());
                    startActivity(i);
                }
                else if(globalSearchResponseDto.getType().equals("Leader")) {
                    Intent i = new Intent(getBaseContext(), LeaderActivity.class);
                    i.putExtra("ID", globalSearchResponseDto.getId());
                    startActivity(i);
                }
            }
        });

        pDialog = new CustomProgressDialog(this, false, true, "Loading search results...");
        pDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(GlobalSearchResultEvent event) {
        if(event.getSuccess()) {
            globalSearchAdapter = new GlobalSearchAdapter(this, R.layout.item_global_search_result, event.getGlobalSearchResponseDtoList());
            resultList.setAdapter(globalSearchAdapter);
        }
        else {
            Toast.makeText(this, "Could not get search results. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
        eventBus.unregister(this);
    }
}
