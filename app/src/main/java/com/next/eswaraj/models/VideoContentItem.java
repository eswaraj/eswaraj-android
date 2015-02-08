package com.next.eswaraj.models;


public class VideoContentItem {

    private String name;
    private String link;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "VideoContentItem{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
