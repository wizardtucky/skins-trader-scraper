package com.application.model;

import com.application.enums.Trader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;
    public String name;
    public Float priceEu;
    public Trader trader;
    public int count;

    public String getName() {
        return name;
    }

    protected Item(String name, Float priceEu, Trader trader) {
        this.name = name;
        this.priceEu = priceEu;
        this.trader = trader;
        this.count = 1;
    }

    protected Item(String name, Float priceEu, int count, Trader trader) {
        this.name = name;
        this.priceEu = priceEu;
        this.trader = trader;
        this.count = count;
    }

    protected Item() {}
}
