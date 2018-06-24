package com.gueg.recipes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.gueg.recipes.recipes_database.Converters;
import com.gueg.recipes.recipes_database.RecipeDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Entity
@TypeConverters(Converters.class)
public class Recipe {

    public static final int CATEGORY_ENTREE = 0;
    public static final int CATEGORY_PLAT = 1;
    public static final int CATEGORY_DESSERT = 2;

    public static final String PARAM_NONE = "com.gueg.recipes.recipessearchactivity.none";
    public static final String PARAM_LIMIT = "com.gueg.recipes.recipessearchactivity.limit";
    public static final String PARAM_CURPOS = "com.gueg.recipes.recipessearchactivity.curpos";

    private String name;
    @PrimaryKey @NonNull
    private String url;
    private String prepTime;
    private String cookingTime;
    private String imageUrl;
    private String tag;
    private int numReviews;
    private int rating;
    private int cat;
    private int people;
    private ArrayList<String> details;
    private ArrayList<Ingredient> ingredients;


    @Ignore
    public Recipe(@NonNull String url) {
        this.url = url;
        this.cat = CATEGORY_PLAT;
    }

    @Ignore
    public Recipe(String name, @NonNull String url, String imgLink, int numReviews, int rating, String tag) {
        this.name = name;
        this.url = url;
        this.imageUrl = imgLink;
        this.tag = tag;
        this.rating = rating;
        this.numReviews = numReviews;
    }

    public Recipe(String name, @NonNull String url, String prepTime, String cookingTime, String imageUrl, String tag, int numReviews, int rating, int cat, int people, ArrayList<String> details, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.url = url;
        this.prepTime = prepTime;
        this.cookingTime = cookingTime;
        this.imageUrl = imageUrl;
        this.tag = tag;
        this.numReviews = numReviews;
        this.rating = rating;
        this.cat = cat;
        this.people = people;
        this.details = details;
        this.ingredients = ingredients;
    }

    private static class RecipeLoader extends AsyncTask<Recipe, Void, Recipe> {
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

    public static void loadRecipe(Context c, Recipe r) {
        if(RecipeDatabase.getDatabase(c).recipeDao().findByUrl(r.getUrl())==null) {
            try {
                new RecipeLoader().execute(r).get();
            } catch (ExecutionException|InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadInformations() throws IOException {
        Document page = Jsoup.connect(getUrl()).get();

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

    public int getCat() {
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

    public String getUrl() {
        return url;
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

    public static ArrayList<Recipe> search(String keyword, String paramDef, int param) throws IOException {
        keyword = keyword.replaceAll(" ", "-");

        ArrayList<Recipe> resultRecipes = new ArrayList<>();

        String pageString = "";
        if(paramDef.equals(PARAM_CURPOS))
            pageString = "&start="+Integer.toString(param);
        Document document = Jsoup.connect("http://www.marmiton.org/recettes/recherche.aspx?aqt=" + keyword + pageString).get();

        Element elementResultsList = document.selectFirst("div.recipe-search__resuts");

        int lim = 0;
        if(paramDef.equals(PARAM_LIMIT))
            lim = param;

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
                str = str.replace("</a>", "").replace("<br>","");
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
                ", url='" + url + '\'' +
                ", prepTime='" + prepTime + '\'' +
                ", cookingTime='" + cookingTime + '\'' +
                ", details='" + details + '\'' +
                ", ingredients=" + ingredients +
                ", people=" + people +
                ", image=" + imageUrl +
                '}';
    }
}
