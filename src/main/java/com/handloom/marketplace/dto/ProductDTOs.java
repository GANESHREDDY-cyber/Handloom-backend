package com.handloom.marketplace.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductDTOs {

    public static class ProductRequest {
        @NotBlank private String name;
        private String description;
        @NotNull private BigDecimal price;
        private BigDecimal originalPrice;
        @NotBlank private String category;
        private String material;
        private String weight;
        private String careInstructions;
        @NotNull private Integer stockCount;
        public String getName() { return name; }
        public void setName(String n) { this.name = n; }
        public String getDescription() { return description; }
        public void setDescription(String d) { this.description = d; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal p) { this.price = p; }
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal op) { this.originalPrice = op; }
        public String getCategory() { return category; }
        public void setCategory(String c) { this.category = c; }
        public String getMaterial() { return material; }
        public void setMaterial(String m) { this.material = m; }
        public String getWeight() { return weight; }
        public void setWeight(String w) { this.weight = w; }
        public String getCareInstructions() { return careInstructions; }
        public void setCareInstructions(String ci) { this.careInstructions = ci; }
        public Integer getStockCount() { return stockCount; }
        public void setStockCount(Integer sc) { this.stockCount = sc; }
    }

    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String category;
        private String material;
        private String weight;
        private Integer stockCount;
        private String status;
        private String artisanName;
        private Long artisanId;
        private List<String> images;
        private Double rating;
        private Integer reviewCount;
        public ProductResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public String getWeight() { return weight; }
        public void setWeight(String weight) { this.weight = weight; }
        public Integer getStockCount() { return stockCount; }
        public void setStockCount(Integer stockCount) { this.stockCount = stockCount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getArtisanName() { return artisanName; }
        public void setArtisanName(String artisanName) { this.artisanName = artisanName; }
        public Long getArtisanId() { return artisanId; }
        public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getReviewCount() { return reviewCount; }
        public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    }
}
