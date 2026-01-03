package com.myshop.myshopbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myshop.myshopbackend.model.User;
import com.myshop.myshopbackend.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User register(User user) {
        return repo.save(user);
    }

    public User login(String email, String password, HttpSession session) {
        // Email se user find karein
        User user = repo.findByEmail(email);

        // Debugging ke liye console par print karein (sirf check karne ke liye)
        System.out.println("Login attempt for email: " + email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            System.out.println("Login Success for: " + user.getName());
            return user;
        } else {
            System.out.println("Login Failed: Password mismatch or User not found");
            throw new RuntimeException("Invalid Email or Password");
        }
    }

    public User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}