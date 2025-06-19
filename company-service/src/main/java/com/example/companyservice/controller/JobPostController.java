package com.example.companyservice.controller;

import com.example.companyservice.entity.JobPost;
import com.example.companyservice.service.JobPostService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/jobposts")
public class JobPostController {

    @Resource
    private JobPostService jobPostService;

    @PostMapping
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("添加招聘")
    public String addJobPost(@RequestBody JobPost jobPost) {
        return jobPostService.addJobPost(jobPost) > 0 ? "Success" : "Failed";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("获取招聘通过id")
    public JobPost getJobPostById(@PathVariable Long id) {
        return jobPostService.getJobPostById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("获取所有招聘")
    public List<JobPost> getAllJobPosts() {
        return jobPostService.getAllJobPosts();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("更新公司招聘")
    public String updateJobPost(@RequestBody JobPost jobPost) {
        return jobPostService.updateJobPost(jobPost) > 0 ? "Updated" : "Failed";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("删除公司招聘通过id")
    public String deleteJobPost(@PathVariable Long id) {
        return jobPostService.deleteJobPost(id) > 0 ? "Deleted" : "Failed";
    }

    @GetMapping("/company/{companyId}")
    @ApiOperation("通过公司id获取招聘")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    public List<JobPost> getJobPostsByCompanyId(@PathVariable Long companyId) {
        return jobPostService.getJobPostsByCompanyId(companyId);
    }
}
