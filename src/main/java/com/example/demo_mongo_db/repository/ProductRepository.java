package com.example.demo_mongo_db.repository;

import com.example.demo_mongo_db.document.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByPriceGreaterThan(int price);
}
