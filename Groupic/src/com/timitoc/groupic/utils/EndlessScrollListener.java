package com.timitoc.groupic.utils;

import android.widget.AbsListView;
import com.timitoc.groupic.fragments.SearchGroupsFragment;

/**
 * Inspired from http://benjii.me/2010/08/endless-scrolling-listview-in-android/
 * but strongly modified and adapted.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    public static EndlessScrollListener instance;

    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private SearchGroupsFragment callback;

    ///My improvised multiton
    public static EndlessScrollListener getInstance(SearchGroupsFragment callback) {
        if (callback != null)
            instance = new EndlessScrollListener(callback);
        return instance;
    }

    private EndlessScrollListener(SearchGroupsFragment callback) {
        this.callback = callback;
    }
    public void reset() {
        currentPage = 0;
        previousTotal = 0;
        loading = true;
        callback.searchForNextGroups(currentPage);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        int visibleThreshold = 1;
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            callback.searchForNextGroups(currentPage);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}

