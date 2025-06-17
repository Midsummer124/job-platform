package com.example.companyservice.service;

import com.example.companyservice.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {
    List<JobApplication> getApplicationsByCompanyId(Long companyId);
    List<JobApplication> getApplicationsByJobPostId(Long jobPostId);
    boolean updateStatus(Long applicationId, String status);
}
