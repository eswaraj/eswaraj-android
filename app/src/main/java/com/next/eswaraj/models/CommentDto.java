package com.next.eswaraj.models;


public class CommentDto {

    private String text;
    private Long creationTime;
    private Long id;
    private Boolean adminComment;
    private Commenter postedBy;
    private Long commentedById;


    public Long getCommentedById() {
        return commentedById;
    }

    public void setCommentedById(Long commentedById) {
        this.commentedById = commentedById;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(Boolean adminComment) {
        this.adminComment = adminComment;
    }

    public Commenter getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Commenter postedBy) {
        this.postedBy = postedBy;
    }

    public void createNewCommenter() {
        postedBy = new Commenter();
    }



    public class Commenter {

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
    }
}
