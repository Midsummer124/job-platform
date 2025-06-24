package com.example.jobseekerservice.controller;

import com.example.jobseekerservice.entity.JobSeeker;
import com.example.jobseekerservice.service.JobSeekerService;
import com.example.jobseekerservice.utils.LogProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// JobSeekerController.java
@RestController
@RequestMapping("/admin/jobseekers")
@Api(tags = "求职者 JobSeeker 接口（管理员）")
public class JobSeekerController {

    @Autowired
    private JobSeekerService jobSeekerService;
    @Autowired
    private LogProducer logProducer;

    @ApiOperation("获取全部求职者")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    public List<JobSeeker> getAllJobSeekers() {
        logProducer.sendLog("jobseeker-service", "INFO", "获取全部求职者");
        return jobSeekerService.list();
    }

    @ApiOperation("根据ID获取求职者")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    public JobSeeker getJobSeeker(@PathVariable Long id) {
        logProducer.sendLog("jobseeker-service", "INFO", "获取求职者");
        return jobSeekerService.getById(id);
    }

    @ApiOperation("新增求职者")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean createJobSeeker(@RequestBody JobSeeker jobSeeker) {
        logProducer.sendLog("jobseeker-service", "INFO", "添加求职者");
        return jobSeekerService.save(jobSeeker);
    }

    @ApiOperation("更新求职者信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean updateJobSeeker(@PathVariable Long id, @RequestBody JobSeeker jobSeeker) {
        logProducer.sendLog("jobseeker-service", "INFO", "更新求职者");
        jobSeeker.setId(id);
        return jobSeekerService.updateById(jobSeeker);
    }

    @ApiOperation("删除求职者")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public boolean deleteJobSeeker(@PathVariable Long id) {
        logProducer.sendLog("jobseeker-service", "INFO", "删除求职者");
        return jobSeekerService.removeById(id);
    }
}
