package com.myshop.myshopbackend.service;

import java.util.List;

import com.myshop.myshopbackend.model.Order;

public interface OrderService {
    List<Order> getOrdersByUserId(Long userId);
}