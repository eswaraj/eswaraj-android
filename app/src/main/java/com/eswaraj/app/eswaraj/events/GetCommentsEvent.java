package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.CommentDto;

import java.util.List;

public class GetCommentsEvent extends BaseEvent {
    private List<CommentDto> commentDtos;

    public List<CommentDto> getCommentDtos() {
        return commentDtos;
    }

    public void setCommentDtos(List<CommentDto> commentDtos) {
        this.commentDtos = commentDtos;
    }
}
