package com.adp.discountservice.adpdiscountservice.repository;

import com.adp.discountservice.adpdiscountservice.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    Optional<Discount> findDiscountByDiscountCode(String discountCode);
    void removeDiscountByDiscountCode(String discountCode);
}