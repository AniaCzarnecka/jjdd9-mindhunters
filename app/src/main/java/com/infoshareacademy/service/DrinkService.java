package com.infoshareacademy.service;

import com.infoshareacademy.domain.DrinkJson;
import com.infoshareacademy.domain.DrinksDatabase;
import com.infoshareacademy.domain.IngredientJson;
import com.infoshareacademy.menu.DisplayMenu;
import com.infoshareacademy.utilities.PropertiesUtilities;
import com.infoshareacademy.utilities.UserInput;
import com.infoshareacademy.utilities.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static com.infoshareacademy.service.Colours.ANSI_RED;
import static com.infoshareacademy.service.Colours.ANSI_RESET;

public class DrinkService {

    private static final Logger STDOUT = LoggerFactory.getLogger("CONSOLE_OUT");
    private static final String USER_MESSAGE = "Wrong input. Please choose number from the list.";
    private final UserInput userInput = new UserInput();
    private int maxExistingId = 0;

    public static void loadDrinkList() {
        DrinksDatabase database = DrinksDatabase.getINSTANCE();
        if (database.getDrinks().isEmpty()) {
            List<DrinkJson> drinkList = new ArrayList<>();
            String fileName = "AllDrinks.json";
            try {
                drinkList.addAll(JsonReader.jsonDrinkReader(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            database.addAllDrinks(drinkList);
        }
    }

    public void printAllDrinks(DrinksDatabase database) {
        PropertiesUtilities propertiesUtilities = new PropertiesUtilities();
        String orderby = propertiesUtilities.getProperty("orderby");
        Stream<DrinkJson> sorted = database.getDrinks().stream();
        switch (orderby) {
            case "asc":
                sorted = sorted.sorted(Comparator.comparing(DrinkJson::getDrinkName));

                break;
            case "desc":
                sorted = sorted.sorted(Comparator.comparing(DrinkJson::getDrinkName).reversed());

                break;
        }

        sorted.forEach(drink -> {
            STDOUT.info("\n{}\n *ID: {}, *Category: {}, {};", drink.getDrinkName().toUpperCase(),
                    drink.getDrinkId(), drink.getCategoryName(), drink.getAlcoholStatus());
            STDOUT.info("\n");
        });

    }

    public static void printSingleDrink(DrinkJson drink) {
        String alcoContColour;
        if (drink.getAlcoholStatus().equals("Alcoholic")) {
            AgeVerification ageVerification = new AgeVerification();
            if (!ageVerification.isAdultSessionActive()) {
                UserInput userInput = new UserInput();
                boolean userChoice = userInput.getYesOrNo("\nAre You 18yo? Y/N: ");
                if (userChoice) {
                    ageVerification.saveAdultSessionTimestamp();
                } else {
                    return;
                }
            }

            alcoContColour = ANSI_RED;
        } else {
            alcoContColour = ANSI_RESET;
        }
        STDOUT.info("\n" + Colours.ANSI_BACKGROUND_YELLOW_BLACK +
                StringUtils.center(drink.getDrinkName().toUpperCase(), 46, "-")
                + Colours.ANSI_RESET);
        STDOUT.info("\n                              Drink Id :" + drink.getDrinkId() +
                "\n  Category : " + drink.getCategoryName() + alcoContColour + "(" +
                drink.getAlcoholStatus() + ")" + ANSI_RESET + "\n");

        STDOUT.info("\n" + Colours.ANSI_BRIGHT_YELLOW +
                StringUtils.rightPad("Recipe :", 46, "=")
                + Colours.ANSI_RESET);

        String wrapText = WordUtils.wrap(drink.getRecipe(), 46);
        STDOUT.info("\n{}", wrapText + "\n");

        STDOUT.info(Colours.ANSI_BRIGHT_YELLOW +
                StringUtils.rightPad("=", 46, "=")
                + Colours.ANSI_RESET + "\n");

        STDOUT.info("\n" + Colours.ANSI_BRIGHT_YELLOW +
                StringUtils.rightPad("Ingredients :", 46, "=")
                + Colours.ANSI_RESET);

        List<IngredientJson> ingredients = drink.getIngredients();
        String emptySpaces = "                           ";
        for (int j = 0; j < ingredients.size(); j++) {
            String adjustedName = drink.getIngredients().get(j).getName() + emptySpaces;
            STDOUT.info("\n" + adjustedName.substring(0, 24) +
                    " : " + drink.getIngredients().get(j).getMeasure());
        }
        STDOUT.info("\n" + Colours.ANSI_BRIGHT_YELLOW +
                StringUtils.rightPad("=", 46, "=")
                + Colours.ANSI_RESET + "\n");

        PropertiesUtilities propertiesUtilities = new PropertiesUtilities();
        String dateFormat = propertiesUtilities.getProperty("date.format");

        STDOUT.info("\nPhoto link : " + "\n" + Colours.ANSI_BLUE + drink.getImageUrl() + ANSI_RESET +
                "\n" + "\n" + Colours.ANSI_BACKGROUND_YELLOW_BLACK + "          Last modification : " +
                drink.getModifiedDate().format(DateTimeFormatter.ofPattern(dateFormat)) +
                Colours.ANSI_RESET + "\n");
    }

    public List<Integer> getAllDrinkIdNumbers(DrinksDatabase database) {
        List<Integer> idNumbers = new ArrayList<>();
        database.getDrinks().forEach(drink -> idNumbers.add(Integer.parseInt(drink.getDrinkId())));
        return idNumbers;
    }

    public void printAllCategories(DrinksDatabase database) {
        List<String> categories = getAllCategories(database);
        int counter = 0;
        for (String category : categories) {
            counter++;
            STDOUT.info("[{}] {}\n", counter, category);
        }
    }

    public List<String> getAllCategories(DrinksDatabase database) {
        TreeSet<String> categories = new TreeSet<>();
        database.getDrinks().forEach(drink -> categories.add(drink.getCategoryName()));
        return List.copyOf(categories);
    }

    public void printAllAlcoholStatuses(DrinksDatabase database) {
        List<String> alcoholStatuses = getAlcoholStatuses(database);
        int counter = 0;
        for (String alcoholStatus : alcoholStatuses) {
            counter++;
            STDOUT.info("[{}], {}\n", counter, alcoholStatus);
        }
    }

    public List<String> getAlcoholStatuses(DrinksDatabase database) {
        TreeSet<String> alcoholStatuses = new TreeSet<>();
        database.getDrinks().forEach(drink -> alcoholStatuses.add(drink.getAlcoholStatus()));
        return List.copyOf(alcoholStatuses);
    }

    public void printDrinkIngrAndMeasures(DrinkJson drink) {
        drink.getIngredients().forEach(i -> STDOUT.info("Ingredient: {}, measure: {}\n", i.getName(), i.getMeasure()));
        STDOUT.info("\n");
    }

    public boolean removeDrink(String id) {
        DrinksDatabase database = DrinksDatabase.getINSTANCE();
        for (DrinkJson drink : database.getDrinks()) {
            if (drink.getDrinkId().trim().equalsIgnoreCase(id)) {
                database.getDrinks().remove(drink);
                return true;
            }
        }
        return false;
    }

    public void createDrink() {
        DrinkJson userDrink = new DrinkJson();
        userDrink.setDrinkId(generateUserDrinkId());

        Utilities.clearScreen();
        userDrink.setDrinkName(userInput.getUserStringInput("\nType name of drink: "));

        Utilities.clearScreen();
        setUserDrinkCategory(userDrink);

        Utilities.clearScreen();
        setUserDrinkAlcoholStatus(userDrink);

        Utilities.clearScreen();
        userDrink.setRecipe(userInput.getUserStringInput("\nType drink recipe: "));

        Utilities.clearScreen();
        userDrink.setImageUrl(userInput.getUserStringInput("\nType drink image URL: "));

        Utilities.clearScreen();
        userDrink.setIngredients(setUserDrinkIngredientAndMeasure(15));
        userDrink.setModifiedDate(LocalDateTime.now());

        DrinksDatabase.getINSTANCE().addDrink(userDrink);
    }

    private String generateUserDrinkId() {
        if (maxExistingId == 0) {
            maxExistingId = Collections.max(getAllDrinkIdNumbers(DrinksDatabase.getINSTANCE()));
        }
        maxExistingId++;
        return String.valueOf(maxExistingId);
    }

    private void setUserDrinkCategory(DrinkJson userDrink) {
        STDOUT.info("\nChoose category number:\n");
        printAllCategories(DrinksDatabase.getINSTANCE());
        int userChoice;
        do {
            userChoice = userInput.getUserNumericInput();
            if (userChoice > 0 && userChoice <= getAllCategories(DrinksDatabase.getINSTANCE()).size()) {
                break;
            }
            STDOUT.info("Wrong input.\n");
        } while (true);
        userDrink.setCategoryName(getAllCategories(DrinksDatabase.getINSTANCE()).get(userChoice - 1));
    }

    private void setUserDrinkAlcoholStatus(DrinkJson userDrink) {
        STDOUT.info("\nChoose alcohol status:\n");
        printAllAlcoholStatuses(DrinksDatabase.getINSTANCE());
        int userChoice;
        do {
            userChoice = userInput.getUserNumericInput();
            if (userChoice > 0 && userChoice <= getAlcoholStatuses(DrinksDatabase.getINSTANCE()).size()) {
                break;
            }
            STDOUT.info("Wrong input.\n");
        } while (true);
        userDrink.setAlcoholStatus((getAlcoholStatuses(DrinksDatabase.getINSTANCE()).get(userChoice - 1)));
    }

    private List<IngredientJson> setUserDrinkIngredientAndMeasure(int maxCapacity) {
        List<IngredientJson> ingredients = new ArrayList<>();
        String name;
        String measure;
        String choice;
        int counter = 1;
        do {
            name = userInput.getUserStringInput("\nnType ingredient no." + counter + " name: ");
            StringBuilder builder = new StringBuilder();
            String message =
                    builder.append("Type ").append("'").append(name).append("'").append(" measure: ").toString();
            measure = userInput.getUserStringInput(message);
            ingredients.add(new IngredientJson(name, measure));
            STDOUT.info("\n");
            counter++;
        } while (userInput.getYesOrNo("Do you want to add another ingredient [max 15], <y/n>?") && (ingredients.size() <= maxCapacity));
        return ingredients;
    }

    public boolean editDrink(String drinkId) {
        for (DrinkJson drink : DrinksDatabase.getINSTANCE().getDrinks()) {
            if (drink.getDrinkId().trim().equalsIgnoreCase(drinkId)) {
                editNavigation(drink);
                return true;
            }
        }
        return false;
    }

    private void editNavigation(DrinkJson editedDrink) {
        boolean cont = true;
        do {
            DisplayMenu.displayEditMenu();
            switch (userInput.getUserNumericInput()) {
                case 1:
                    Utilities.clearScreen();
                    STDOUT.info("Previous drink name: {}\n\n", editedDrink.getDrinkName());
                    editedDrink.setDrinkName(userInput.getUserStringInput("Type name of drink: "));
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 2:
                    Utilities.clearScreen();
                    STDOUT.info("Previous drink category: {}\n\n", editedDrink.getCategoryName());
                    setUserDrinkCategory(editedDrink);
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 3:
                    Utilities.clearScreen();
                    STDOUT.info("Previous drink alcohol status: {}\n\n", editedDrink.getAlcoholStatus());
                    setUserDrinkAlcoholStatus(editedDrink);
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 4:
                    Utilities.clearScreen();
                    STDOUT.info("Previous drink recipe: {}\n\n", editedDrink.getRecipe());
                    editedDrink.setRecipe(userInput.getUserStringInput("Type drink recipe: "));
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 5:
                    Utilities.clearScreen();
                    STDOUT.info("Previous drink image URL: {}\n\n", editedDrink.getImageUrl());
                    editedDrink.setImageUrl(userInput.getUserStringInput("Type drink image URL: "));
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 6:
                    Utilities.clearScreen();
                    printDrinkIngrAndMeasures(editedDrink);
                    editedDrink.setIngredients(setUserDrinkIngredientAndMeasure(15));
                    editedDrink.setModifiedDate(LocalDateTime.now());
                    break;
                case 7:
                    cont = false;
                    break;
                default:
                    STDOUT.info(USER_MESSAGE);
                    Utilities.freezeConsole();
                    break;
            }
        } while (cont);
    }
}




