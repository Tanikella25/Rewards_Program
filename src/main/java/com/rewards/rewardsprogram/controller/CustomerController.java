package com.rewards.rewardsprogram.controller;

import com.rewards.rewardsprogram.exception.UserNotFoundException;
import com.rewards.rewardsprogram.entity.Customer;
import com.rewards.rewardsprogram.entity.Transaction;
import com.rewards.rewardsprogram.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    CustomerService customerService(CustomerService customerService) {
        this.customerService = customerService;
        return customerService;
    }

    @PostMapping("/addCustomers")
    public ResponseEntity<List<Customer>> addCustomers(@RequestBody List<Customer> customers) {
        return new ResponseEntity<>(customerService.saveCustomers(customers), HttpStatus.CREATED);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> findAllCustomers() {
        return new ResponseEntity<>(customerService.getCustomers(), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> findAllTransactions() {
        return new ResponseEntity<>(customerService.getTransactions(), HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Customer> findProductById(@PathVariable int customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer != null) {
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateCustomer/{customerId}")
    public ResponseEntity<Customer> updatePoints(@PathVariable int customerId, @RequestParam("transactionAmount") double transactionAmount) throws UserNotFoundException {
        Customer updatedCustomer = customerService.updatePoints(customerId, transactionAmount);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @PutMapping("/updateCustomer/{customerId}/points")
    public ResponseEntity<Customer> updatePointsByDate(@PathVariable int customerId,
                                                       @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws UserNotFoundException {
        double calculatedPoints = customerService.calculatePointsForDateRange(startDate, endDate, customerId);
        Customer updatedCustomer = customerService.updateOldPoints(customerId, calculatedPoints);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteProduct(@PathVariable int customerId) {
        String message = customerService.deleteById(customerId);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

}
