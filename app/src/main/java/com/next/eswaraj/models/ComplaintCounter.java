package com.next.eswaraj.models;

import com.eswaraj.web.dto.BaseDto;


public class ComplaintCounter extends BaseDto {

    private String name;
    private Long count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ComplaintCounter{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
