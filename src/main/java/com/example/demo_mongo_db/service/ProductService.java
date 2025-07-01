package com.example.demo_mongo_db.service;

import com.example.demo_mongo_db.document.Product;
import com.example.demo_mongo_db.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 모든 상품 가져오기
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID로 상품 가져오기
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // 상품 등록
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateProduct(String id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setStock(updatedProduct.getStock());
                    return productRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("[에러] 상품을 찾을 수 없습니다."));
    }

    // 상품 삭제
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}