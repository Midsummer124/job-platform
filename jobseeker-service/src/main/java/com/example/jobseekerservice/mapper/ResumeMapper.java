package com.example.jobseekerservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jobseekerservice.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}
