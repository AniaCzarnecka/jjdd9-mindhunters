package com.infoshareacademy.service;

import com.infoshareacademy.domain.Drink;
import com.infoshareacademy.domain.User;
import com.infoshareacademy.domain.dto.FullDrinkView;
import com.infoshareacademy.repository.DrinkRepository;
import com.infoshareacademy.repository.UserRepository;
import com.infoshareacademy.service.mapper.FullDrinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;


@Stateless
public class UserService {

    private static final Logger packageLogger = LoggerFactory.getLogger(UserService.class.getName());
    private static final Integer PAGE_SIZE = 5;

    @EJB
    private UserRepository userRepository;

    @EJB
    private DrinkRepository drinkRepository;

    @Inject
    private FullDrinkMapper fullDrinkMapper;


    public void saveOrDeleteFavourite(String userId, String drinkId) {

        Drink drink = drinkRepository.findDrinkById(Long.parseLong(drinkId));
        User user = userRepository.findUserById(Long.parseLong(userId));

        List<Drink> favouriteDrinks = user.getDrinks();

        if (!favouriteDrinks.contains(drink)) {
            user.getDrinks().add(drink);
            packageLogger.info("User ID = {} added drink ID = {} to favourites", userId, drinkId);

        } else {
            user.getDrinks().remove(drink);
            packageLogger.info("User ID = {} deleted drink ID = {} from favourites", userId, drinkId);

        }

    }

    public List<FullDrinkView> favouritesList(String userId) {

        User user = userRepository.findUserById(Long.parseLong(userId));

        return fullDrinkMapper.toView(user.getDrinks());

    }

    public List<FullDrinkView> favouritesList(String userId, int pageNumber) {
        int startPosition = (pageNumber - 1) * PAGE_SIZE;
        int endPosition = PAGE_SIZE;

        List<Drink> drinks = userRepository.findFavouritesList(Long.valueOf(userId), startPosition, endPosition);

        return fullDrinkMapper.toView(drinks);

    }

    public int countPagesFavouritesList(String userId) {
        int maxPageNumber = userRepository.countPagesFindFavouritesList(Long.valueOf(userId));

        return maxPageNumber;

    }

    public static int getMaxPageNumber(String querySize) {
        return (int) Math.ceil((Double.valueOf(querySize) / PAGE_SIZE));
    }


}
