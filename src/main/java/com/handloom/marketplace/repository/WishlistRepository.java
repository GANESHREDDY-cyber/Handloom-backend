package com.handloom.marketplace.repository;
import com.handloom.marketplace.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByBuyerId(Long buyerId);
    void deleteByBuyerIdAndProductId(Long buyerId, Long productId);
    boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);
}
