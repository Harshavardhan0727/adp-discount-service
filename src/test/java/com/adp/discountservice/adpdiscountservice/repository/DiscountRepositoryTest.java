package com.adp.discountservice.adpdiscountservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.adp.discountservice.adpdiscountservice.model.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

@DataJpaTest
public class DiscountRepositoryTest {
    @Autowired
    private DiscountRepository discountRepository;

    private Discount discount;

    @BeforeEach
    public void setup() {
        discount = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();
    }

    @Test
    public void givenDiscountObject_whenSave_thenReturnSavedDiscount() {
        //given
        Discount discount = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();

        // when
        Discount savedDiscount = discountRepository.save(discount);

        // then
        assertThat(savedDiscount).isNotNull();
        assertThat(savedDiscount.getId()).isGreaterThan(0);
    }

    @Test
    public void givenDiscountsList_whenFindAll_thenDiscountsList() {
        // given - precondition or setup

        Discount discount1 = Discount.builder()
                .discountCode("CDE")
                .discountPercent(15.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        discountRepository.save(discount);
        discountRepository.save(discount1);

        // when
        List<Discount> discountList = discountRepository.findAll();

        // then
        assertThat(discountList).isNotNull();
        assertThat(discountList.size()).isEqualTo(2);
    }

    @Test
    public void givenDiscountObject_whenFindByDiscountCode_thenReturnDiscountObject() {

        discountRepository.save(discount);

        // when
        Discount discountDB = discountRepository.findDiscountByDiscountCode(discount.getDiscountCode()).get();

        // then
        assertThat(discountDB).isNotNull();
    }

    @Test
    public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {

        discountRepository.save(discount);

        // when
        discountRepository.removeDiscountByDiscountCode(discount.getDiscountCode());
        Optional<Discount> discountOptional = discountRepository.findDiscountByDiscountCode(discount.getDiscountCode());

        // then
        assertThat(discountOptional).isEmpty();
    }
}