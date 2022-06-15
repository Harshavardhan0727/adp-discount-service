package com.adp.discountservice.adpdiscountservice.service;

import com.adp.discountservice.adpdiscountservice.exception.DiscountAlreadyExistsException;
import com.adp.discountservice.adpdiscountservice.model.Discount;
import com.adp.discountservice.adpdiscountservice.model.Item;
import com.adp.discountservice.adpdiscountservice.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {
    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private Discount discount;

    @BeforeEach
    public void setup() {
        discount = Discount.builder()
                .id(1L)
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();
    }

    @Test
    public void givenItemList_whenComputeBestDiscount_thenReturnDiscountMap() {
        // given
        Discount d1 = Discount.builder()
                .id(2L)
                .discountCode("CDE")
                .discountPercent(15.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        Item item1 = Item.builder()
                .itemId(123L)
                .itemCost(50.0)
                .itemType("CLOTHES")
                .build();

        List<Item> lst = new ArrayList<Item>();
        lst.add(item1);

        given(discountRepository.findAll()).willReturn(List.of(discount, d1));

        // when
        Map<String, Double> bestDiscountMap = discountService.computeBestDiscount(lst);

        // then
        assertThat(bestDiscountMap).containsEntry("ABC", 45.0);
    }

    @Test
    public void givenItemList1_whenComputeBestDiscount_thenReturnDiscountMap() {
        // given
        Discount d1 = Discount.builder()
                .id(2L)
                .discountCode("CDE")
                .discountPercent(15.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        Discount d2 = Discount.builder()
                .id(3L)
                .discountCode("FGH")
                .discountPercent(20.0)
                .discountType("ITEM_COUNT")
                .discountTypeValue("2")
                .discountedItemId(123L)
                .build();

        Item item1 = Item.builder()
                .itemId(123L)
                .itemCost(50.0)
                .itemType("CLOTHES")
                .build();

        List<Item> lst = new ArrayList<Item>();
        lst.add(item1);
        lst.add(item1);
        lst.add(item1);
        lst.add(item1);
        lst.add(item1);

        given(discountRepository.findAll()).willReturn(List.of(discount, d1, d2));

        // when
        Map<String, Double> bestDiscountMap = discountService.computeBestDiscount(lst);

        // then
        assertThat(bestDiscountMap).containsEntry("FGH", 200.0);
    }

    @Test
    public void givenItemList2_whenComputeBestDiscount_thenReturnDiscountMap() {
        // given
        Discount d1 = Discount.builder()
                .id(2L)
                .discountCode("CDE")
                .discountPercent(15.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        Item item1 = Item.builder()
                .itemId(123L)
                .itemCost(50.0)
                .itemType("CLOTHES")
                .build();

        Item item2 = Item.builder()
                .itemId(456L)
                .itemCost(300.0)
                .itemType("ELECTRONICS")
                .build();

        List<Item> lst = new ArrayList<Item>();
        lst.add(item1);
        lst.add(item2);

        given(discountRepository.findAll()).willReturn(List.of(discount, d1));

        // when
        Map<String, Double> bestDiscountMap = discountService.computeBestDiscount(lst);

        // then
        assertThat(bestDiscountMap).containsEntry("CDE", 305.0);
    }

    @Test
    public void givenDiscountObject_whenAddDiscount_thenReturnDiscountObject() {
        // given
        given(discountRepository.findDiscountByDiscountCode(discount.getDiscountCode())).willReturn(Optional.empty());

        given(discountRepository.save(discount)).willReturn(discount);

        // when
        Discount savedDiscount = discountService.addDiscount(discount);

        // then
        assertThat(savedDiscount).isNotNull();
    }

    @Test
    public void givenExistingDiscountCode_whenAddDiscount_thenThrowsException() {
        // given
        given(discountRepository.findDiscountByDiscountCode(discount.getDiscountCode())).willReturn(Optional.of(discount));

        // when
        assertThrows(DiscountAlreadyExistsException.class, () -> {
            discountService.addDiscount(discount);
        });

        // then
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    public void givenDiscountsList_whenFindAllDiscounts_thenReturnDiscountsList() {
        // given
        Discount d1 = Discount.builder()
                .id(2L)
                .discountCode("CDE")
                .discountPercent(10.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        given(discountRepository.findAll()).willReturn(List.of(discount, d1));

        // when
        List<Discount> discountList = discountService.findAllDiscounts();

        // then
        assertThat(discountList).isNotNull();
        assertThat(discountList.size()).isEqualTo(2);
    }

    @Test
    public void givenEmptyDiscountsList_whenFindAllDiscounts_thenReturnDiscountsList() {
        // given
        Discount d1 = Discount.builder()
                .id(2L)
                .discountCode("CDE")
                .discountPercent(10.0)
                .discountType("TOTAL_COST")
                .discountTypeValue("100")
                .build();

        given(discountRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<Discount> discountList = discountService.findAllDiscounts();

        // then
        assertThat(discountList).isEmpty();
        assertThat(discountList.size()).isEqualTo(0);
    }

    @Test
    public void givenDiscountCode_whenFindDiscountByDiscountCode_thenReturnDiscountObject() {
        // given
        given(discountRepository.findDiscountByDiscountCode("ABC")).willReturn(Optional.of(discount));

        // when
        Discount savedDiscount = discountService.findByDiscountCode(discount.getDiscountCode()).get();

        // then
        assertThat(savedDiscount).isNotNull();
    }

    @Test
    public void givenDiscountCode_whenRemoveDiscount_thenNothing() {
        // given
        String discountCode = "ABC";

        willDoNothing().given(discountRepository).removeDiscountByDiscountCode(discountCode);

        // when
        discountService.removeDiscount(discountCode);

        // then
        verify(discountRepository, times(1)).removeDiscountByDiscountCode(discountCode);
    }
}