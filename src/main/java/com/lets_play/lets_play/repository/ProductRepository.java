package com.lets_play.lets_play.repository;

import com.lets_play.lets_play.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
    List<Product> findByNameContainingIgnoreCase(String name);
}
