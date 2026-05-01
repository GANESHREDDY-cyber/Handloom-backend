package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.*;
import com.handloom.marketplace.exception.AppException;
import com.handloom.marketplace.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class MarketingController {

    private final CampaignRepository campaignRepository;
    private final BannerRepository bannerRepository;
    private final UserRepository userRepository;

    public MarketingController(CampaignRepository campaignRepository, BannerRepository bannerRepository, UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.bannerRepository = bannerRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/campaigns")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<List<Campaign>> getCampaigns() {
        return ResponseEntity.ok(campaignRepository.findAll());
    }

    @PostMapping("/api/campaigns")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Map<String, String> body) {
        Campaign c = new Campaign();
        c.setName(body.get("name"));
        c.setType(body.get("type"));
        if (body.get("startDate") != null) c.setStartDate(LocalDate.parse(body.get("startDate")));
        if (body.get("endDate") != null) c.setEndDate(LocalDate.parse(body.get("endDate")));
        c.setCreatedBy(getCurrentUser());
        return ResponseEntity.ok(campaignRepository.save(c));
    }

    @PutMapping("/api/campaigns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Campaign c = campaignRepository.findById(id)
                .orElseThrow(() -> new AppException("Campaign not found", HttpStatus.NOT_FOUND));
        if (body.containsKey("name")) c.setName(body.get("name"));
        if (body.containsKey("status")) c.setStatus(Campaign.CampaignStatus.valueOf(body.get("status")));
        return ResponseEntity.ok(campaignRepository.save(c));
    }

    @DeleteMapping("/api/campaigns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/banners")
    public ResponseEntity<List<Banner>> getBanners() {
        return ResponseEntity.ok(bannerRepository.findByIsActiveTrue());
    }

    @PostMapping("/api/banners")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<Banner> createBanner(@RequestBody Map<String, String> body) {
        Banner b = new Banner();
        b.setTitle(body.get("title"));
        b.setImageUrl(body.get("imageUrl"));
        b.setLinkUrl(body.get("linkUrl"));
        b.setPosition(body.get("position"));
        b.setCreatedBy(getCurrentUser());
        return ResponseEntity.ok(bannerRepository.save(b));
    }

    @DeleteMapping("/api/banners/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MARKETING')")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
