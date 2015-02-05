package com.next.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;


public class GcmMessageDto extends BaseDto {

    private String message;
    private Long complaintId;
    private ViewerDto viewedBy;

    public GcmMessageDto() {
        viewedBy = new ViewerDto();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public ViewerDto getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(ViewerDto viewedBy) {
        this.viewedBy = viewedBy;
    }

    @Override
    public String toString() {
        return "GcmMessageDto{" +
                "message='" + message + '\'' +
                ", complaintId=" + complaintId +
                ", viewedBy=" + viewedBy +
                '}';
    }

    public class ViewerDto extends BaseDto {
        private String name;
        private String profilePhoto;
        private String position;
        private Boolean self;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public Boolean getSelf() {
            return self;
        }

        public void setSelf(Boolean self) {
            this.self = self;
        }

        @Override
        public String toString() {
            return "ViewerDto{" +
                    "name='" + name + '\'' +
                    ", profilePhoto='" + profilePhoto + '\'' +
                    ", position='" + position + '\'' +
                    ", self=" + self +
                    '}';
        }
    }
}
