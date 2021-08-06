package com.example.pizza.service;


import com.example.pizza.dto.PizzaCsvRow;
import com.example.pizza.entity.Pizza;
import com.example.pizza.exception.InvalidCsvException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvServiceTest {

    CsvService underTest = new CsvService();

    @Test
    public void mapPizzaCsvRowToPizzaTest() {
        PizzaCsvRow csvRow = new PizzaCsvRow("name",new BigDecimal("1337"),"t1","t2","t3","t4", "t5");
        Pizza actual = underTest.mapPizzaCsvRowToPizza(csvRow);
        assertEquals("name", actual.getName());
        assertEquals(new BigDecimal("1337"), actual.getPrice());
        assertEquals(5, actual.getToppings().size());
        assertThat(actual.getToppings(), hasItems("t1", "t2", "t3", "t4", "t5"));
    }

    @Test
    public void mapPizzaCsvRowToPizzaTest_repeated_toppings() {
        PizzaCsvRow csvRow = new PizzaCsvRow("name",new BigDecimal("1337"),"t1","t1","t1","t1", "t1");
        Pizza actual = underTest.mapPizzaCsvRowToPizza(csvRow);
        assertEquals("name", actual.getName());
        assertEquals(new BigDecimal("1337"), actual.getPrice());
        assertEquals(1, actual.getToppings().size());
        assertThat(actual.getToppings(), hasItems("t1"));
    }

    @Test
    public void mapPizzaCsvRowToPizzaTest_empty_values() {
        PizzaCsvRow csvRow = new PizzaCsvRow("",new BigDecimal("1337"),"","","","", "");
        Pizza actual = underTest.mapPizzaCsvRowToPizza(csvRow);
        assertEquals("", actual.getName());
        assertEquals(new BigDecimal("1337"), actual.getPrice());
        assertEquals(1, actual.getToppings().size());
        assertThat(actual.getToppings(), hasItems(""));
    }

    @Test
    public void getPizzaStreamTest() throws IOException {
        InputStream test = new FileInputStream(new ClassPathResource("csv/good.csv").getFile());
        Stream<Pizza> actual = underTest.getPizzaStream(test);

        List<Pizza> actualList = actual.collect(Collectors.toList());
        assertEquals(2, actualList.size());
        assertEquals("pizza1", actualList.get(0).getName());
        assertEquals(new BigDecimal("1337"), actualList.get(0).getPrice());
        assertEquals(5, actualList.get(0).getToppings().size());
        assertThat(actualList.get(0).getToppings(), hasItems("topping11","topping12","topping13","topping14","topping15"));
    }

    @Test
    public void getPizzaStreamTest_bad_column_names() throws IOException {
        InputStream test = new FileInputStream(new ClassPathResource("csv/bad_column_names.csv").getFile());
        assertThrows(InvalidCsvException.class, () -> underTest.getPizzaStream(test));

        InputStream test2 = new FileInputStream(new ClassPathResource("csv/more_than_5_toppings.csv").getFile());
        assertThrows(InvalidCsvException.class, () -> underTest.getPizzaStream(test2));
    }



}
