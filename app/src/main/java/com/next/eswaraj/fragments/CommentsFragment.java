package com.next.eswaraj.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.next.eswaraj.R;
import com.next.eswaraj.adapters.CommentListAdapter;
import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.events.GetCommentsEvent;
import com.next.eswaraj.events.SavedCommentEvent;
import com.next.eswaraj.helpers.GoogleAnalyticsTracker;
import com.next.eswaraj.middleware.MiddlewareServiceImpl;
import com.next.eswaraj.models.CommentDto;
import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.util.UserSessionUtil;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class CommentsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    UserSessionUtil userSession;
    @Inject
    GoogleAnalyticsTracker googleAnalyticsTracker;

    private ComplaintDto complaintDto;
    private List<CommentDto> commentDtoList;
    private CommentListAdapter commentListAdapter;
    private Boolean dataDownloadStarted = false;
    
    private ImageView cSend;
    private EditText cComment;
    private ListView cOldComments;
    private Button cShowMore;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
        if(getActivity() != null && !dataDownloadStarted) {
            middlewareService.loadComments(getActivity(), complaintDto, 0, 5);
            dataDownloadStarted = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);
        cSend = (ImageView) rootView.findViewById(R.id.cSend);
        cComment = (EditText) rootView.findViewById(R.id.cComment);
        cOldComments = (ListView) rootView.findViewById(R.id.cOldComments);
        cShowMore = (Button) rootView.findViewById(R.id.cShowMore);

        cShowMore.setVisibility(View.GONE);

        cOldComments.setDividerHeight(0);

        cSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "CommentsFragment: Send");
                if(!cComment.getText().toString().equals("")) {
                    middlewareService.postComment(userSession.getUser(), complaintDto, cComment.getText().toString());
                    cComment.setText("");
                }
            }
        });

        cShowMore.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAnalyticsTracker.trackUIEvent(GoogleAnalyticsTracker.UIAction.CLICK, "CommentsFragment: Show More");
                middlewareService.loadComments(getActivity(), complaintDto, commentDtoList.size(), 5);
            }
        });

        if(!dataDownloadStarted && complaintDto != null) {
            middlewareService.loadComments(getActivity(), complaintDto, 0, 5);
            dataDownloadStarted = true;
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(final GetCommentsEvent event) {
        if(event.getSuccess()) {
            if(commentListAdapter == null) {
                commentDtoList = event.getCommentDtos();
                commentListAdapter = new CommentListAdapter(getActivity(), R.layout.item_comment_list, commentDtoList);
                cOldComments.setAdapter(commentListAdapter);
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(CommentDto commentDto : event.getCommentDtos()) {
                            commentListAdapter.addComment(commentDto, false);
                        }
                        commentListAdapter.notifyDataSetChanged();
                    }
                });
            }
            if(commentListAdapter.getCount() >= 5) {
                cShowMore.setVisibility(View.VISIBLE);
            }
        }
        else {
            Toast.makeText(getActivity(), "Failed to fetch comments. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(final SavedCommentEvent event) {
        if(event.getSuccess()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commentListAdapter.addComment(event.getCommentDto(), true);
                    commentListAdapter.notifyDataSetChanged();
                }
            });
            if(commentListAdapter.getCount() >= 5) {
                cShowMore.setVisibility(View.VISIBLE);
            }
            Toast.makeText(getActivity(), "Comment Posted", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Failed to save comment. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
