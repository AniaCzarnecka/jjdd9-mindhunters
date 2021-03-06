package com.infoshareacademy.repository;

import com.infoshareacademy.domain.Drink;
import com.infoshareacademy.domain.Ingredient;
import com.infoshareacademy.service.DrinkService;
import org.hibernate.jpa.QueryHints;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional

@Stateless
public class DrinkRepositoryBean implements DrinkRepository {


    private static final Integer LIVE_SEARCH_LIMIT = 10;

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private DrinkService drinkService;

    @Override
    public Drink findDrinkById(Long drinkId) {

        return entityManager.find(Drink.class, drinkId);
    }

    @Override
    public void delete(Long id) {

        Drink drink = findDrinkById(id);
        if (drink != null) {
            entityManager.remove(drink);
        }

    }

    @Override
    public void update(Long id, Drink updatedDrink) {
        entityManager.detach(updatedDrink);
        entityManager.merge(updatedDrink);
    }

    @Override
    public List<Drink> findDrinksByName(String partialDrinkName, int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findDrinkByPartialName");
        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        query.setParameter("partialDrinkName", "%" + partialDrinkName + "%");
        return query.getResultList();
    }


    @Override
    public int countPagesByName(String partialDrinkName) {
        Query query = entityManager.createNamedQuery("Drink.countDrinksByPartialName");
        query.setParameter("partialDrinkName", "%" + partialDrinkName + "%");
        String querySize = query.getSingleResult().toString();

        int maxPageNumber = drinkService.getMaxPageNumber(querySize);

        return maxPageNumber;

    }

    @Override
    public List<Drink> liveSearchDrinksByName(String partialDrinkName) {
        Query query = entityManager.createNamedQuery("Drink.findDrinkByPartialName");
        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        query.setMaxResults(LIVE_SEARCH_LIMIT);
        query.setParameter("partialDrinkName", "%" + partialDrinkName + "%");
        return query.getResultList();
    }


    @Override
    public List<Drink> findByIngredients(List<Ingredient> ingredients, int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findByIngredients");
        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        query.setParameter("ingredients", ingredients);
        return query.getResultList();
    }

    @Override
    public int countPagesByIngredients(List<Ingredient> ingredients) {
        Query query = entityManager.createNamedQuery("Drink.countByIngredients");
        query.setParameter("ingredients", ingredients);
        String querySize = query.getSingleResult().toString();

        int maxPageNumber = drinkService.getMaxPageNumber(querySize);

        return maxPageNumber;
    }


    @Override
    public List<Drink> findAllDrinks(int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findAll");

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        return query.getResultList();

    }

    @Override
    public List<Drink> findAllDrinks() {
        Query query = entityManager.createNamedQuery("Drink.findAll");

        return query.getResultList();

    }

    @Override
    public int countPagesFindAll() {
        Query query = entityManager.createNamedQuery("Drink.countFindAll");

        String querySize = query.getSingleResult().toString();
        int maxPageNumber = drinkService.getMaxPageNumber(querySize);
        return maxPageNumber;

    }

    @Override
    public List<Drink> findByCategories(List<Long> category, int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findDrinksByCategories");

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public int countPagesByCategories(List<Long> category) {
        Query query = entityManager.createNamedQuery("Drink.CountDrinksByCategories");

        query.setParameter("category", category);
        String querySize = query.getSingleResult().toString();
        int maxPageNumber = drinkService.getMaxPageNumber(querySize);
        return maxPageNumber;
    }

    @Override
    public List<Drink> findByAlcoholStatus(List<String> alcoholStatus, int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findDrinksByAlcoholStatus");

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        query.setParameter("alcoholStatus", alcoholStatus);
        return query.getResultList();
    }

    public void save(Drink drink) {
        entityManager.persist(drink);
    }

    @Override
    public int countPagesByAlcoholStatus(List<String> alcoholStatus) {
        Query query = entityManager.createNamedQuery("Drink.countDrinksByAlcoholStatus");

        query.setParameter("alcoholStatus", alcoholStatus);
        String querySize = query.getSingleResult().toString();

        int maxPageNumber = drinkService.getMaxPageNumber(querySize);
        return maxPageNumber;

    }

    @Override
    public List<Drink> findByCategoriesAndAlcoholStatus(List<Long> category, List<String> alcoholStatus,
                                                        int startPosition, int endPosition) {
        Query query = entityManager.createNamedQuery("Drink.findByCategoriesAndAlcoholStatus");

        query.setFirstResult(startPosition);
        query.setMaxResults(endPosition);

        query.setParameter("alcoholStatus", alcoholStatus).setParameter("category", category);

        return query.getResultList();

    }

    @Override
    public int countPagesByCategoriesAndAlcoholStatus(List<Long> category, List<String> alcoholStatus) {
        Query query = entityManager.createNamedQuery("Drink.countDrinksByCategoriesAndAlcoholStatus");

        query.setParameter("alcoholStatus", alcoholStatus).setParameter("category", category);
        String querySize = query.getSingleResult().toString();

        int maxPageNumber = drinkService.getMaxPageNumber(querySize);
        return maxPageNumber;

    }

    @Override
    public List<Drink> findDrinksToApprove() {
        Query query = entityManager.createNamedQuery("Drink.getDrinksToApprove");

        return query.getResultList();
    }

    @Override
    public void deleteIngredientsFromDrink(Long drinkId){
        Query query = entityManager.createNamedQuery("Drink.deleteIngredientByDrink");
        query.setParameter("drinkId", drinkId).executeUpdate();

    }

}
