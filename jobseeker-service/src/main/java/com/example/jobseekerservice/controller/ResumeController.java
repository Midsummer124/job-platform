package com.example.jobseekerservice.controller;

import com.example.jobseekerservice.entity.Resume;
import com.example.jobseekerservice.service.ResumeService;
import com.example.jobseekerservice.utils.LogProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resumes")
@Api(tags = "简历 Resume 接口")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;
    @Autowired
    private LogProducer logProducer;

    @ApiOperation("获取某个求职者的全部简历")
    @GetMapping("/jobseeker/{jobSeekerId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Resume> getByJobSeeker(@PathVariable Long jobSeekerId) {
        logProducer.sendLog("jobseeker-service", "INFO", "获取求职者全部简历");
        return resumeService.getResumesByJobSeekerId(jobSeekerId);
    }

    @ApiOperation("新增简历")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean createResume(@RequestBody Resume resume) {
        logProducer.sendLog("jobseeker-service", "INFO", "新增简历");
        return resumeService.save(resume);
    }

    @ApiOperation("更新简历")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean updateResume(@PathVariable Long id, @RequestBody Resume resume) {
        logProducer.sendLog("jobseeker-service", "INFO", "更新简历");
        resume.setId(id);
        return resumeService.updateById(resume);
    }

    @ApiOperation("删除简历")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean deleteResume(@PathVariable Long id) {
        logProducer.sendLog("jobseeker-service", "INFO", "删除简历");
        return resumeService.removeById(id);
    }

    @ApiOperation("将某份简历设置为默认简历")
    @PostMapping("/{id}/set-default")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public boolean setDefaultResume(@PathVariable Long id) {
        logProducer.sendLog("jobseeker-service", "INFO", "设置默认简历");
        return resumeService.setDefaultResume(id);
    }
}
