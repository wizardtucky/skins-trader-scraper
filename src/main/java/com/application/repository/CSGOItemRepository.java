package com.application.repository;

import com.application.model.CSGOItem;
import com.application.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CSGOItemRepository extends ItemRepository<CSGOItem>{
    Iterable<CSGOItem> findCSGOItemsByPriceEuGreaterThan(Float priceEu);
}