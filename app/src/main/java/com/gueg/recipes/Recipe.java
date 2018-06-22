package com.gueg.recipes;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    public static final int CATEGORY_ENTREE = 0;
    public static final int CATEGORY_PLAT = 1;
    public static final int CATEGORY_DESSERT = 2;

    private int id;
    private int cat;
    private String name;
    private String urlLink;
    private String prepTime;
    private String cookingTime;
    private String imageUrl;
    private String tag;
    private int numReviews;
    private int rating;
    private ArrayList<String> details;
    private ArrayList<Ingredient> ingredients;
    private int people;


    public Recipe(String name, String urlLink, String imgLink, int numReviews, int rating, String tag) {
        this.name = name;
        this.urlLink = urlLink;
        this.imageUrl = imgLink;
        this.tag = tag;
        this.rating = rating;
        this.numReviews = numReviews;
    }

    public static class RecipeLoader extends AsyncTask<Recipe, Void, Recipe> {
        @Override
        protected Recipe doInBackground(Recipe... recipes) {
            try {
                recipes[0].loadInformations();
                return recipes[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private void loadInformations() throws IOException {
        Document page = Jsoup.connect(this.getUrlLink()).get();

        String nameString = page.selectFirst("h1.main-title").toString();
        name = nameString.substring(nameString.indexOf(">")+1, nameString.indexOf("</"));

        Element pageContent = page.selectFirst("div#sticky-desktop-only");


        try {
            imageUrl = pageContent.selectFirst("img#af-diapo-desktop-0_img").toString();
            imageUrl = imageUrl.substring(imageUrl.indexOf("src=\"") + 5, imageUrl.indexOf(".jpg\"") + 4);
        } catch (NullPointerException e) {
            e.printStackTrace();
            imageUrl = "";
        }

        String prepTimeString = pageContent.select("span.recipe-infos__timmings__value").get(0).toString();
        prepTime = prepTimeString.substring(prepTimeString.indexOf(">")+1, prepTimeString.indexOf("</"));
        String cookingTimeString;
        try {
            cookingTimeString = pageContent.select("span.recipe-infos__timmings__value").get(1).toString();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            cookingTimeString = ">0</";
        }
        cookingTime = cookingTimeString.substring(cookingTimeString.indexOf(">")+1, cookingTimeString.indexOf("</"));;
        details = getSteps(pageContent.selectFirst("ol.recipe-preparation__list").toString());

        String ingredientsString = pageContent.selectFirst("ul.recipe-ingredients__list").toString();
        ingredients = extractIngredients(ingredientsString);

        String ppl = page.select("span.title-2.recipe-infos__quantity__value").toString();
        ppl = ppl.substring(ppl.indexOf(">")+1, ppl.indexOf("</"));
        try {
            people = Integer.decode(ppl);
        } catch(NumberFormatException e) {
            e.printStackTrace();
            people = 0;
        }

    }

    public void setCategory(int cat) {
        this.cat = cat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCategory() {
        return cat;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getDetails() {
        return details;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPeople() {
        return people;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public int getRating() {
        return rating;
    }

    public String getTag() {
        return tag;
    }

    public static ArrayList<Recipe> search(String keyword, String limit) throws IOException {
        keyword = keyword.replaceAll(" ", "-");

        ArrayList<Recipe> resultRecipes = new ArrayList<>();

        Document document = Jsoup.connect("http://www.marmiton.org/recettes/recherche.aspx?aqt=" + keyword).get();

        Element elementResultsList = document.selectFirst("div.recipe-search__resuts");

        int lim = 0;
        if(limit!=null)
            lim = Integer.decode(limit);

        if(lim==0||lim>elementResultsList.select("a.recipe-card").size())
            lim = elementResultsList.select("a.recipe-card").size();

        for(int i=0; i < lim; i++) {
            Element current = elementResultsList.select("a.recipe-card").get(i);

            String imgLink = current.selectFirst("img").toString();
            imgLink = imgLink.substring(imgLink.indexOf("src=\"")+5, imgLink.indexOf("\" alt"));

            String urlLink = current.toString().substring(current.toString().indexOf("href=\"")+6, current.toString().indexOf("\">"));

            String title = current.selectFirst("h4.recipe-card__title").toString();
            title = title.substring(title.indexOf("\">")+2, title.indexOf("</"));

            String tags = current.selectFirst("li.mrtn-tag.mrtn-tag--dark").toString();
            tags = tags.substring(tags.indexOf("dark\">")+6, tags.indexOf("</"));

            String numReviewsString = current.selectFirst("span.mrtn-font-discret").toString();
            numReviewsString = numReviewsString.substring(numReviewsString.indexOf("sur ")+4, numReviewsString.indexOf("avis")-1).replace(" ","");
            if (numReviewsString.length()<1)
                numReviewsString = "0";

            String ratingString = current.selectFirst("span.recipe-card__rating__value").toString();
            ratingString = ratingString.substring(ratingString.indexOf("value\">")+7, ratingString.indexOf("</s")).replace(" ","");
            if (ratingString.length()<1)
                ratingString = "0";

            resultRecipes.add(new Recipe(title, urlLink, imgLink, Integer.decode(numReviewsString), Integer.decode(ratingString), tags));
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

                if (quantityString.length() == 0)
                    quantityString = "1";

                String ingredientName;
                if (quantityString.equals("1")) {
                    ingredientName = s.substring(s.indexOf("<p class=\"name_singular\" data-name-singular=\""));
                    ingredientName = ingredientName.substring(ingredientName.indexOf("data-name-singular=") + 20, ingredientName.indexOf("style")).replace("\"", "");
                } else {
                    ingredientName = s.substring(s.indexOf("<p class=\"name_plural\" data-name-plural=\""));
                    ingredientName = ingredientName.substring(ingredientName.indexOf("data-name-plural=") + 18, ingredientName.indexOf("style")).replace("\"", "");
                }
                String imageLink;
                try {
                    imageLink = s.substring(s.indexOf("src=\"") + 5, s.indexOf(".jpg") + 4);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    imageLink = "";
                }

                result.add(new Ingredient(ingredientName, quantityString, imageLink));
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

    public static String getRandomRecipe() throws IOException {
        Document page = Jsoup.connect("http://www.marmiton.org/recettes/recette-hasard.aspx").get();
        String nameString = page.selectFirst("h1.main-title").toString();
        return nameString.substring(nameString.indexOf(">")+1, nameString.indexOf("</"));
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
