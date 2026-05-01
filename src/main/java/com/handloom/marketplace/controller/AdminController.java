package com.handloom.marketplace.controller;

import com.handloom.marketplace.dto.AnalyticsDTOs.DashboardStats;
import com.handloom.marketplace.dto.AuthDTOs.UserDTO;
import com.handloom.marketplace.entity.*;
import com.handloom.marketplace.exception.AppException;
import com.handloom.marketplace.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalArtisans(userRepository.countByRole(User.Role.ARTISAN));
        stats.setTotalProducts(productRepository.count());
        stats.setTotalOrders(orderRepository.count());
        stats.setRevenue(orderRepository.getTotalRevenue());
        stats.setPendingApprovals(productRepository.countByStatus(Product.ProductStatus.PENDING));

        List<Map<String, Object>> recentOrders = orderRepository.findAll().stream().limit(10).map(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", "ORD" + String.format("%03d", o.getId()));
            m.put("buyer", o.getBuyer().getName());
            m.put("amount", o.getTotalAmount());
            m.put("status", o.getStatus().name());
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);
        result.put("recentOrders", recentOrders);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setRole(User.Role.valueOf(body.get("role")));
        userRepository.save(user);
        return ResponseEntity.ok("Role updated");
    }

    @PutMapping("/artisans/{id}/approve")
    public ResponseEntity<String> approveArtisan(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);
        return ResponseEntity.ok("Artisan approved");
    }

    @PutMapping("/products/{id}/approve")
    public ResponseEntity<String> approveProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        product.setStatus(Product.ProductStatus.APPROVED);
        productRepository.save(product);
        return ResponseEntity.ok("Product approved");
    }

    @PutMapping("/products/{id}/reject")
    public ResponseEntity<String> rejectProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        product.setStatus(Product.ProductStatus.REJECTED);
        productRepository.save(product);
        return ResponseEntity.ok("Product rejected");
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardStats> getAnalytics() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalArtisans(userRepository.countByRole(User.Role.ARTISAN));
        stats.setTotalProducts(productRepository.count());
        stats.setTotalOrders(orderRepository.count());
        stats.setRevenue(orderRepository.getTotalRevenue());
        return ResponseEntity.ok(stats);
    }
}
