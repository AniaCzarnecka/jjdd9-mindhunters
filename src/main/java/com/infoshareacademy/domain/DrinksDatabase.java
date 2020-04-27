package com.infoshareacademy.domain;

import java.util.ArrayList;
import java.util.List;

public final class DrinksDatabase {

    private static DrinksDatabase INSTANCE;
    private String info = "Initial info class";
    private List<Drink> drinks;

    private DrinksDatabase() {
        drinks = new ArrayList<>();
    }

    public static DrinksDatabase getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DrinksDatabase();
        }
        return INSTANCE;
    }

    public void addDrinks(List<Drink> drinkRecipes) {
        drinks.addAll(drinkRecipes);
    }

    public List<Drink> getDrinks() {

        return this.drinks;
    }
}