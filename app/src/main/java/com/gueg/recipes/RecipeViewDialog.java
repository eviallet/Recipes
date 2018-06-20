package com.gueg.recipes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gueg.recipes.tools.VerticalSpaceItemDecoration;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;


public class RecipeViewDialog extends DialogFragment {

    private BlurDialogEngine mBlurEngine;

    View rootView;

    TextView _title;
    TextView _prep;
    TextView _cook;
    TextView _people;

    RecyclerView _ingredientsView;
    IngredientsAdapter _ingredientsAdapter;
    RecyclerView _stepsView;
    StepsAdapter _stepsAdapter;

    Recipe _r;
    boolean _modif;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_recipe_view, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        mBlurEngine = new BlurDialogEngine(Objects.requireNonNull(getActivity()));
        mBlurEngine.setBlurRadius(8);
        mBlurEngine.setDownScaleFactor(8f);
        mBlurEngine.debug(false);
        mBlurEngine.setBlurActionBar(true);
        mBlurEngine.setUseRenderScript(true);


        _title = rootView.findViewById(R.id.dialog_recipe_view_title);
        _title.setText(_r.getName());

        _prep = rootView.findViewById(R.id.dialog_recipe_view_details_preptime);
        _cook = rootView.findViewById(R.id.dialog_recipe_view_details_cooktime);
        _people = rootView.findViewById(R.id.dialog_recipe_view_details_people);

        _ingredientsView = rootView.findViewById(R.id.dialog_recipe_view_ingredients_recyclerview);
        _stepsView = rootView.findViewById(R.id.dialog_recipe_view_steps_recyclerview);

        try {
            _r = new Recipe.RecipeLoader().execute(_r).get();
        } catch(InterruptedException|ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        _people.setText(Integer.toString(_r.getPeople()));

        _prep.setText(_r.getPrepTime());
        _cook.setText(_r.getCookingTime());


        _ingredientsView.setHasFixedSize(true);
        _ingredientsView.setLayoutManager(new LinearLayoutManager(getContext()));
        _ingredientsView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        _ingredientsAdapter = new IngredientsAdapter(_r.getIngredients());
        _ingredientsView.setAdapter(_ingredientsAdapter);

        _stepsView.setHasFixedSize(true);
        _stepsView.setLayoutManager(new LinearLayoutManager(getContext()));
        _stepsView.addItemDecoration(new VerticalSpaceItemDecoration(15));
        _stepsAdapter = new StepsAdapter(_r.getDetails(), _r.getDetails().size());
        _stepsView.setAdapter(_stepsAdapter);



        /*
        if(modif) {
            Button remove = rootView.findViewById(R.id.btn_bookmarkActivity_remove);
            remove.setVisibility(View.VISIBLE);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _listener.onBookmarkRemoved();
                    dismiss();
                }
            });
        }

        rootView.findViewById(R.id.btn_bookmarkActivity_annuler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootView.findViewById(R.id.btn_bookmarkActivity_valider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().isEmpty()&&!url.getText().toString().isEmpty()) {
                    _listener.onBookmarkAdded(new Bookmark(title.getText().toString(),url.getText().toString(),b.getPic()));
                    dismiss();
                } else
                    Toast.makeText(getActivity(), "Entrer un titre et une url", Toast.LENGTH_SHORT).show();
            }
        });
        */

        return rootView;
    }

    public void setRecipe(Recipe r, boolean modif) {
        _r = r;
        _modif = modif;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume(getRetainInstance());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //_listener.onCancel();
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
}
