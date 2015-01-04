package com.eswaraj.app.eswaraj.events;


import com.eswaraj.app.eswaraj.models.ComplaintSavedResponseDto;

import java.io.File;

public class ComplaintSavedEvent {

    ComplaintSavedResponseDto complaintSavedResponseDto;
    File imageFile;

    public ComplaintSavedResponseDto getComplaintSavedResponseDto() {
        return complaintSavedResponseDto;
    }

    public void setComplaintSavedResponseDto(ComplaintSavedResponseDto complaintSavedResponseDto) {
        this.complaintSavedResponseDto = complaintSavedResponseDto;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
