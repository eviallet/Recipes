package com.gueg.recipes;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The class source.Recipe describe a recipe.
 */
public class Recipe {
    /**
     * Name of the source.Recipe
     */
    private String name;
    /**
     * URL link of the source.Recipe extracted from <a href="http://marmiton.org">http://marmiton.org</a>
     */
    private String urlLink;
    /**
     * HTML page of the source.Recipe
     */
    private Document page;
    /**
     * HTML code that contains informations about the source.Recipe
     */
    private Element pageContent;

    /**
     * Preparation time of the source.Recipe
     */
    private String prepTime;
    /**
     * Cooking time of the source.Recipe
     */
    private String cookingTime;
    /**
     * Details about the source.Recipe : instructions about how to make this source.Recipe
     */
    private ArrayList<String> details;
    /**
     * Ingredients needed to cook the source.Recipe
     */
    private ArrayList<Ingredient> ingredients;
    /**
     * Number of person for whom the recipe is made.
     */
    private int people;

    private String imageUrl;


    /**
     * Simple constructor of source.Recipe.
     *
     * @param name    Name of the source.Recipe.
     * @param urlLink Url link of the source.Recipe.
     */
    public Recipe(String name, String urlLink) {

        this.name = name;
        this.urlLink = urlLink;


    }

    /**
     * Method that send a query to marmiton.org and load the informations about the source.Recipe.
     *
     * @throws IOException
     */
    public void loadInformations() throws IOException {
        page = Jsoup.connect(this.getUrlLink()).get();

        String nameString = page.selectFirst("h1.main-title").toString();
        name = nameString.substring(nameString.indexOf(">")+1, nameString.indexOf("</"));

        pageContent = page.selectFirst("div#sticky-desktop-only");


        imageUrl = pageContent.selectFirst("img#af-diapo-desktop-0_img").toString();
        imageUrl = imageUrl.substring(imageUrl.indexOf("src=\"")+5 , imageUrl.indexOf(".jpg\"")+4);

        String prepTimeString = pageContent.select("span.recipe-infos__timmings__value").get(0).toString();
        prepTime = prepTimeString.substring(prepTimeString.indexOf(">")+1, prepTimeString.indexOf("</"));
        String cookingTimeString = pageContent.select("span.recipe-infos__timmings__value").get(1).toString();
        cookingTime = cookingTimeString.substring(cookingTimeString.indexOf(">")+1, cookingTimeString.indexOf("</"));;
        details = getSteps(pageContent.selectFirst("ol.recipe-preparation__list").toString());

        String ingredientsString = pageContent.selectFirst("ul.recipe-ingredients__list").toString();
        ingredients = extractIngredients(ingredientsString);

        String ppl = page.select("span.title-2.recipe-infos__quantity__value").toString();
        ppl = ppl.substring(ppl.indexOf(">")+1, ppl.indexOf("</"));
        people = Integer.decode(ppl);

    }


    public String getName() {
        return name;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public String getCookingTime() {
        return cookingTime;
    }



    /**
     * Static method which search Recipes by name and returns an ArrayList of these Recipes.
     *
     * @param keyword Title of the source.Recipe to search.
     * @return ArrayList of Recipes. Informations about these Recipes are not available due to performance reasons, you have to call {@link #loadInformations()} loadInformations} on the source.Recipe you want to have the informations.
     * @throws IOException
     */
    public static ArrayList<Recipe> search(String keyword) throws IOException {
        keyword = keyword.replaceAll(" ", "-");

        ArrayList<Recipe> resultRecipes = new ArrayList<>();

        Document document = Jsoup.connect("http://www.marmiton.org/recettes/recherche.aspx?aqt=" + keyword).get();

        Element elementResultsList = document.getElementsByClass("recipe-search__resuts").first();
        Elements resultsElements = elementResultsList.getElementsByClass("recipe-results ");

        for (Element e : resultsElements) {
            Elements currentRecipeElement = e.getElementsByClass("recipe-card");
            String title = currentRecipeElement.first().attr("recipe-card__title");
            String urlLink = currentRecipeElement.first().attr("href");

            resultRecipes.add(new Recipe(title, urlLink));
        }

        return resultRecipes;
    }



    private ArrayList<Ingredient> extractIngredients(String ingredientsString){

        ArrayList<Ingredient> result = new ArrayList<>();

        String completeString[] = ingredientsString.split("</li>");

        for (String s : completeString) {
            if(s.contains("recipe-ingredients__list__item")) {
                String quantityString = s.substring(s.indexOf("<span class=\"recipe-ingredient-qt\" data-base-qt=\""));
                quantityString = quantityString.substring(quantityString.indexOf(">") + 1, quantityString.indexOf("</"));
                float quantity;
                if (quantityString.length() > 0)
                    quantity = Float.parseFloat(quantityString);
                else
                    quantity = 1.0f;

                String ingredientName;
                if (quantity == 1.0f) {
                    ingredientName = s.substring(s.indexOf("<p class=\"name_singular\" data-name-singular=\""));
                    ingredientName = ingredientName.substring(ingredientName.indexOf("data-name-singular=") + 20, ingredientName.indexOf("style")).replace("\"", "");
                } else {
                    ingredientName = s.substring(s.indexOf("<p class=\"name_plural\" data-name-plural=\""));
                    ingredientName = ingredientName.substring(ingredientName.indexOf("data-name-plural=") + 18, ingredientName.indexOf("style")).replace("\"", "");
                }

                String unit = "";

                String imageLink = s.substring(s.indexOf("src=\"")+5, s.indexOf(".jpg")+4);
                //String imageLink = "";

                result.add(new Ingredient(ingredientName, quantity, unit, imageLink));
            }
        }

        return result;
    }

    private ArrayList<String> getSteps(String stepsString) {
        ArrayList<String> results = new ArrayList<>();

        String[] strs = stepsString.split("</li>");

        for(String str : strs) {
            if(str.contains("recipe-preparation__list__item")) {
                str = str.replace("<li class=\"recipe-preparation__list__item\"> <h3 class=\"__secondary\">", "");
                str = str.substring(str.indexOf("</h3> ") + 6);
                while (str.contains("href"))
                    str = new StringBuilder(str).delete(str.indexOf("<"), str.indexOf(">") + 1).toString();
                str = str.replace("</a>", "");
                results.add(str);
            }
        }

        return results;
    }


    /**
     * Method which return a random Recipe.
     * @return  A random Recipe.
     * @throws IOException
     */
    public static Recipe getRandomRecipe() throws IOException {

        Document document = Jsoup.connect("http://www.marmiton.org/recettes/recette-hasard.aspx").get();
        String url = document.baseUri();

        Element element = document.getElementsByClass("m_title").first();
        element = element.getElementsByClass("item").first();
        element = element.getElementsByClass("fn").first();

        String title = element.text();

        Recipe recipe = new Recipe(title, url);
        recipe.loadInformations();

        return recipe;

    }


    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", urlLink='" + urlLink + '\'' +
                ", prepTime='" + prepTime + '\'' +
                ", cookingTime='" + cookingTime + '\'' +
                ", details='" + details + '\'' +
                ", ingredients=" + ingredients +
                ", people=" + people +
                ", image=" + imageUrl +
                '}';
    }
}
