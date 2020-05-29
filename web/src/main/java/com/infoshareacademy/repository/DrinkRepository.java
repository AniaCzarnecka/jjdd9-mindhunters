package com.infoshareacademy.repository;

import com.infoshareacademy.domain.Drink;
import com.infoshareacademy.domain.Ingredient;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DrinkRepository {

    Drink findDrinkById(Long id);

    List<Drink> findDrinksByName(String partialDrinkName);

    List<Drink> findDrinkByIngredients(List<Ingredient> ingredients);

}