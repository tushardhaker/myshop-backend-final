package com.myshop.myshopbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.myshop.myshopbackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    User findByMobile(String mobile);

    // This helps to find user by either email or mobile during login
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.mobile = :identifier")
    User findByEmailOrMobile(@Param("identifier") String identifier);
}