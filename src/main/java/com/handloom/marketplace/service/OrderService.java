package com.handloom.marketplace.service;

import com.handloom.marketplace.dto.OrderDTOs.*;
import com.handloom.marketplace.entity.*;
import com.handloom.marketplace.exception.AppException;
import com.handloom.marketplace.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
            OrderItem oi = new OrderItem();
            oi.setProduct(product);
            oi.setQuantity(itemReq.getQuantity());
            oi.setUnitPrice(product.getPrice());
            items.add(oi);
        }

        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setBuyer(buyer);
        order.setTotalAmount(total);
        order.setShippingAddress(request.getShippingAddress());
        order.setItems(items);
        items.forEach(i -> i.setOrder(order));

        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return orderRepository.findByBuyerId(buyer.getId()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Order not found", HttpStatus.NOT_FOUND));
    }

    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException("Order not found", HttpStatus.NOT_FOUND));
        order.setStatus(Order.OrderStatus.valueOf(status));
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (o.getItems() != null) {
            for (OrderItem i : o.getItems()) {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setProductId(i.getProduct().getId());
                ir.setProductName(i.getProduct().getName());
                ir.setQuantity(i.getQuantity());
                ir.setUnitPrice(i.getUnitPrice());
                itemResponses.add(ir);
            }
        }
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setStatus(o.getStatus().name());
        r.setTotalAmount(o.getTotalAmount());
        r.setShippingAddress(o.getShippingAddress());
        r.setTrackingNumber(o.getTrackingNumber());
        r.setItems(itemResponses);
        r.setCreatedAt(o.getCreatedAt() != null ? o.getCreatedAt().toString() : null);
        return r;
    }
}
