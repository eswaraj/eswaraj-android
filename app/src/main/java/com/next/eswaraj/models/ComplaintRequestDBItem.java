package com.next.eswaraj.models;


public class ComplaintRequestDBItem {

    private Integer id;
    private String request;
    private String file;
    private Integer valid;

    public String getRequest() {
        return request;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ComplaintRequestDBItem{" +
                "id=" + id +
                ", request='" + request + '\'' +
                ", file='" + file + '\'' +
                ", valid=" + valid +
                '}';
    }

}
