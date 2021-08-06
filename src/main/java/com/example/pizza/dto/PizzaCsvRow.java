package com.example.pizza.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PizzaCsvRow {
    @CsvBindByName(column = "Pizzaname")
    String pizzaName;
    @CsvBindByName(column = "price")
    BigDecimal price;
    @CsvBindByName(column = "topping1")
    String topping1;
    @CsvBindByName(column = "topping2")
    String topping2;
    @CsvBindByName(column = "topping3")
    String topping3;
    @CsvBindByName(column = "topping4")
    String topping4;
    @CsvBindByName(column = "topping5")
    String topping5;
}
