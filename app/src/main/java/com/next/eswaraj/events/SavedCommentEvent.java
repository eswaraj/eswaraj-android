package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.CommentDto;


public class SavedCommentEvent extends BaseEvent {

    private CommentDto commentDto;

    public CommentDto getCommentDto() {
        return commentDto;
    }

    public void setCommentDto(CommentDto commentDto) {
        this.commentDto = commentDto;
    }
}
