package com.example.companyservice.controller;

import com.example.companyservice.entity.JobApplication;
import com.example.companyservice.service.JobApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company/employ")
@Api(tags = "招聘管理接口")
public class EmployController {

    @Autowired
    private JobApplicationService applicationService;

    @ApiOperation("获取公司所有投递记录")
    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplications(@RequestParam Long companyId) {
        return ResponseEntity.ok(applicationService.getApplicationsByCompanyId(companyId));
    }

    @ApiOperation("根据岗位获取申请列表")
    @GetMapping("/jobpost/{id}/applications")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplicationsByJob(@PathVariable("id") Long jobPostId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobPostId(jobPostId));
    }

    @ApiOperation("更新投递状态（接受、拒绝等）")
    @PutMapping("/application/{id}/status")
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long applicationId,
                                          @RequestParam String status) {
        boolean updated = applicationService.updateStatus(applicationId, status);
        if (updated) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败");
        }
    }
}

