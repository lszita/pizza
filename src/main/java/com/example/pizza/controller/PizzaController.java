package com.example.pizza.controller;

import com.example.pizza.dto.CsvResponse;
import com.example.pizza.dto.DealRequest;
import com.example.pizza.exception.CsvServiceException;
import com.example.pizza.exception.InvalidCsvException;
import com.example.pizza.service.PizzaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("pizza")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class PizzaController {

    PizzaService pizzaService;

    @PostMapping("batch")
    public ResponseEntity<CsvResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            pizzaService.processBatch(file.getInputStream());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidCsvException e) {
            return new ResponseEntity<>(new CsvResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IOException | CsvServiceException e) {
            log.error("Error in batch process" ,e);
            return new ResponseEntity<>(new CsvResponse("An unexpected error has occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("deals")
    public List<String> deal(@RequestBody DealRequest req) {
        return pizzaService.pizzaDeals(req.getFunds());
    }
}
