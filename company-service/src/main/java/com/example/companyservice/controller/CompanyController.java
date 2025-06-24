package com.example.companyservice.controller;

import com.example.companyservice.entity.Company;
import com.example.companyservice.service.CompanyService;
import com.example.companyservice.utils.LogProducer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private LogProducer logProducer;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("获取公司通过id")
    public Company getCompany(@PathVariable Long id) {
        logProducer.sendLog("company-service", "INFO", "获取公司信息");
        return companyService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ENTERPRISE', 'ADMIN')")
    @ApiOperation("获取所有公司")
    public List<Company> getAllCompanies() {
        return companyService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation("创建公司")
    public String createCompany(@RequestBody Company company) {
        logProducer.sendLog("company-service", "INFO", "创建公司");
        return companyService.createCompany(company) ? "Created successfully" : "Failed to create";
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation("更新公司信息")
    public String updateCompany(@RequestBody Company company) {
        logProducer.sendLog("company-service", "INFO", "更新公司");
        return companyService.updateCompany(company) ? "Updated successfully" : "Failed to update";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation("删除公司通过id")
    public String deleteCompany(@PathVariable Long id) {
        logProducer.sendLog("company-service", "INFO", "删除公司");
        return companyService.deleteCompany(id) ? "Deleted successfully" : "Failed to delete";
    }
}
