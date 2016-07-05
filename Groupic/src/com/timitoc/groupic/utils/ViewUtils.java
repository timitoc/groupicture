package com.timitoc.groupic.utils;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import com.timitoc.groupic.R;

/*
Made combining different answers from SO and other sites regarding view expand/collapse problem
 */

public class ViewUtils {

    public static void addAction(final View layout, final View summary, final Context context) {
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (summary.getVisibility() == View.GONE) {
                    //expand(summary);
                    //expandOrCollapse(summary, "expand");
                    summary.setVisibility(View.VISIBLE);
                    slide_down(context, summary);
                } else {
                    //collapse(summary);
                    //expandOrCollapse(summary, "collapse");
                }
            }
        });
    }

    private static void expand(View summary) {
        //set Visible
        summary.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 300);

        ValueAnimator mAnimator = slideAnimator(0, 300, summary);

        mAnimator.start();
    }

    private static void collapse(final View summary) {
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, summary);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                summary.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }


    private static ValueAnimator slideAnimator(int start, int end, final View summary) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static void expandOrCollapse(final View v,String exp_or_colpse) {
        TranslateAnimation anim = null;
        if(exp_or_colpse.equals("expand"))
        {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        }
        else{
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener= new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);
        }

        // To Collapse
        //

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
}
