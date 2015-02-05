package com.next.eswaraj.models;


public class ComplaintStatusCounters {

    private Integer total;
    private Integer open;
    private Integer closed;

    public ComplaintStatusCounters() {
        total = 0;
        open = 0;
        closed = 0;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getClosed() {
        return closed;
    }

    public void setClosed(Integer closed) {
        this.closed = closed;
    }
}
