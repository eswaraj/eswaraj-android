package com.eswaraj.app.eswaraj.models;

import java.io.Serializable;


public class GlobalSearchResponseDto implements Serializable {

    private Long id;
    private String type;
    private String subType;
    private String name;
    private String image;
    private String partyName;
    private String cName;
    private PoliticalBodyAdminDto politicalBodyAdminDto;

    public PoliticalBodyAdminDto getPoliticalBodyAdminDto() {
        return politicalBodyAdminDto;
    }

    public void setPoliticalBodyAdminDto(PoliticalBodyAdminDto politicalBodyAdminDto) {
        this.politicalBodyAdminDto = politicalBodyAdminDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    @Override
    public String toString() {
        return "GlobalSearchResponseDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", partyName='" + partyName + '\'' +
                ", cName='" + cName + '\'' +
                ", politicalBodyAdminDto=" + politicalBodyAdminDto +
                '}';
    }
}
