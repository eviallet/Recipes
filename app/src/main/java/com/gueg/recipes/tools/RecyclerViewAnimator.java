package com.gueg.recipes.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.gueg.recipes.R;

public class RecyclerViewAnimator {

    // https://www.youtube.com/watch?v=33wOlQ2y0hQ
    public static void runAnimation(RecyclerView view) {
        Context context = view.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.recyclerview_layout_enter_bottom);

        view.setLayoutAnimation(controller);
        view.scheduleLayoutAnimation();
    }
}
