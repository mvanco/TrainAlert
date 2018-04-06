package cz.intesys.trainalert.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    /**
     * I want my FAB to react on RecyclerView
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    /**
     * I want my FAB to react on scroll events on RecyclerView
     *
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param axes
     * @param type
     * @return
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    /**
     * Action with my FAB according to scroll state of RecyclerView
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     * @param type
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        Log.d("onNestedScroll", "dxConsumed:" + dxConsumed + " dyConsumed:" + dyConsumed + " dxUnconsumed:" + dxUnconsumed + " dyUnconsumed:" + dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                }

                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    child.setVisibility(View.INVISIBLE);
                }
            });
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}