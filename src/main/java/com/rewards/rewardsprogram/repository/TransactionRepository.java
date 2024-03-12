package com.rewards.rewardsprogram.repository;


import com.rewards.rewardsprogram.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByTransactionDateBetweenAndCustomerId(LocalDate startDate, LocalDate endDate, int customerId);
}
