package com.example.jobseekerservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jobseekerservice.entity.JobSeeker;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobSeekerMapper extends BaseMapper<JobSeeker> {
}
