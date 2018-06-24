package com.gueg.recipes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gueg.recipes.tools.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;


public class RecipeIntentDialog extends DialogFragment {

    private BlurDialogEngine mBlurEngine;

    View rootView;

    RecipeUpdate _listener;

    RecyclerView _intentRecipeView;
    RecipeIntentAdapter _intentRecipeAdapter;

    Recipe _r[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.CustomDialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_recipe_intent, container, false);
        super.onCreateView(inflater, container, savedInstanceState);


        mBlurEngine = new BlurDialogEngine(Objects.requireNonNull(getActivity()));
        mBlurEngine.setBlurRadius(8);
        mBlurEngine.setDownScaleFactor(8f);
        mBlurEngine.debug(false);
        mBlurEngine.setBlurActionBar(true);
        mBlurEngine.setUseRenderScript(true);

        _intentRecipeView = rootView.findViewById(R.id.dialog_recipe_intent_recyclerview);

        rootView.findViewById(R.id.dialog_recipe_intent_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _listener.onRecipesLoaded(_r);
                dismiss();
            }
        });

        rootView.findViewById(R.id.dialog_recipe_intent_annuler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Recipe r : _r)
                    Recipe.loadRecipe(getActivity(), r);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _intentRecipeView.setHasFixedSize(true);
                        _intentRecipeView.setLayoutManager(new LinearLayoutManager(getContext()));
                        _intentRecipeView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                        _intentRecipeAdapter = new RecipeIntentAdapter(new ArrayList<>(Arrays.asList(_r)));
                        _intentRecipeView.setAdapter(_intentRecipeAdapter);
                    }
                });
            }
        }).start();


        return rootView;
    }

    public void setRecipes(String strs[]) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for(String url : strs) {
            if(url.contains("http://")&&url.contains("marmiton.org"))
                recipes.add(new Recipe(url));
        }

        Recipe recs[] = new Recipe[recipes.size()];
        for(int i=0; i<recipes.size(); i++)
            recs[i] = recipes.get(i);

        _r = recs;
    }

    public void setListener(RecipeUpdate listener) {
        _listener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume(getRetainInstance());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mBlurEngine.onDismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlurEngine.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }


    public interface RecipeUpdate{
        void onRecipesLoaded(Recipe[] recipes);
    }
}
