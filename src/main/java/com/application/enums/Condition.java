package com.application.enums;

public enum Condition {
    FACTORYNEW("Factory New"),
    MINIMALWEAR("Minimal Wear"),
    FIELDTESTED("Field-Tested"),
    WELLWORN("Well-Worn"),
    BATTLESCARED("Battle-Scarred"),
    NONE("None");

    public final String label;

    Condition(String label) {
        this.label = label;
    }
}


