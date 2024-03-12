package com.rewards.rewardsprogram;

import com.rewards.rewardsprogram.exception.UserNotFoundException;
import com.rewards.rewardsprogram.entity.Customer;
import com.rewards.rewardsprogram.entity.Transaction;
import com.rewards.rewardsprogram.repository.CustomerRepository;
import com.rewards.rewardsprogram.repository.TransactionRepository;
import com.rewards.rewardsprogram.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;


@SpringBootTest
public class CustomerServiceTests {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private Transaction testTransaction;

    @BeforeEach
    public void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("Customer");
        testCustomer.setPoints(0);

        testTransaction = new Transaction();
        testTransaction.setTransactionId(1);
        testTransaction.setCustomerId(1);
        testTransaction.setTransactionAmount(120);
    }

    @Test
    public void testGetCustomerById() {
        org.mockito.Mockito.when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));

        Customer foundCustomer = customerService.getCustomerById(1);

        Assertions.assertEquals(testCustomer, foundCustomer);
    }

    @Test
    public void testUpdatePoints() throws UserNotFoundException {
        org.mockito.Mockito.when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        org.mockito.Mockito.when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        Customer updatedCustomer = customerService.updatePoints(1, 120);

        Assertions.assertEquals(90, updatedCustomer.getPoints());
    }

    @Test
    public void testUpdatePointsWithNonExistingCustomer() {
        org.mockito.Mockito.when(customerRepository.findById(1)).thenReturn(Optional.empty());
        try {
            Customer updatedCustomer = customerService.updatePoints(1, 120);
            Assertions.assertNull(updatedCustomer);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testCalculatePoints() {
        int points = customerService.calculatePoints(120);

        Assertions.assertEquals(90, points);
    }

    @Test
    public void testCalculatePointsForDateRange() throws UserNotFoundException {
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();
        testTransaction.setTransactionDate(LocalDate.now().minusDays(1));

        org.mockito.Mockito.when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        org.mockito.Mockito.when(transactionRepository.findByTransactionDateBetweenAndCustomerId(
                        startDate, endDate, 1))
                .thenReturn(Collections.singletonList(testTransaction));

        int points = customerService.calculatePointsForDateRange(startDate, endDate, 1);

        Assertions.assertEquals(90, points);
    }

    @Test
    public void testCalculatePointsForDateRangeNoTransactions() throws UserNotFoundException {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        org.mockito.Mockito.when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        org.mockito.Mockito.when(transactionRepository.findByTransactionDateBetweenAndCustomerId(
                        startDate, endDate, 1))
                .thenReturn(Collections.emptyList());

        int points = customerService.calculatePointsForDateRange(startDate, endDate, 1);

        Assertions.assertEquals(0, points);
    }
}
