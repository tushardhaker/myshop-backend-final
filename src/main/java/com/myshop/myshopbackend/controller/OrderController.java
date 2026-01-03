package com.myshop.myshopbackend.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myshop.myshopbackend.model.Order;
import com.myshop.myshopbackend.model.OrderItem;
import com.myshop.myshopbackend.model.Product;
import com.myshop.myshopbackend.repository.OrderItemRepository;
import com.myshop.myshopbackend.repository.OrderRepository;
import com.myshop.myshopbackend.repository.ProductRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private ProductRepository productRepo;

    @PutMapping("/item/{itemId}/review")
    @Transactional
    public ResponseEntity<?> submitReview(@PathVariable Long itemId, @RequestBody Map<String, Object> reviewData) {
        Optional<OrderItem> itemOpt = orderItemRepo.findById(itemId);
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            if (!"DELIVERED".equalsIgnoreCase(item.getStatus())) {
                return ResponseEntity.badRequest().body("Can only review delivered items.");
            }
            item.setRating((Integer) reviewData.get("rating"));
            item.setReview((String) reviewData.get("review"));
            orderItemRepo.save(item);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/place")
    @Transactional
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PLACED");
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Optional<Product> productOpt = productRepo.findByNameIgnoreCase(item.getProductName());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        if (product.getStock() >= item.getQuantity()) {
                            product.setStock(product.getStock() - item.getQuantity());
                            productRepo.save(product);
                        }
                    }
                    item.setOrder(order);
                    item.setStatus("PLACED");
                    item.setPlacedAt(LocalDateTime.now());
                }
            }
            return ResponseEntity.ok(orderRepo.save(order));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/item/{itemId}/status")
    @Transactional
    public ResponseEntity<?> updateItemStatus(@PathVariable Long itemId, @RequestParam String status) {
        Optional<OrderItem> itemOpt = orderItemRepo.findById(itemId);
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            String oldStatus = item.getStatus();
            item.setStatus(status.toUpperCase());

            LocalDateTime now = LocalDateTime.now();
            if ("SHIPPED".equalsIgnoreCase(status))
                item.setShippedAt(now);
            else if ("DELIVERED".equalsIgnoreCase(status))
                item.setDeliveredAt(now);
            else if ("CANCELLED".equalsIgnoreCase(status))
                item.setCancelledAt(now);
            else if ("RETURNED".equalsIgnoreCase(status))
                item.setReturnedAt(now);

            if (!"CANCELLED".equalsIgnoreCase(oldStatus) && !"RETURNED".equalsIgnoreCase(oldStatus)) {
                if ("CANCELLED".equalsIgnoreCase(status) || "RETURNED".equalsIgnoreCase(status)) {
                    Optional<Product> productOpt = productRepo.findByNameIgnoreCase(item.getProductName());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        product.setStock(product.getStock() + item.getQuantity());
                        productRepo.save(product);
                    }
                }
            }
            orderItemRepo.save(item);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Map<String, Object>>> getShopOrdersDetailed(@PathVariable Long shopId) {
        List<OrderItem> items = orderItemRepo.findByShopId(shopId);
        List<Map<String, Object>> response = new ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productName", item.getProductName());
            map.put("quantity", item.getQuantity());
            map.put("price", item.getPrice());
            map.put("status", item.getStatus());
            map.put("rating", item.getRating());
            map.put("review", item.getReview());
            map.put("placedAt", item.getPlacedAt());
            map.put("shippedAt", item.getShippedAt());
            map.put("deliveredAt", item.getDeliveredAt());

            if (item.getOrder() != null) {
                map.put("customerName", item.getOrder().getCustomerName());
                map.put("customerMobile", item.getOrder().getMobile());
                map.put("orderDate", item.getOrder().getOrderDate());
                map.put("address", item.getOrder().getAddress());
                map.put("userId", item.getOrder().getUserId());

                // FIXED: Adding both paymentType and paymentId to the response map
                map.put("paymentType", item.getOrder().getPaymentType());
                map.put("paymentMethod", item.getOrder().getPaymentMethod());
                map.put("paymentId", item.getOrder().getPaymentId()); // <--- YE MISSING THA

                // Sync status: Agar main Order "PAID" hai toh item status ko bhi "PAID" ki
                // tarah treat karein
                if ("PAID".equalsIgnoreCase(item.getOrder().getStatus())
                        && "PLACED".equalsIgnoreCase(item.getStatus())) {
                    map.put("status", "PAID");
                }
            }
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByUserDetailed(@PathVariable Long userId) {
        List<Order> orders = orderRepo.findByUserId(userId);
        List<Map<String, Object>> response = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("orderDate", order.getOrderDate());
            orderMap.put("status", order.getStatus());
            orderMap.put("address", order.getAddress());

            // Adding payment info here too
            orderMap.put("paymentType", order.getPaymentType());
            orderMap.put("paymentId", order.getPaymentId()); // <--- ADDED HERE TOO

            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("productName", item.getProductName());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemMap.put("shopName", item.getShopName());
                itemMap.put("shopId", item.getShopId());
                itemMap.put("itemStatus", item.getStatus());
                itemMap.put("rating", item.getRating());
                itemMap.put("review", item.getReview());
                itemMap.put("placedAt", item.getPlacedAt());
                itemMap.put("shippedAt", item.getShippedAt());
                itemMap.put("deliveredAt", item.getDeliveredAt());
                itemsList.add(itemMap);
            }
            orderMap.put("items", itemsList);
            response.add(orderMap);
        }
        return ResponseEntity.ok(response);
    }
}