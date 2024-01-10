package com.application.model;

import javax.persistence.*;

@Entity
public class CSGOItemPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;

    @ManyToOne
    @JoinColumn(name="csgoItemId", nullable=false)
    private CSGOItem csgoItem;

    public String name;
    public float average;
    public float median;
    public int sold;
    public float deviation;
    public float lowestPrice;
    public float highestPrice;

    public CSGOItemPriceHistory(String name, float average, float median, int sold, float deviation, float lowestPrice, float highestPrice) {
        this.name = name;
        this.average = average;
        this.median = median;
        this.sold = sold;
        this.deviation = deviation;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }

    public CSGOItemPriceHistory() {
    }

    public String getName() {
        return name;
    }
}
