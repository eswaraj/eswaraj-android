package com.next.eswaraj.events;

import com.next.eswaraj.base.BaseEvent;
import com.next.eswaraj.models.ComplaintPostResponseDto;

import java.io.File;


public class ComplaintPostedEvent extends BaseEvent {

    private ComplaintPostResponseDto complaintPostResponseDto;
    private File imageFile;

    public ComplaintPostResponseDto getComplaintPostResponseDto() {
        return complaintPostResponseDto;
    }

    public void setComplaintPostResponseDto(ComplaintPostResponseDto complaintPostResponseDto) {
        this.complaintPostResponseDto = complaintPostResponseDto;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
