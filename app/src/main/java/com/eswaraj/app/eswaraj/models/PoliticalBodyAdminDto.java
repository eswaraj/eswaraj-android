package com.eswaraj.app.eswaraj.models;


import com.eswaraj.web.dto.BaseDto;
import com.eswaraj.web.dto.PartyDto;

public class PoliticalBodyAdminDto extends BaseDto {

    private PartyDto party;
    private PoliticalAdminTypeDto politicalAdminType;
    private PoliticalAdminLocationDto location;
    private Long startDate;
    private String urlIdentifier;
    private String personExternalId;
    private String name;
    private String gender;
    private String profilePhoto;

    public PoliticalBodyAdminDto() {
        party = new PartyDto();
        politicalAdminType = new PoliticalAdminTypeDto();
        location = new PoliticalAdminLocationDto();
    }

    public PoliticalAdminLocationDto getLocation() {
        return location;
    }

    public void setLocation(PoliticalAdminLocationDto location) {
        this.location = location;
    }

    public PartyDto getPartyDto() {
        return party;
    }

    public void setPartyDto(PartyDto partyDto) {
        this.party = partyDto;
    }

    public PoliticalAdminTypeDto getPoliticalAdminTypeDto() {
        return politicalAdminType;
    }

    public void setPoliticalAdminTypeDto(PoliticalAdminTypeDto politicalAdminTypeDto) {
        this.politicalAdminType = politicalAdminTypeDto;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public String getUrlIdentifier() {
        return urlIdentifier;
    }

    public void setUrlIdentifier(String urlIdentifier) {
        this.urlIdentifier = urlIdentifier;
    }

    public String getPersonExternalId() {
        return personExternalId;
    }

    public void setPersonExternalId(String personExternalId) {
        this.personExternalId = personExternalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "PoliticalBodyAdminDto{" +
                "partyDto=" + party +
                ", politicalAdminTypeDto=" + politicalAdminType +
                ", startDate=" + startDate +
                ", urlIdentifier='" + urlIdentifier + '\'' +
                ", personExternalId='" + personExternalId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }

    public class PoliticalAdminTypeDto extends BaseDto {
        private String name;
        private String shortName;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "PoliticalAdminTypeDto{" +
                    "name='" + name + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public class PoliticalAdminLocationDto extends BaseDto {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "PoliticalAdminLocationDto{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
