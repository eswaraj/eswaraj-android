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
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.SavedCommentEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.models.CommentDto;
import com.eswaraj.app.eswaraj.models.ComplaintDto;
import com.eswaraj.web.dto.UserDto;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class CommentsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private UserDto userDto;
    private ComplaintDto complaintDto;
    private List<CommentDto> commentDtoList;
    private CommentListAdapter commentListAdapter;
    
    private Button cSend;
    private EditText cComment;
    private ListView cOldComments;
    private Button cShowMore;

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
        cShowMore = (Button) rootView.findViewById(R.id.cShowMore);


        cSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.postComment(userDto, complaintDto, cComment.getText().toString());
                cComment.setText("");
            }
        });

        cShowMore.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                middlewareService.loadComments(getActivity(), complaintDto, commentDtoList.size() + 50);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
        middlewareService.loadComments(getActivity(), complaintDto, 50);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
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
                        commentDtoList = event.getCommentDtos();
                        commentListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
        else {
            Toast.makeText(getActivity(), "Failed to fetch comments. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            userDto = event.getUserDto();
        }
        else {
            //This will never happen
        }
    }

    public void onEventMainThread(final SavedCommentEvent event) {
        if(event.getSuccess()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commentListAdapter.addComment(event.getCommentDto());
                    commentListAdapter.notifyDataSetChanged();
                }
            });
            Toast.makeText(getActivity(), "Comment Posted", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Failed to save comment. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
