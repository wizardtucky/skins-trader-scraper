package com.application.model;

import com.application.enums.Trader;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class RustItem extends Item {
    public RustItem(String name, Float priceEu, int count, Trader trader) {
        super(name, priceEu, count, trader);
    }

    public RustItem() {
        super();
    }
}
