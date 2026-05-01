package com.handloom.marketplace.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class CartDTOs {

    public static class AddToCartRequest {
        @NotNull private Long productId;
        @NotNull @Min(1) private Integer quantity;
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class CartResponse {
        private Long cartId;
        private List<CartItemResponse> items;
        private BigDecimal total;
        public CartResponse() {}
        public Long getCartId() { return cartId; }
        public void setCartId(Long cartId) { this.cartId = cartId; }
        public List<CartItemResponse> getItems() { return items; }
        public void setItems(List<CartItemResponse> items) { this.items = items; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }

    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String name;
        private String artisanName;
        private BigDecimal price;
        private Integer quantity;
        private String image;
        public CartItemResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getArtisanName() { return artisanName; }
        public void setArtisanName(String artisanName) { this.artisanName = artisanName; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }
}
