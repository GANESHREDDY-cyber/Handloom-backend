package com.handloom.marketplace.repository;
import com.handloom.marketplace.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    List<Product> findByArtisanId(Long artisanId);
    long countByStatus(Product.ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.category) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchProducts(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (:category IS NULL OR p.category = :category) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> filterProducts(@Param("category") String category, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
}
