package com.handloom.marketplace.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity @Table(name = "cart")
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "buyer_id", unique = true) private User buyer;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) private List<CartItem> items;

    public Cart() {}
    public Long getId() { return id; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
