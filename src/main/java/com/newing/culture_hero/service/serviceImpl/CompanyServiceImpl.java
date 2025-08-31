package com.newing.culture_hero.service.serviceImpl;

import com.newing.culture_hero.entity.Company;
import com.newing.culture_hero.repository.CompanyRepository;
import com.newing.culture_hero.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Company createCompany(String code, String name, String email, String phone, String address) {
        if (companyRepository.existsByCode(code)) {
            throw new RuntimeException("Company code already exists: " + code);
        }
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setCode(code);
        company.setName(name);
        company.setEmail(email);
        company.setPhone(phone);
        company.setAddress(address);
        company.setStatus("ACTIVE");
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Company> findByCode(String code) {
        return companyRepository.findByCode(code);
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional
    public Company updateCompany(UUID id, String name, String email, String phone, String address, String status) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));
        company.setName(name);
        company.setEmail(email);
        company.setPhone(phone);
        company.setAddress(address);
        company.setStatus(status);
        company.setUpdatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    @Override
    public void deleteCompany(UUID id) {
        companyRepository.deleteById(id);
    }
}
