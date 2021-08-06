package com.example.pizza.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pizza {
    @Id
    @GeneratedValue
    Long id;
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotNull(message = "Price is mandatory")
    BigDecimal price;
    @ElementCollection
    @Size(min=1, max=5, message = "Number of pizza toppings must be between 1 and 5")
    Set<String> toppings = new HashSet<>();
}
