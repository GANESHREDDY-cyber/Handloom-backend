package com.handloom.marketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "wishlist")
public class Wishlist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "buyer_id", nullable = false) private User buyer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    private LocalDateTime addedAt;
    @PrePersist protected void onCreate() { addedAt = LocalDateTime.now(); }

    public Wishlist() {}
    public Long getId() { return id; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public LocalDateTime getAddedAt() { return addedAt; }
}
