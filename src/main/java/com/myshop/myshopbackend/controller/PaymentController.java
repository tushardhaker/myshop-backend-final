package com.myshop.myshopbackend.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    // TODO: Inhe apni Notepad wali keys se replace karein
    private final String KEY_ID = "rzp_test_Rz1yk3tgGSCQya"; 
    private final String KEY_SECRET = "M3mSzyckXO2I6XwPLyWejqEb"; 

    @PostMapping("/create-order")
    public String createOrder(@RequestBody Map<String, Object> data) {
        try {
            // Frontend se amount Rupees mein aayega (e.g., 500)
            int amount = Integer.parseInt(data.get("amount").toString());

            // Razorpay Client initialize karein
            RazorpayClient client = new RazorpayClient(KEY_ID, KEY_SECRET);

            // Order details set karein
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Razorpay amount paise mein leta hai (1 INR = 100 paise)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            // Order create karein
            Order order = client.orders.create(orderRequest);
            
            // Return the order JSON string to frontend
            return order.toString(); 
        } catch (RazorpayException e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Internal Server Error\"}";
        }
    }
}