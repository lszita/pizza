package com.example.pizza.service;

import com.example.pizza.dto.PizzaCsvRow;
import com.example.pizza.entity.Pizza;
import com.example.pizza.exception.CsvServiceException;
import com.example.pizza.exception.InvalidCsvException;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class CsvService {

    private static final String EXPECTED_HEADER = "Pizzaname,price,topping1,topping2,topping3,topping4,topping5";

    public Stream<Pizza> getPizzaStream(InputStream file) {

        InputStreamReader isr = new InputStreamReader(file);
        BufferedReader br = new BufferedReader(isr);

        try {
            br.mark(EXPECTED_HEADER.length());
            if (!EXPECTED_HEADER.equals(br.readLine())) {
                br.close();
                isr.close();
                file.close();
                throw new InvalidCsvException("CSV headers are invalid, expected format is: " + EXPECTED_HEADER);
            } else {
                br.reset();
            }
        } catch (IOException e) {
            throw new CsvServiceException("Error occurred during validation of the file", e);
        }

        return new CsvToBeanBuilder<PizzaCsvRow>(br)
                .withIgnoreEmptyLine(true)
                .withType(PizzaCsvRow.class)
                .build()
                .stream()
                .map(this::mapPizzaCsvRowToPizza);
    }

    Pizza mapPizzaCsvRowToPizza(PizzaCsvRow row) {
        Pizza pizza = new Pizza();
        pizza.setName(row.getPizzaName());
        pizza.setPrice(row.getPrice());
        Set<String> toppings = new HashSet<>();
        addToSetIfNotNull(row.getTopping1(), toppings);
        addToSetIfNotNull(row.getTopping2(), toppings);
        addToSetIfNotNull(row.getTopping3(), toppings);
        addToSetIfNotNull(row.getTopping4(), toppings);
        addToSetIfNotNull(row.getTopping5(), toppings);
        pizza.setToppings(toppings);
        return pizza;
    }

    private void addToSetIfNotNull(String e, Set<String> s) {
        if (!Objects.isNull(e)) {
            s.add(e);
        }
    }
}
