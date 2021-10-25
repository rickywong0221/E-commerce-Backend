package com.fsse.ecommerce.service;

import com.fsse.ecommerce.domain.CartItem;
import com.fsse.ecommerce.domain.Product;
import com.fsse.ecommerce.domain.entity.ProductEntity;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductByPid(Long id);
    ProductEntity getProductEntityByPid(Long pid);
}
