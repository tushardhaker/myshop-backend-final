package com.myshop.myshopbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByShopId(Long shopId);
}