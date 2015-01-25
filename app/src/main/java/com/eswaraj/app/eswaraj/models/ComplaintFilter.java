package com.eswaraj.app.eswaraj.models;


import java.io.Serializable;

public class ComplaintFilter implements Serializable {

    private ComplaintFilterType complaintFilterType;
    private Long categoryId;
    private String status;

    private String displayText;

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public ComplaintFilterType getComplaintFilterType() {
        return complaintFilterType;
    }

    public void setComplaintFilterType(ComplaintFilterType complaintFilterType) {
        this.complaintFilterType = complaintFilterType;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ComplaintFilter{" +
                "complaintFilterType=" + complaintFilterType +
                ", categoryId=" + categoryId +
                ", status='" + status + '\'' +
                ", displayText='" + displayText + '\'' +
                '}';
    }

    public enum ComplaintFilterType {
        CATEGORY,
        STATUS,
        NONE
    }
}
