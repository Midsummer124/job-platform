package com.example.adminservice.client;

import com.example.companyservice.entity.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("company/api/company")
    List<Company> getAllCompanies();
}
