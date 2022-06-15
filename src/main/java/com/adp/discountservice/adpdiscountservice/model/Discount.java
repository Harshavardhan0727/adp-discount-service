package com.adp.discountservice.adpdiscountservice.model;

import lombok.*;

import javax.persistence.*;

/**
 * There are three types of discounts: by item type, by total cost of items, by count of a particular item
 * The discount is applied via a percentage. For example, all items of type X are 15% off.
 * Each discount has a corresponding Code.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @Column(name = "discount_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "discount_code", nullable = false)
    private String discountCode;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercent;

    @Column(name = "discount_type", nullable = false)
    // 3 possible values: Ex -> "CLOTHES" or "100" or "quantity"  // ID
    private String discountType;

    @Column(name = "discount_type_value", nullable = false)
    private String discountTypeValue;

    @Column(name = "discounted_item_id")
    private Long discountedItemId;
}