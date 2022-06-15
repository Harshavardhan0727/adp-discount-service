package com.adp.discountservice.adpdiscountservice.controller;

import com.adp.discountservice.adpdiscountservice.model.Discount;
import com.adp.discountservice.adpdiscountservice.model.Item;
import com.adp.discountservice.adpdiscountservice.service.DiscountService;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenItemList_whenComputeBestDiscount_thenReturnBestDiscountMap() throws Exception {
        // given
        Discount discount1 = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();

        Discount discount2 = Discount.builder()
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
        Map<String, Double> m1 = new HashMap<>();
        m1.put("ABC", 45.0);

        given(discountService.computeBestDiscount(lst)).willReturn(m1);

        // when
        ResultActions response = mockMvc.perform(post("/api/v1/discounts/best-discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lst)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.ABC").value("45.0"));
    }

    @Test
    public void givenDiscountObject_whenAddDiscount_thenReturnSavedDiscount() throws Exception {
        // given
        Discount discount = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();

        given(discountService.addDiscount(any(Discount.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(post("/api/v1/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discount)));

        // then
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.discountCode",
                        is(discount.getDiscountCode())))
                .andExpect(jsonPath("$.discountPercent",
                        is(discount.getDiscountPercent())))
                .andExpect(jsonPath("$.discountType",
                        is(discount.getDiscountType())))
                .andExpect(jsonPath("$.discountTypeValue",
                        is(discount.getDiscountTypeValue())));
    }

    @Test
    public void givenListOfDiscounts_whenGetAllAvailableDiscounts_thenReturnDiscountsList() throws Exception {
        // given
        List<Discount> listOfDiscounts = new ArrayList<>();
        listOfDiscounts.add(Discount.builder().discountCode("ABC").discountPercent(10.0).discountType("ITEM_TYPE").discountTypeValue("CLOTHES").build());
        listOfDiscounts.add(Discount.builder().discountCode("CDE").discountPercent(15.0).discountType("TOTAL_COST").discountTypeValue("100").build());

        given(discountService.findAllDiscounts()).willReturn(listOfDiscounts);

        // when
        ResultActions response = mockMvc.perform(get("/api/v1/discounts"));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfDiscounts.size())));
    }

    @Test
    public void givenDiscountCode_whenFindDiscountByCode_thenReturnDiscountObject() throws Exception {
        // given
        String discountCode = "ABC";
        Discount discount = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();

        given(discountService.findByDiscountCode(discountCode)).willReturn(Optional.of(discount));

        // when
        ResultActions response = mockMvc.perform(get("/api/v1/discounts/{discountCode}", discountCode));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.discountCode", is(discount.getDiscountCode())))
                .andExpect(jsonPath("$.discountPercent", is(discount.getDiscountPercent())))
                .andExpect(jsonPath("$.discountType", is(discount.getDiscountType())))
                .andExpect(jsonPath("$.discountTypeValue", is(discount.getDiscountTypeValue())));
    }

    @Test
    public void givenInvalidDiscountCode_whenFindDiscountByCode_thenReturnEmpty() throws Exception {
        // given
        String discountCode = "ABC";
        Discount discount = Discount.builder()
                .discountCode("ABC")
                .discountPercent(10.0)
                .discountType("ITEM_TYPE")
                .discountTypeValue("CLOTHES")
                .build();

        given(discountService.findByDiscountCode(discountCode)).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/api/v1/discounts/{discountCode}", discountCode));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void givenDiscountCode_whenRemoveDiscount_thenReturn204() throws Exception {
        // given
        String discountCode = "ABC";
        willDoNothing().given(discountService).removeDiscount(discountCode);

        // when
        ResultActions response = mockMvc.perform(delete("/api/v1/discounts/{discountCode}", discountCode));

        // then
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}