package com.newing.culture_hero.controller;

import com.newing.culture_hero.entity.Company;
import com.newing.culture_hero.service.CompanyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PreAuthorize("hasAnyRole('CONSULTANT_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Company>> getCompanies() {
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("hasRole('CONSULTANT_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Company> createCompany(@RequestBody Company companyRequest) {
        Company company =
                companyService.createCompany(
                        companyRequest.getCode(),
                        companyRequest.getName(),
                        companyRequest.getEmail(),
                        companyRequest.getPhone(),
                        companyRequest.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @PreAuthorize("hasRole('CLIENT_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable UUID id) {
        UUID uuid = UUID.fromString(String.valueOf(id));
        return companyService
                .findById(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('CLIENT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(
            @PathVariable UUID id, @RequestBody Company companyRequest) {
        Company updatedCompany =
                companyService.updateCompany(
                        id,
                        companyRequest.getName(),
                        companyRequest.getEmail(),
                        companyRequest.getPhone(),
                        companyRequest.getAddress(),
                        companyRequest.getStatus());
        return ResponseEntity.ok(updatedCompany);
    }

    @PreAuthorize("hasRole('CONSULTANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
