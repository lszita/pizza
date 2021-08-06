package com.example.pizza.service;

import com.example.pizza.entity.Pizza;
import com.example.pizza.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock
    PizzaRepository pizzaRepository;

    @Mock
    CsvService csvService;

    static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    PizzaService underTest;

    @BeforeEach
    public void init() {
        Validator validator = factory.getValidator();
        underTest = new PizzaService(pizzaRepository, csvService, validator);
    }

    @Test
    public void isValidPizzaTest() {
        Pizza p1Valid = new Pizza(null, "a", BigDecimal.ONE, Set.of("a","b"));
        assertTrue(underTest.isValidPizza(p1Valid));

        Pizza pInvalid = new Pizza(null, "a", BigDecimal.ONE, Collections.emptySet());
        assertFalse(underTest.isValidPizza(pInvalid));
        //could test a ton more cases...
    }

    @Test
    public void savePizzaTest_when_found() {
        Pizza p1 = new Pizza();
        p1.setName("p1");
        when(pizzaRepository.findByName(eq("p1"))).thenReturn(Optional.of(p1));

        underTest.savePizza(p1);

        verify(pizzaRepository, times(1)).findByName(eq("p1"));
        verify(pizzaRepository, times(0)).save(any());
    }

    @Test
    public void savePizzaTest_when_not_found() {
        Pizza p1 = new Pizza();
        p1.setName("p1");
        when(pizzaRepository.findByName(eq("p1"))).thenReturn(Optional.empty());

        underTest.savePizza(p1);

        verify(pizzaRepository, times(1)).findByName(eq("p1"));
        verify(pizzaRepository, times(1)).save(p1);
    }

    @Test
    public void processBatchTest() throws IOException {
        InputStream stubInputStream = new ByteArrayInputStream("test data".getBytes());
        Pizza p1 = new Pizza(null,"p1", BigDecimal.ONE, Collections.emptySet());

        when(csvService.getPizzaStream(any())).thenReturn(Stream.of(p1));

        underTest.processBatch(stubInputStream);
        verify(pizzaRepository, times(0)).save(p1);
    }

    @Test
    public void pizzaDealsTest_exact_match() {
        List<Pizza> pizzas = Arrays.asList(
            new Pizza(null, "p1", new BigDecimal("10"), Collections.emptySet()),
            new Pizza(null, "p2", new BigDecimal("30"), Collections.emptySet()),
            new Pizza(null, "p3", new BigDecimal("5"), Collections.emptySet())
        );

        when(pizzaRepository.findByPriceLessThanEqual(any())).thenReturn(pizzas);
        List<String> result = underTest.pizzaDeals(new BigDecimal("100"));

        assertEquals(4, result.size());
        assertThat(result , hasItems("p2", "p2", "p2", "p1"));
    }

    @Test
    public void pizzaDealsTest_not_exact_match() {
        List<Pizza> pizzas = Arrays.asList(
                new Pizza(null, "p1", new BigDecimal("10"), Collections.emptySet()),
                new Pizza(null, "p2", new BigDecimal("30"), Collections.emptySet()),
                new Pizza(null, "p3", new BigDecimal("5"), Collections.emptySet())
        );

        when(pizzaRepository.findByPriceLessThanEqual(any())).thenReturn(pizzas);
        List<String> result = underTest.pizzaDeals(new BigDecimal("103"));

        assertEquals(4, result.size());
        assertThat(result , hasItems("p2", "p2", "p2", "p1"));
    }


    @Test
    public void pizzaDealsTest_not_enough_funds() {
        List<Pizza> pizzas = Arrays.asList(
                new Pizza(null, "p1", new BigDecimal("10"), Collections.emptySet()),
                new Pizza(null, "p2", new BigDecimal("30"), Collections.emptySet()),
                new Pizza(null, "p3", new BigDecimal("5"), Collections.emptySet())
        );

        when(pizzaRepository.findByPriceLessThanEqual(any())).thenReturn(pizzas);
        List<String> result = underTest.pizzaDeals(new BigDecimal("0"));

        assertEquals(0, result.size());
    }

}