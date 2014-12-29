package com.eswaraj.app.eswaraj.events;

import com.eswaraj.app.eswaraj.base.BaseEvent;
import com.eswaraj.web.dto.ComplaintDto;

import java.io.File;


public class ComplaintPostedEvent extends BaseEvent {

    private ComplaintDto complaintDto;
    private File imageFile;

    public ComplaintDto getComplaintDto() {
        return complaintDto;
    }

    public void setComplaintDto(ComplaintDto complaintDto) {
        this.complaintDto = complaintDto;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
