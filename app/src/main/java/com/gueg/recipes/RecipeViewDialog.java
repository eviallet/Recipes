package com.gueg.recipes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gueg.recipes.recipes_database.RecipeDatabase;
import com.gueg.recipes.tools.RecyclerItemClickListener;
import com.gueg.recipes.tools.VerticalSpaceItemDecoration;
import com.plattysoft.leonids.ParticleSystem;

import java.util.Objects;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;


public class RecipeViewDialog extends DialogFragment {

    private BlurDialogEngine mBlurEngine;

    View rootView;

    TextView _title;
    TextView _prep;
    TextView _cook;
    TextView _people;
    ImageView _fav;

    RecipeUpdate _listener;

    RecyclerView _ingredientsView;
    IngredientsAdapter _ingredientsAdapter;
    RecyclerView _stepsView;
    StepsAdapter _stepsAdapter;

    ListPopupWindow _menu;
    String[] _menuChoices={"Entrée", "Plat", "Dessert"};


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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_recipe_view, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(":-:","Preparing blur");

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
        _ingredientsView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), _ingredientsView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {}
            @Override
            public void onLongItemClick(View view, int position) {
                LocalBroadcastManager.getInstance(getActivity()).
                        sendBroadcast(
                                new Intent().
                                        setAction(MainActivity.SHOPPING_KEY).
                                        putExtra(MainActivity.SHOPPING_ITEM, _r.getIngredients().get(position)));
                showCustomToast();

            }
        }));
        _stepsView = rootView.findViewById(R.id.dialog_recipe_view_steps_recyclerview);

        _fav = rootView.findViewById(R.id.dialog_recipe_view_favorite);


        _menu = new ListPopupWindow(getActivity());
        _menu.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, _menuChoices));
        _menu.setAnchorView(_fav);
        _menu.setWidth(600);
        _menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position>=0&&position<=2)
                    _r.setCategory(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RecipeDatabase.getDatabase(getContext()).recipeDao().insertAll(_r);
                    }
                }).start();

                new ParticleSystem(getActivity(), 60, R.drawable.particle_circle_green, 500)
                        .setSpeedRange(0.05f, 0.15f).setStartTime(250).setFadeOut(250)
                        .oneShot(_fav, 60);
                _fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                _menu.dismiss();
            }
        });
        _fav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _menu.show();
                return true;
            }
        });

        Log.d(":-:","Dialog layout inflated");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Recipe.loadRecipe(getActivity(), _r);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootView.findViewById(R.id.dialog_recipe_view_loading).setVisibility(View.GONE);
                        rootView.findViewById(R.id.dialog_recipe_view_container).setVisibility(View.VISIBLE);

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


                        _fav.setOnClickListener(new View.OnClickListener() {
                            private boolean _state = false;
                            private boolean _modifRegistered = false;

                            @Override
                            public void onClick(View view) {
                                if(_modif && !_modifRegistered) {
                                    _fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                                    _state = true;
                                    _modifRegistered = true;
                                } else {
                                    _state = !_state;
                                    if (_state)
                                        _menu.show();
                                    else
                                        deleteRecipe();
                                }
                            }
                        });

                        if(_modif)
                            _fav.callOnClick();

                        rootView.findViewById(R.id.dialog_recipe_view_share).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Partager");
                                i.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.recipe.com/&"+_r.getUrl());
                                startActivity(Intent.createChooser(i,"Partager avec"));
                            }
                        });
                    }
                });
            }
        }).start();


        return rootView;
    }

    private void deleteRecipe() {
        if(_modif) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    RecipeDatabase.getDatabase(getContext()).recipeDao().delete(_r);
                                    if(_modif)
                                        _listener.onRecipeRemoved();
                                }
                            }).start();
                            _fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Supprimer la recette ?").setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RecipeDatabase.getDatabase(getContext()).recipeDao().delete(_r);
                    if(_modif)
                        _listener.onRecipeRemoved();
                }
            }).start();
            _fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
        }
    }

    public void setRecipe(Recipe r, boolean modif) {
        _r = r;
        _modif = modif;
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

    private void showCustomToast() {
        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(
                getLayoutInflater().inflate(
                        R.layout.custom_toast,
                        (ViewGroup) rootView.findViewById(R.id.custom_toast_container)));
        toast.show();
    }

    public interface RecipeUpdate{
        void onRecipeRemoved();
    }
}
