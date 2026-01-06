package com.myshop.myshopbackend.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"https://myshop-pro-tushar-2004.vercel.app", "http://localhost:5500", "http://127.0.0.1:5500"}, allowCredentials = "true")
public class PaymentController {

    private final String KEY_ID = "rzp_test_Rz1yk3tgGSCQya"; 
    private final String KEY_SECRET = "M3mSzyckXO2I6XwPLyWejqEb"; 

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            int amount = Integer.parseInt(data.get("amount").toString());
            RazorpayClient client = new RazorpayClient(KEY_ID, KEY_SECRET);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(orderRequest);
            return ResponseEntity.ok(order.toString()); // String ko proper JSON ki tarah bhejein
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}