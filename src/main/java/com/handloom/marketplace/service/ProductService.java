package com.handloom.marketplace.service;

import com.handloom.marketplace.dto.ProductDTOs.*;
import com.handloom.marketplace.entity.Product;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.exception.AppException;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.repository.ReviewRepository;
import com.handloom.marketplace.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public Page<ProductResponse> getAllApproved(int page, int size, String sort) {
        Sort sortObj = sort.equals("price_asc") ? Sort.by("price").ascending()
                : sort.equals("price_desc") ? Sort.by("price").descending()
                : Sort.by("createdAt").descending();
        return productRepository.findByStatus(Product.ProductStatus.APPROVED, PageRequest.of(page, size, sortObj))
                .map(this::toResponse);
    }

    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
    }

    public ProductResponse create(ProductRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User artisan = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(request.getCategory());
        product.setMaterial(request.getMaterial());
        product.setWeight(request.getWeight());
        product.setCareInstructions(request.getCareInstructions());
        product.setStockCount(request.getStockCount());
        product.setArtisan(artisan);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(request.getCategory());
        product.setStockCount(request.getStockCount());
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) { productRepository.deleteById(id); }

    public Page<ProductResponse> search(String q, int page, int size) {
        return productRepository.searchProducts(q, PageRequest.of(page, size)).map(this::toResponse);
    }

    public Page<ProductResponse> filter(String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        return productRepository.filterProducts(category, minPrice, maxPrice, PageRequest.of(page, size)).map(this::toResponse);
    }

    public List<ProductResponse> getMyProducts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User artisan = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return productRepository.findByArtisanId(artisan.getId()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product p) {
        Double rating = reviewRepository.getAverageRating(p.getId());
        long reviewCount = reviewRepository.countByProductId(p.getId());
        List<String> images = p.getImages() != null
                ? p.getImages().stream().map(img -> img.getImageUrl()).collect(Collectors.toList())
                : List.of();
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setOriginalPrice(p.getOriginalPrice());
        r.setCategory(p.getCategory());
        r.setMaterial(p.getMaterial());
        r.setWeight(p.getWeight());
        r.setStockCount(p.getStockCount());
        r.setStatus(p.getStatus().name());
        r.setArtisanName(p.getArtisan().getName());
        r.setArtisanId(p.getArtisan().getId());
        r.setImages(images);
        r.setRating(rating);
        r.setReviewCount((int) reviewCount);
        return r;
    }
}
