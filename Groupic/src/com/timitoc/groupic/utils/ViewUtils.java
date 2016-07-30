package com.timitoc.groupic.utils;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import com.timitoc.groupic.R;

import java.lang.reflect.Method;

/*
Made combining different answers from SO and other sites regarding view expand/collapse problem
 */

public class ViewUtils {

    public static void toggle(final View summary) {
        if (summary.getVisibility() == View.GONE) {
            animateView(expand(summary, true), summary);
        } else {
            animateView(expand(summary, false), summary);
        }
    }

    private static void animateView(Animation a, View v) {
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                    v,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        } else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = 0;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(300);
        return a;
    }
}
