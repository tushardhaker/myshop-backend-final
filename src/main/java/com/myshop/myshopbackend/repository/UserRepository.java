package com.myshop.myshopbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.myshop.myshopbackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    // Mobile check karne ke liye
    User findByMobile(String mobile);

    @Query("SELECT u FROM User u WHERE u.email = :id OR u.mobile = :id")
    User findByEmailOrMobile(@Param("id") String identifier);
}