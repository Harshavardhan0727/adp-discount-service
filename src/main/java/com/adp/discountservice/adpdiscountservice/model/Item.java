package com.adp.discountservice.adpdiscountservice.model;

import lombok.*;

import javax.persistence.*;

/**
 * An item has three properties: its id, its cost, and its type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_cost", nullable = false)
    private Double itemCost;

    @Column(name = "item_type", nullable = false)
    private String itemType;
}