package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.adapters.CommentListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetCommentsEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.CommentDto;
import com.eswaraj.web.dto.ComplaintDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class CommentsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ComplaintDto complaintDto;
    private List<CommentDto> commentDtoList;
    private CommentListAdapter commentListAdapter;
    
    private Button cSend;
    private EditText cComment;
    private ListView cOldComments;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);
        cSend = (Button) rootView.findViewById(R.id.cSend);
        cComment = (EditText) rootView.findViewById(R.id.cComment);
        cOldComments = (ListView) rootView.findViewById(R.id.cOldComments);

        cSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Post comment request launch here
                //On completion update the adapter to add the new comment
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
        middlewareService.loadComments(getActivity(), complaintDto);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(GetCommentsEvent event) {
        if(event.getSuccess()) {
            commentDtoList = event.getCommentDtos();
            commentListAdapter = new CommentListAdapter(getActivity(), R.layout.item_comment_list, commentDtoList);
            cOldComments.setAdapter(commentListAdapter);
        }
        else {
            Toast.makeText(getActivity(), "Failed to fetch comments. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
