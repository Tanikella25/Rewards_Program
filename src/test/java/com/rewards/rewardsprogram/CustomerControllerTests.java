package com.rewards.rewardsprogram;

import com.rewards.rewardsprogram.exception.UserNotFoundException;
import com.rewards.rewardsprogram.controller.CustomerController;
import com.rewards.rewardsprogram.entity.Customer;
import com.rewards.rewardsprogram.entity.Transaction;
import com.rewards.rewardsprogram.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;


@SpringBootTest
public class CustomerControllerTests {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;
    private Transaction testTransaction;

    @BeforeEach
    public void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("Customer");
        testCustomer.setPoints(90);

        testTransaction = new Transaction();
        testTransaction.setTransactionId(1);
        testTransaction.setTransactionAmount(120);
    }

    @Test
    public void testAddCustomers() {
        List<Customer> customers = Arrays.asList(testCustomer);

        Mockito.when(customerService.saveCustomers(customers)).thenReturn(customers);

        ResponseEntity<List<Customer>> responseEntity = customerController.addCustomers(customers);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertEquals(customers, responseEntity.getBody());
    }

    @Test
    public void testFindAllCustomers() {
        List<Customer> customers = Arrays.asList(testCustomer);

        Mockito.when(customerService.getCustomers()).thenReturn(customers);

        ResponseEntity<List<Customer>> responseEntity = customerController.findAllCustomers();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(customers, responseEntity.getBody());
    }

    @Test
    public void testFindAllTransactions() {
        List<Transaction> transactions = Arrays.asList(testTransaction);

        Mockito.when(customerService.getTransactions()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> responseEntity = customerController.findAllTransactions();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(transactions, responseEntity.getBody());
    }

    @Test
    public void testFindCustomerById() {
        Mockito.when(customerService.getCustomerById(1)).thenReturn(testCustomer);

        ResponseEntity<Customer> responseEntity = customerController.findProductById(1);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(testCustomer, responseEntity.getBody());
    }

    @Test
    public void testUpdatePoints() throws UserNotFoundException {
        Mockito.when(customerService.updatePoints(1, 120)).thenReturn(testCustomer);

        ResponseEntity<Customer> responseEntity = customerController.updatePoints(1, 120);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(testCustomer, responseEntity.getBody());
    }

    @Test
    public void testUpdatePointsByDate() throws UserNotFoundException {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        Mockito.when(customerService.calculatePointsForDateRange(startDate, endDate, 1)).thenReturn(90);
        Mockito.when(customerService.updateOldPoints(1, 90)).thenReturn(testCustomer);

        ResponseEntity<Customer> responseEntity = customerController.updatePointsByDate(1, startDate, endDate);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(testCustomer, responseEntity.getBody());
    }

    @Test
    public void testDeleteProduct() {
        String message = "Customer No Longer in Rewards Program: 1";

        Mockito.when(customerService.deleteById(1)).thenReturn(message);

        ResponseEntity<String> responseEntity = customerController.deleteProduct(1);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(message, responseEntity.getBody());
    }
}
