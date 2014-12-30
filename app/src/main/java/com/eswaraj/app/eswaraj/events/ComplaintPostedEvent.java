package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.app.eswaraj.models.ComplaintPostResponseDto;
import com.eswaraj.web.dto.ComplaintDto;

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
