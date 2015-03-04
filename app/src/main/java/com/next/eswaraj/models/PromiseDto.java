package com.next.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;


public class PromiseDto extends BaseDto {

    private String title;
    private String description;
    private String status;
    private String deliveryTime;

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

    @Override
    public String toString() {
        return "PromiseDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                '}';
    }
}
