package com.infoshareacademy.mapper;

import com.infoshareacademy.domain.*;
import com.infoshareacademy.jsonSupport.CategoryJson;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class UploadDrinkMapper {

    @Inject
    UploadCategoryMapper uploadCategoryMapper;

    @Inject
    UploadIngredientMapper uploadIngredientMapper;

    public Drink toEntity(DrinkJson drinkJson, CategoryJson categoryJson) {

        Drink drink = new Drink();

        String categoryName = drinkJson.getCategoryName();

        drink.setDrinkId(drinkJson.getDrinkId());
        drink.setDrinkName(drinkJson.getDrinkName());

        drink.setAlcoholStatus(drinkJson.getAlcoholStatus());
        drink.setRecipe(drinkJson.getRecipe());
        drink.setImage(drinkJson.getImageUrl());
        drink.setDate(drinkJson.getModifiedDate());
        drink.setApproved(true);
        
        List<DrinkIngredient> drinkIngredients = new ArrayList<>();
        for (IngredientJson ingredientJson : drinkJson.getIngredients()) {
            DrinkIngredient drinkIngredient = uploadIngredientMapper.toEntity(ingredientJson, drink);
            drinkIngredient.setDrinkId(drink);
            drinkIngredients.add(drinkIngredient);
        }
        drink.setDrinkIngredients(drinkIngredients);
        categoryJson.setCategoryName(drinkJson.getCategoryName());
        Category category = uploadCategoryMapper.toEntity(categoryJson);
        drink.setCategory(category);

        return drink;
    }
}
