package com.lets_play.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.PermitAll;

import com.lets_play.lets_play.entity.Product;
import com.lets_play.lets_play.security.UserDetailsImpl;
import com.lets_play.lets_play.service.ProductService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @PermitAll
    public ResponseEntity<List<Product>> getAllProducts() {
        // This endpoint is PUBLIC as per requirements
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        product.setUserId(userDetails.getId());

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        // This endpoint is PUBLIC as per requirements
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody Product productRequest,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        Product updatedProduct = productService.updateProduct(id, productRequest, currentUserId, userRole);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteProduct(@PathVariable String id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        productService.deleteProduct(id, currentUserId, userRole);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PermitAll
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        // This endpoint is PUBLIC as per requirements
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#userId, authentication.name)")
    public ResponseEntity<List<Product>> getProductsByUserId(@PathVariable String userId) {
        List<Product> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }
}
