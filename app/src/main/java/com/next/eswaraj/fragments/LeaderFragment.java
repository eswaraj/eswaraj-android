package com.next.eswaraj.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.config.TimelineType;
import com.next.eswaraj.events.CallPhoneEvent;
import com.next.eswaraj.events.GetLeaderEvent;
import com.next.eswaraj.events.SendEmailEvent;
import com.next.eswaraj.events.ShowPromisesEvent;
import com.next.eswaraj.events.StartAnotherActivityEvent;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.PoliticalBodyAdminDto;
import com.next.eswaraj.util.UserSessionUtil;
import com.next.eswaraj.widgets.CustomNetworkImageView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class LeaderFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;

    private CustomNetworkImageView lPhoto;
    private TextView lName;
    private TextView lPost;
    private TextView lAge;
    private TextView lAddress;
    private TextView lParty;
    private TextView lEducation;
    private WebView lDetails;
    private TextView lConstituency;
    private Button lPromise;
    private Button lEmail;
    private Button lPhone;
    private View headerView;
    private TimelineFragment timelineFragment;

    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public LeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);

        if(savedInstanceState == null) {
            timelineFragment = new TimelineFragment();
            getChildFragmentManager().beginTransaction().add(R.id.lTimeline, timelineFragment).commit();
        }
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.header_leader, null);
        timelineFragment.setHeader(headerView);

        lPhoto = (CustomNetworkImageView) headerView.findViewById(R.id.lPhoto);
        lName = (TextView) headerView.findViewById(R.id.lName);
        lPost = (TextView) headerView.findViewById(R.id.lPost);
        lDetails = (WebView) headerView.findViewById(R.id.lDetails);
        lAge = (TextView) headerView.findViewById(R.id.lAge);
        lParty = (TextView) headerView.findViewById(R.id.lParty);
        lAddress = (TextView) headerView.findViewById(R.id.lAddress);
        lEducation = (TextView) headerView.findViewById(R.id.lEducation);
        lConstituency = (TextView) headerView.findViewById(R.id.lConstituency);
        lPromise = (Button) headerView.findViewById(R.id.lPromise);
        lEmail = (Button) headerView.findViewById(R.id.lEmail);
        lPhone = (Button) headerView.findViewById(R.id.lPhone);


        politicalBodyAdminDto = (PoliticalBodyAdminDto) getActivity().getIntent().getSerializableExtra("LEADER");

        if(politicalBodyAdminDto == null) {
            middlewareService.loadLeaderById(getActivity(), getActivity().getIntent().getLongExtra("ID", 0));
        }
        else {
            getActivity().setTitle(politicalBodyAdminDto.getName());
            setFields();
        }
        lConstituency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAnotherActivityEvent event = new StartAnotherActivityEvent();
                event.setId(politicalBodyAdminDto.getLocation().getId());
                eventBus.post(event);
            }
        });
        lPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPromisesEvent event = new ShowPromisesEvent();
                event.setSuccess(true);
                event.setPoliticalBodyAdminDto(politicalBodyAdminDto);
                eventBus.post(event);
            }
        });
        lEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(politicalBodyAdminDto.getOfficeEmail() != null && !politicalBodyAdminDto.getOfficeEmail().equals("")) {
                    SendEmailEvent event = new SendEmailEvent();
                    event.setEmail(politicalBodyAdminDto.getOfficeEmail());
                    event.setSubject("[eSwaraj] Message from " + userSession.getUser().getPerson().getName());
                    event.setMessage("Sent from eSwaraj");
                    eventBus.post(event);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("We dont have the official email ID of " + politicalBodyAdminDto.getName() + " yet. We will try to get it soon.")
                            .setCancelable(false)
                            .setTitle("Email ID not known")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        lPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(politicalBodyAdminDto.getMobileNumber1() != null && !politicalBodyAdminDto.getMobileNumber1().equals("")) {
                    CallPhoneEvent event = new CallPhoneEvent();
                    event.setNumber(politicalBodyAdminDto.getMobileNumber1());
                    eventBus.post(event);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("We dont have the phone number of " + politicalBodyAdminDto.getName() + " yet. We will try to get it soon.")
                            .setCancelable(false)
                            .setTitle("Phone number not known")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        return rootView;
    }

    private void setFields() {
        timelineFragment.setTypeAndId(TimelineType.LEADER, politicalBodyAdminDto.getId());
        if(politicalBodyAdminDto.getProfilePhoto() != null && !politicalBodyAdminDto.getProfilePhoto().equals("")) {
            Picasso.with(getActivity()).load(politicalBodyAdminDto.getProfilePhoto().replace("http:", "https:")).error(R.drawable.anon).placeholder(R.drawable.anon).into(lPhoto);
        }
        else {
            lPhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.anon));
        }
        lName.setText(politicalBodyAdminDto.getName());
        lPost.setText(politicalBodyAdminDto.getPoliticalAdminTypeDto().getShortName() + ", " + politicalBodyAdminDto.getLocation().getName());
        lParty.setText(WordUtils.capitalizeFully(politicalBodyAdminDto.getParty().getName()));
        lAddress.setText("");
        lAge.setText("");
        lEducation.setText("");
        lConstituency.setText(politicalBodyAdminDto.getLocation().getName());
        if (politicalBodyAdminDto.getBiodata() != null && !politicalBodyAdminDto.getBiodata().equals("")) {
            lDetails.loadData(politicalBodyAdminDto.getBiodata(), "text/html", null);
        }
        else {
            lDetails.setVisibility(View.GONE);
        }
    }

    public void onEventMainThread(GetLeaderEvent event) {
        if(event.getSuccess()) {
            politicalBodyAdminDto = event.getPoliticalBodyAdminDto();
            getActivity().setTitle(politicalBodyAdminDto.getName());
            setFields();
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch leader details. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
