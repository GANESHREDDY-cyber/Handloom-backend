package com.handloom.marketplace.dto;

import jakarta.validation.constraints.*;

public class ReviewDTOs {

    public static class ReviewRequest {
        @NotNull private Long productId;
        @NotNull @Min(1) @Max(5) private Integer rating;
        private String comment;
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class ReviewResponse {
        private Long id;
        private String buyerName;
        private Integer rating;
        private String comment;
        private String createdAt;
        public ReviewResponse() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}
