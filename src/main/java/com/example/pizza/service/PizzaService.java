package com.example.pizza.service;

import com.example.pizza.entity.Pizza;
import com.example.pizza.repository.PizzaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class PizzaService {

    PizzaRepository pizzaRepository;
    CsvService csvService;
    Validator validator;

    public void processBatch(InputStream file) {
        csvService
            .getPizzaStream(file)
            .filter(this::isValidPizza)
            .forEach(this::savePizza);
    }

    public List<String> pizzaDeals(BigDecimal availableFunds) {
        List<Pizza> pizzas = pizzaRepository.findByPriceLessThanEqual(availableFunds);
        pizzas.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()) * -1);

        List<String> deals = new ArrayList<>();
        for(Pizza p : pizzas) {
            while (BigDecimal.ZERO.compareTo(availableFunds.subtract(p.getPrice())) <= 0) {
                availableFunds = availableFunds.subtract(p.getPrice());
                deals.add(p.getName());
            }
        }
        return deals;
    }


    void savePizza(Pizza pizza) {
        Optional<Pizza> optionalPizzaInDB = pizzaRepository.findByName(pizza.getName());
        if (optionalPizzaInDB.isPresent()) {
            Pizza pizzaInDB = optionalPizzaInDB.get();
            pizzaInDB.setPrice(pizza.getPrice());
            //It would be logical to update the toppings as well at this point, but the task does not mention it
            // "If a certain type of pizza is already in the application, the price will be updated.
        } else {
            pizzaRepository.save(pizza);
        }
    }

    boolean isValidPizza(Pizza pizza) {
        Set<ConstraintViolation<Pizza>> violations = validator.validate(pizza);
        if (violations.isEmpty()) {
            return true;
        } else {
            violations.forEach(e -> log.warn(e.getMessage()));
            return false;
        }
    }

}
