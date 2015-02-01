package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.ShowProfileEvent;
import com.eswaraj.app.eswaraj.events.ShowSelectAmenityEvent;
import com.eswaraj.app.eswaraj.events.ShowUserComplaintsEvent;
import com.eswaraj.app.eswaraj.helpers.ComplaintFilterHelper;
import com.eswaraj.app.eswaraj.helpers.GoogleAnalyticsTracker;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.app.eswaraj.models.ComplaintFilter;
import com.eswaraj.app.eswaraj.util.GenericUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class UserSnapshotFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private ComplaintListFragment complaintListFragment;
    private CustomProgressDialog pDialog;
    private List<ComplaintDto> complaintDtoList;
    private List<ComplaintDto> currentComplaintDtoList;
    private ComplaintFilter complaintFilter;
    private View headerView;
    private View footerView;
    private ImageView photo;
    private TextView name;
    private TextView details;
    private Button total;
    private Button open;
    private Button close;
    private Button profile;
    private Button smart;
    private Button showAll;
    private Button create;

    private View.OnClickListener showAllListener;
    private Long openCount;
    private Long closeCount;
    private Long totalCount;

    public UserSnapshotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        complaintListFragment = new ComplaintListFragment();
        complaintListFragment.showLimited(5);
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.usListFragment, complaintListFragment).commit();
        }
        middlewareService.loadUserComplaints(getActivity(), userSession.getUser(), true);
        pDialog = new CustomProgressDialog(getActivity(), false, true, "Fetching your complaints ...");
        pDialog.show();
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_snapshot, container, false);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.header_user_snapshot, null);
        footerView = getActivity().getLayoutInflater().inflate(R.layout.footer_user_snapshot, null);
        complaintListFragment.setHeader(headerView);
        complaintListFragment.setFooter(footerView);

        photo = (ImageView) headerView.findViewById(R.id.usPhoto);
        name = (TextView) headerView.findViewById(R.id.usName);
        details = (TextView) headerView.findViewById(R.id.usDetails);
        total = (Button) headerView.findViewById(R.id.usTotal);
        open = (Button) headerView.findViewById(R.id.usOpen);
        close = (Button) headerView.findViewById(R.id.usClose);
        profile = (Button) headerView.findViewById(R.id.usProfile);
        smart = (Button) headerView.findViewById(R.id.usSmart);
        showAll = (Button) footerView.findViewById(R.id.usShowAll);
        create = (Button) footerView.findViewById(R.id.usCreate);

        if(userSession.getProfilePhoto() != null && !userSession.getProfilePhoto().equals("")) {
            Picasso.with(getActivity()).load(userSession.getProfilePhoto().replace("http", "https")).error(R.drawable.anon).placeholder(R.drawable.anon).into(photo);
        }
        else {
            photo.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.anon));
        }
        name.setText(userSession.getUser().getPerson().getName());
        if(userSession.getUser().getPerson().getDob() != null) {
            details.setText(GenericUtil.getAge(userSession.getUser().getPerson().getDob()) + " Years, " + userSession.getUser().getPerson().getGender());
        }

        if(totalCount != null) {
            total.setText("Total\n" + totalCount);
            open.setText("Open\n" + openCount);
            close.setText("Closed\n" + closeCount);
        }

        showAllListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowUserComplaintsEvent event = new ShowUserComplaintsEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        };
        total.setOnClickListener(showAllListener);
        open.setOnClickListener(showAllListener);
        close.setOnClickListener(showAllListener);
        smart.setOnClickListener(showAllListener);
        showAll.setOnClickListener(showAllListener);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowProfileEvent event = new ShowProfileEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSelectAmenityEvent event = new ShowSelectAmenityEvent();
                event.setSuccess(true);
                eventBus.post(event);
            }
        });
        return rootView;
    }

    private void setComplaintData(List<ComplaintDto> complaintDtos) {
        currentComplaintDtoList = complaintDtos;
        complaintListFragment.setData(currentComplaintDtoList);
    }

    public void markComplaintClosed(Long id) {
        complaintListFragment.markComplaintClosed(id);
    }

    public void setFilter(ComplaintFilter filter) {
        complaintFilter = filter;
        setComplaintData(ComplaintFilterHelper.filter(complaintDtoList, filter));
    }

    public void onEventMainThread(GetUserComplaintsEvent event) {
        if(event.getSuccess()) {
            complaintDtoList = event.getComplaintDtoList();
            setComplaintData(ComplaintFilterHelper.filter(complaintDtoList, complaintFilter));
            openCount = new Long(0);
            closeCount = new Long(0);
            for(ComplaintDto complaintDto : complaintDtoList) {
                if(complaintDto.getStatus().equals("Done")) {
                    closeCount++;
                }
                else {
                    openCount++;
                }
            }
            totalCount = Long.valueOf(complaintDtoList.size());
            if(total != null) {
                total.setText("Total\n" + totalCount);
                open.setText("Open\n" + openCount);
                close.setText("Closed\n" + closeCount);
            }
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
        pDialog.dismiss();
    }

}
