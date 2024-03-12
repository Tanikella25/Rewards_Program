package com.rewards.rewardsprogram.service;


import com.rewards.rewardsprogram.exception.UserNotFoundException;
import com.rewards.rewardsprogram.entity.Customer;
import com.rewards.rewardsprogram.entity.Transaction;
import com.rewards.rewardsprogram.repository.CustomerRepository;
import com.rewards.rewardsprogram.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        return customerRepository;
    }

    @Autowired
    private TransactionRepository transactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        return transactionRepository;
    }

    public List<Customer> saveCustomers(List<Customer> customers) {
        return customerRepository.saveAll(customers);
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer updatePoints(int customerId, double transactionAmount) throws UserNotFoundException {
        Customer existingCustomer = customerRepository.findById(customerId).orElse(null);
        if (existingCustomer != null) {
            existingCustomer.setId(existingCustomer.getId());
            existingCustomer.setPoints(getPoints(customerId) + calculatePoints(transactionAmount));
            return customerRepository.save(existingCustomer);
        } else throw new UserNotFoundException("Customer Not Found: " + customerId);
    }

    public Customer updateOldPoints(int customerId, double calculatedPoints) throws UserNotFoundException {
        Customer existingCustomer = customerRepository.findById(customerId).orElse(null);
        if (existingCustomer != null) {
            existingCustomer.setId(existingCustomer.getId());
            existingCustomer.setPoints(getPoints(customerId) + calculatedPoints);
            return customerRepository.save(existingCustomer);
        } else throw new UserNotFoundException("Customer Not Found: " + customerId);
    }

    public String deleteById(int customerId) {
        customerRepository.deleteById(customerId);
        return "Customer No Longer in RewardsProgram: " + customerId;
    }

    public int calculatePoints(double transactionAmount) {
        int points = 0;
        double amountOver100 = Math.max(transactionAmount - 100, 0);
        double amountBetween50And100 = Math.min(Math.max(transactionAmount - 50, 0), 50);

        points += (int) (amountOver100 * 2); // 2 points for every dollar over $100
        points += (int) amountBetween50And100; // 1 point for every dollar between $50 and $100

        return points;
    }

    public double getPoints(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        double points = customer.getPoints();
        return points;
    }

    public int calculatePointsForDateRange(LocalDate startDate, LocalDate endDate, int customerId) throws UserNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            List<Transaction> transactions = transactionRepository.findByTransactionDateBetweenAndCustomerId(startDate, endDate, customer.getId());
            return transactions.stream()
                    .mapToInt(transaction -> {
                        double amount = transaction.getTransactionAmount();
                        if (amount > 100) {
                            return (int) ((amount - 100) * 2 + 50);
                        } else if (amount > 50) {
                            return (int) (amount - 50);
                        }
                        return 0;
                    }).sum();
        } else throw new UserNotFoundException("Customer Not Found: " + customerId);
    }
}
