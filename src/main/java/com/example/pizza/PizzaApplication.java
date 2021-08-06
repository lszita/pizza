package com.example.pizza;

import com.example.pizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PizzaApplication {

	@Autowired
	PizzaService pizzaService;

	public static void main(String[] args) {
		SpringApplication.run(PizzaApplication.class, args);
	}

}
