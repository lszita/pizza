package com.example.pizza.repository;

import com.example.pizza.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "pizza", path = "pizza")
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    Optional<Pizza> findByName(String name);
    List<Pizza> findByPriceLessThanEqual(BigDecimal price);
}
