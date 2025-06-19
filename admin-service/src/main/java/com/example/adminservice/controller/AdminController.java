package com.example.adminservice.controller;

import com.example.adminservice.client.CompanyClient;
import com.example.adminservice.client.JobSeekerClient;
import com.example.adminservice.entity.CompanyDTO;
import com.example.adminservice.entity.JobSeekerDTO;
import com.example.adminservice.entity.StatisticsDTO;
import com.example.adminservice.service.AdminService;
import com.example.companyservice.entity.Company;
import com.example.jobseekerservice.entity.JobSeeker;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CompanyClient companyClient;

    @Autowired
    private JobSeekerClient jobSeekerClient;


    @GetMapping("/statistics")
    @ApiOperation("获取统计信息")
    public StatisticsDTO getStatistics() {
        return adminService.getStatistics();
    }

    @GetMapping("/stats")
    @ApiOperation("获取统计数据")
    public Map<String, Long> getSystemStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("companyCount", adminService.getCompanyCount());
        stats.put("jobSeekerCount", adminService.getJobSeekerCount());
        stats.put("jobPostCount", adminService.getJobPostCount());
        stats.put("applicationCount", adminService.getApplicationCount());
        return stats;
    }

    @GetMapping("/companies")
    @ApiOperation("获取所有公司信息")
    public List<CompanyDTO> getAllCompanies() {
        List<Company> companies = companyClient.getAllCompanies();
        return companies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    @GetMapping("/jobseekers")
    @ApiOperation("获取所有求职者信息")
    public List<JobSeekerDTO> getAllJobSeekers() {
        List<JobSeeker> seekers = jobSeekerClient.getAllJobSeekers();
        return seekers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setIndustry(company.getIndustry());
        dto.setAddress(company.getAddress());
        dto.setWebsite(company.getWebsite());
        dto.setDescription(company.getDescription());
        dto.setVerified(company.getVerified());
        dto.setCreatedAt(company.getCreatedAt());
        return dto;
    }

    private JobSeekerDTO convertToDTO(JobSeeker seeker) {
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setId(seeker.getId());
        dto.setFullName(seeker.getFullName());
        dto.setGender(seeker.getGender());
        dto.setBirthDate(seeker.getBirthDate());
        dto.setEducationLevel(seeker.getEducationLevel());
        dto.setWorkExperience(seeker.getWorkExperience());
        dto.setSkills(seeker.getSkills());
        dto.setProfileStatus(seeker.getProfileStatus());
        return dto;
    }


}
