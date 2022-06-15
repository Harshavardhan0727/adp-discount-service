package com.adp.discountservice.adpdiscountservice.controller;

import com.adp.discountservice.adpdiscountservice.model.Discount;
import com.adp.discountservice.adpdiscountservice.model.Item;
import com.adp.discountservice.adpdiscountservice.service.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountController.class);
    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    /**
     * Multiple discounts may apply but the system should select the best discount for the customer.
     * Respond with the best discount code and with the total cost of the items after applying the discount.
     */
    @PostMapping("/best-discount")
    public ResponseEntity<Map<String, Double>> computeBestDiscount(@RequestBody List<Item> items) {
        try {
            Map<String, Double> discountMap = discountService.computeBestDiscount(items);
            LOGGER.info("Discount Map is {}", discountMap);
            return new ResponseEntity<>(discountMap, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all the available discounts from the system
     */
    @GetMapping
    public ResponseEntity<List<Discount>> getAllAvailableDiscounts() {
        try {
            List<Discount> availableDiscounts = new ArrayList<>();
            discountService.findAllDiscounts().forEach(availableDiscounts::add);

            if (availableDiscounts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Discount>>(availableDiscounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Find discount by given discount code
     */
    @GetMapping("/{discountCode}")
    public ResponseEntity<Discount> getDiscountByCode(@PathVariable("discountCode") String discountCode) {
        try {
            return discountService.findByDiscountCode(discountCode)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The system should expose an API to add a new discount
     */
    @PostMapping
    public ResponseEntity<Discount> addDiscount(@RequestBody Discount discount) {
        try {
            Discount _discount = discountService.addDiscount(discount);
            return new ResponseEntity<>(_discount, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The system should expose an API to remove an existing discount
     */
    @DeleteMapping("/{discountCode}")
    public ResponseEntity<String> removeDiscount(@PathVariable("discountCode") String discountCode) {
        try {
            discountService.removeDiscount(discountCode);
            return new ResponseEntity<String>("Discount deleted from the system successfully!.", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}