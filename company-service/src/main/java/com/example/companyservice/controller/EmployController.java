package com.example.companyservice.controller;

import com.example.companyservice.entity.JobApplication;
import com.example.companyservice.service.JobApplicationService;
import com.example.companyservice.utils.LogProducer;
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
    @Autowired
    private LogProducer logProducer;

    @ApiOperation("获取公司所有投递记录")
    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplications(@RequestParam Long companyId) {
        logProducer.sendLog("company-service", "INFO", "获取公司投递记录");
        return ResponseEntity.ok(applicationService.getApplicationsByCompanyId(companyId));
    }

    @ApiOperation("根据岗位获取申请列表")
    @GetMapping("/jobpost/{id}/applications")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplicationsByJob(@PathVariable("id") Long jobPostId) {
        logProducer.sendLog("company-service", "INFO", "根据岗位获取申请列表");
        return ResponseEntity.ok(applicationService.getApplicationsByJobPostId(jobPostId));
    }

    @ApiOperation("更新投递状态（接受、拒绝等）")
    @PutMapping("/application/{id}/status")
    @PreAuthorize("hasAnyRole( 'ENTERPRISE', 'ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long applicationId,
                                          @RequestParam String status) {
        logProducer.sendLog("company-service", "INFO", "更新投递状态");
        boolean updated = applicationService.updateStatus(applicationId, status);
        if (updated) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失败");
        }
    }
    @ApiOperation("根据 ID 获取投递记录")
    @GetMapping("/application/{id}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<JobApplication> getApplication(@PathVariable Long id) {
        logProducer.sendLog("company-service", "INFO", "获取投递记录");
        JobApplication app = applicationService.getById(id);
        return app != null ? ResponseEntity.ok(app) : ResponseEntity.notFound().build();
    }

    @ApiOperation("获取所有投递记录")
    @GetMapping("/application")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        logProducer.sendLog("company-service", "INFO", "获取所有投递记录");
        return ResponseEntity.ok(applicationService.getAll());
    }

    @ApiOperation("添加投递记录")
    @PostMapping("/application")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE')")
    public ResponseEntity<String> createApplication(@RequestBody JobApplication jobApplication) {
        logProducer.sendLog("company-service", "INFO", "添加投递记录");
        boolean created = applicationService.createApplication(jobApplication);
        return created ? ResponseEntity.ok("创建成功") : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("创建失败");
    }

    @ApiOperation("删除投递记录")
    @DeleteMapping("/application/{id}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        logProducer.sendLog("company-service", "INFO", "删除投递记录");
        boolean deleted = applicationService.deleteById(id);
        return deleted ? ResponseEntity.ok("删除成功") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("删除失败");
    }

}

