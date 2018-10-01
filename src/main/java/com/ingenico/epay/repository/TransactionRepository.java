package com.ingenico.epay.repository;

import com.ingenico.epay.domain.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction , Long> {

}
