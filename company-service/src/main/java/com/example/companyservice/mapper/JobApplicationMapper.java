package com.example.companyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.companyservice.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {
    List<JobApplication> getApplicationsByCompanyId(@Param("companyId") Long companyId);
    List<JobApplication> getApplicationsByJobPostId(@Param("jobPostId") Long jobPostId);
    int updateApplicationStatus(@Param("id") Long id, @Param("status") String status);
}
