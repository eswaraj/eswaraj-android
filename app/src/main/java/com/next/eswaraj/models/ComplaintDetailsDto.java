package com.next.eswaraj.models;


import com.eswaraj.web.dto.PhotoDto;

import java.util.ArrayList;
import java.util.List;

public class ComplaintDetailsDto extends ComplaintDto {

    List<PhotoDto> photos;

    public ComplaintDetailsDto() {
        photos = new ArrayList<PhotoDto>();
    }

    public List<PhotoDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoDto> photos) {
        this.photos = photos;
    }

}
