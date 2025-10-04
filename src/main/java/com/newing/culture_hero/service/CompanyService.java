package com.newing.culture_hero.service;

import com.newing.culture_hero.entity.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {
  Company createCompany(String code, String name, String email, String phone, String address);

  Optional<Company> findById(UUID id);

  Optional<Company> findByCode(String code);

  List<Company> findAll();

  Company updateCompany(
      UUID id, String name, String email, String phone, String address, String status);

  void deleteCompany(UUID id);
}
