package com.next.eswaraj.events;


import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.CommentDto;

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
