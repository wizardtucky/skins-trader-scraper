package com.application.repository;

import com.application.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ItemRepository<T extends Item> extends CrudRepository<T, Long> {
}