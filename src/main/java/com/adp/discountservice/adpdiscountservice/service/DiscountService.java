package com.adp.discountservice.adpdiscountservice.service;

import com.adp.discountservice.adpdiscountservice.controller.DiscountController;
import com.adp.discountservice.adpdiscountservice.exception.DiscountAlreadyExistsException;
import com.adp.discountservice.adpdiscountservice.model.Discount;
import com.adp.discountservice.adpdiscountservice.model.Item;
import com.adp.discountservice.adpdiscountservice.repository.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiscountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountController.class);
    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * Multiple discounts may apply but the system should select the best discount for the customer.
     * Respond with the best discount code and with the total cost of the items after applying the discount.
     */
    public Map<String, Double> computeBestDiscount(List<Item> items) {
        List<Discount> discounts = discountRepository.findAll();
        Map<String, Double> map = new HashMap<>();
        Map<String, Double> resp = new HashMap<>();

        for (Discount d : discounts) {
            double individualDiscountTotalCost = 0.0;
            String discountType = d.getDiscountType(); // 3 types Ex vals: "ITEM_TYPE", "TOTAL_COST", "ITEM_COUNT"
            for (Item item : items) {
                // DISCOUNT TYPE 1
                if (discountType.equals("ITEM_TYPE")) {
                    String discountTypeValue = d.getDiscountTypeValue();
                    if (discountTypeValue.equals(item.getItemType())) {  // "CLOTHES" or "ELECTRONICS" matching actual values
                        // then this discount is applicable to this item. So calculate the final cost
                        individualDiscountTotalCost += item.getItemCost() - (item.getItemCost() * d.getDiscountPercent() * 0.01);
                    } else {
                        individualDiscountTotalCost += item.getItemCost();
                        continue;
                    }
                }

                // DISCOUNT TYPE 2
                else if (discountType.equals("TOTAL_COST")) {
                    double discountTypeValue = Double.parseDouble(d.getDiscountTypeValue());
                    if (discountTypeValue < item.getItemCost()) {
                        individualDiscountTotalCost += item.getItemCost() - (item.getItemCost() * d.getDiscountPercent() * 0.01);
                    } else {
                        individualDiscountTotalCost += item.getItemCost();
                        continue;
                    }
                }

                // DISCOUNT TYPE 3
                else if (discountType.equals("ITEM_COUNT")) {
                    int discountTypeValue = Integer.parseInt(d.getDiscountTypeValue());  // Ex: 2 or more
                    long discountedItemId = d.getDiscountedItemId();   // discount applicable to only items with id: 123 Ex.
                    int numOfItemsWithDiscountedItemId = (int) items.stream().filter(itm -> itm.getItemId() == discountedItemId).count();

                    if (numOfItemsWithDiscountedItemId >= discountTypeValue) {
                        individualDiscountTotalCost += item.getItemCost() - (item.getItemCost() * d.getDiscountPercent() * 0.01); // no discount, just adding up the total final cost of the cart
                    } else {
                        individualDiscountTotalCost += item.getItemCost();
                        continue;  // move to next item as the discount is not matching to any of the given cases
                    }
                }
            }
            // for a particular discount code, you calculated the total cost --> now put it in a map where discount code is key & value is total cost of cart
            map.put(d.getDiscountCode(), individualDiscountTotalCost);
        }

        String bestDiscountCode = Collections.min(map.entrySet(), Map.Entry.comparingByValue()).getKey();

        LOGGER.info("Best discount is {}", bestDiscountCode);
        resp.put(bestDiscountCode, map.get(bestDiscountCode));
        return resp;
    }

    /**
     * Get all the available discounts from the system
     */
    public List<Discount> findAllDiscounts() {
        return discountRepository.findAll();
    }

    /**
     * Find discount by discount code
     */
    public Optional<Discount> findByDiscountCode(String discountCode) {
        Optional<Discount> discountByDiscountCode = discountRepository.findDiscountByDiscountCode(discountCode);
        return discountByDiscountCode;
    }

    /**
     * Add a new discount
     */
    public Discount addDiscount(Discount discount) {
        Optional<Discount> savedDiscount = discountRepository.findDiscountByDiscountCode(discount.getDiscountCode());

        if (savedDiscount.isPresent()) {
            throw new DiscountAlreadyExistsException("Discount already exists with given code: " + discount.getDiscountCode());
        }
        Discount _discount = discountRepository.save(discount);
        LOGGER.info("Discount {} successfully added to the system", _discount.getDiscountCode());
        return _discount;
    }

    /**
     * Remove an existing discount
     */
    public void removeDiscount(String discountCode) {
        discountRepository.removeDiscountByDiscountCode(discountCode);
    }
}