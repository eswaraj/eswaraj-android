package com.next.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TimelineDto extends BaseDto implements Serializable {

    private String title;
    private String description;
    private Long updateTime;
    private Long creationTime;
    private Poster createdBy;
    private String type;
    private String status;
    private String deliveryTime;
    private List<String> youtubeUrl;
    private List<String> images;
    private List<String> documents;

    public TimelineDto() {
        createdBy = new Poster();
        youtubeUrl = new ArrayList<>();
        images = new ArrayList<>();
        documents = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Poster getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Poster createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(List<String> youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "TimelineDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", creationTime=" + creationTime +
                ", createdBy=" + createdBy +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                ", youtubeUrl=" + youtubeUrl +
                ", images=" + images +
                ", documents=" + documents +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public class Poster {

        private String name;
        private String externalId;
        private String gender;
        private String profilePhoto;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        @Override
        public String toString() {
            return "Poster{" +
                    "name='" + name + '\'' +
                    ", externalId='" + externalId + '\'' +
                    ", gender='" + gender + '\'' +
                    ", profilePhoto='" + profilePhoto + '\'' +
                    '}';
        }
    }
}
