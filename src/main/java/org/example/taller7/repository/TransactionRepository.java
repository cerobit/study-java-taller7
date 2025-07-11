package org.example.taller7.repository;

import org.example.taller7.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

}
