package com.lets_play.lets_play.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lets_play.lets_play.entity.Product;
import com.lets_play.lets_play.exception.ResourceNotFoundException;
import com.lets_play.lets_play.exception.UnauthorizedException;
import com.lets_play.lets_play.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getProductsByUserId(String userId) {
        return productRepository.findByUserId(userId);
    }

    public Product updateProduct(String id, Product productRequest, String currentUserId, String userRole) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Only owner or admin can update
        if (!product.getUserId().equals(currentUserId) && !"ADMIN".equals(userRole)) {
            throw new UnauthorizedException("You don't have permission to update this product");
        }

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());

        return productRepository.save(product);
    }

    public void deleteProduct(String id, String currentUserId, String userRole) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Only owner or admin can delete
        if (!product.getUserId().equals(currentUserId) && !"ADMIN".equals(userRole)) {
            throw new UnauthorizedException("You don't have permission to delete this product");
        }

        productRepository.deleteById(id);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
