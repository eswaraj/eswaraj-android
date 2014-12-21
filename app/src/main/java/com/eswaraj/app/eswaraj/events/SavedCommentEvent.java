package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.CommentDto;


public class SavedCommentEvent extends BaseEvent {

    private CommentDto commentDto;

    public CommentDto getCommentDto() {
        return commentDto;
    }

    public void setCommentDto(CommentDto commentDto) {
        this.commentDto = commentDto;
    }
}
