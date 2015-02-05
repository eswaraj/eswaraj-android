package com.next.eswaraj.helpers;


import com.next.eswaraj.models.ComplaintDto;
import com.next.eswaraj.models.ComplaintFilter;
import com.next.eswaraj.models.ComplaintStatusCounters;
import com.eswaraj.web.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;

public class ComplaintFilterHelper {

    public static List<ComplaintDto> filter(List<ComplaintDto> complaintDtoList, ComplaintFilter filter) {
        if(filter == null) {
            return complaintDtoList;
        }
        if(filter.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.CATEGORY) {
            return filterByCategory(complaintDtoList, filter.getCategoryId());
        }
        if(filter.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.STATUS) {
            return filterByStatus(complaintDtoList, filter.getStatus());
        }
        if(filter.getComplaintFilterType() == ComplaintFilter.ComplaintFilterType.NONE) {
            return complaintDtoList;
        }
        return null;
    }

    private static List<ComplaintDto> filterByCategory(List<ComplaintDto> complaintDtoList, Long id) {
        List<ComplaintDto> filteredList = new ArrayList<>();
        for(ComplaintDto complaintDto : complaintDtoList) {
            for(CategoryDto categoryDto : complaintDto.getCategories()) {
                if (categoryDto.getId().equals(id)) {
                    filteredList.add(complaintDto);
                }
            }
        }
        return filteredList;
    }

    private static List<ComplaintDto> filterByStatus(List<ComplaintDto> complaintDtoList, String status) {
        List<ComplaintDto> filteredList = new ArrayList<>();
        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getStatus().equals(status)) {
                filteredList.add(complaintDto);
            }
        }
        return filteredList;
    }

    public static ComplaintStatusCounters getStatusCounters(List<ComplaintDto> complaintDtoList) {
        ComplaintStatusCounters counters = new ComplaintStatusCounters();
        for(ComplaintDto complaintDto : complaintDtoList) {
            if(complaintDto.getStatus().equals("Done")) {
                counters.setClosed(counters.getClosed() + 1);
            }
            else {
                counters.setOpen(counters.getOpen() + 1);
            }
        }
        counters.setTotal(complaintDtoList.size());
        return counters;
    }

}
