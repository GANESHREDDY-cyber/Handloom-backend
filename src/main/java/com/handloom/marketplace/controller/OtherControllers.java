package com.handloom.marketplace.controller;

import com.handloom.marketplace.dto.ReviewDTOs.*;
import com.handloom.marketplace.dto.CartDTOs.*;
import com.handloom.marketplace.entity.*;
import com.handloom.marketplace.exception.AppException;
import com.handloom.marketplace.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

// ── Review Controller ─────────────────────────────────
@RestController
@RequestMapping("/api/reviews")
class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        User buyer = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        Review review = new Review();
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review saved = reviewRepository.save(review);
        ReviewResponse resp = new ReviewResponse();
        resp.setId(saved.getId());
        resp.setBuyerName(buyer.getName());
        resp.setRating(saved.getRating());
        resp.setComment(saved.getComment());
        resp.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getByProduct(@PathVariable Long productId) {
        List<ReviewResponse> list = reviewRepository.findByProductId(productId).stream().map(r -> {
            ReviewResponse resp = new ReviewResponse();
            resp.setId(r.getId());
            resp.setBuyerName(r.getBuyer().getName());
            resp.setRating(r.getRating());
            resp.setComment(r.getComment());
            resp.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return resp;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

// ── Cart Controller ───────────────────────────────────
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('BUYER')")
class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository,
                          UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    private Cart getOrCreateCart(User buyer) {
        return cartRepository.findByBuyerId(buyer.getId()).orElseGet(() -> {
            Cart c = new Cart();
            c.setBuyer(buyer);
            return cartRepository.save(c);
        });
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(buildCartResponse(getOrCreateCart(getCurrentUser())));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        User buyer = getCurrentUser();
        Cart cart = getOrCreateCart(buyer);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }
        return ResponseEntity.ok(buildCartResponse(cartRepository.findById(cart.getId()).orElse(cart)));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(@RequestBody Map<String, Object> body) {
        Long itemId = Long.valueOf(body.get("itemId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException("Cart item not found", HttpStatus.NOT_FOUND));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok(buildCartResponse(getOrCreateCart(getCurrentUser())));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> remove(@PathVariable Long itemId) {
        cartItemRepository.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem i : cart.getItems()) {
                CartItemResponse ir = new CartItemResponse();
                ir.setId(i.getId());
                ir.setProductId(i.getProduct().getId());
                ir.setName(i.getProduct().getName());
                ir.setArtisanName(i.getProduct().getArtisan().getName());
                ir.setPrice(i.getProduct().getPrice());
                ir.setQuantity(i.getQuantity());
                items.add(ir);
            }
        }
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        CartResponse resp = new CartResponse();
        resp.setCartId(cart.getId());
        resp.setItems(items);
        resp.setTotal(total);
        return resp;
    }
}

// ── Wishlist Controller ───────────────────────────────
@RestController
@RequestMapping("/api/wishlist")
@PreAuthorize("hasRole('BUYER')")
class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistController(WishlistRepository wishlistRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getWishlist() {
        User buyer = getCurrentUser();
        List<Map<String, Object>> list = wishlistRepository.findByBuyerId(buyer.getId()).stream().map(w -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", w.getId());
            m.put("productId", w.getProduct().getId());
            m.put("name", w.getProduct().getName());
            m.put("price", w.getProduct().getPrice());
            m.put("artisanName", w.getProduct().getArtisan().getName());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Map<String, Long> body) {
        User buyer = getCurrentUser();
        Product product = productRepository.findById(body.get("productId"))
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        if (!wishlistRepository.existsByBuyerIdAndProductId(buyer.getId(), product.getId())) {
            Wishlist w = new Wishlist();
            w.setBuyer(buyer);
            w.setProduct(product);
            wishlistRepository.save(w);
        }
        return ResponseEntity.ok("Added to wishlist");
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        wishlistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
