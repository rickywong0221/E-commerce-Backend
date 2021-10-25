package com.fsse.ecommerce.repository;

import com.fsse.ecommerce.domain.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
}
