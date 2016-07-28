package com.timitoc.groupic.utils;

import android.widget.AbsListView;
import android.widget.StackView;
import com.timitoc.groupic.fragments.SearchGroupsFragment;

/**
 * Taken from http://benjii.me/2010/08/endless-scrolling-listview-in-android/
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    public static EndlessScrollListener instance;

    private int visibleThreshold = 1;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private SearchGroupsFragment callback;

    public static EndlessScrollListener getInstance(SearchGroupsFragment callback) {
        if (instance == null)
            instance = new EndlessScrollListener(callback);
        return instance;
    }

    public void reset() {
        currentPage = 0;
        previousTotal = 0;
        loading = true;
        callback.searchForNextGroups(currentPage);
    }

    private EndlessScrollListener(SearchGroupsFragment callback) {
        this.callback = callback;
    }

    private EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            callback.searchForNextGroups(currentPage);
            System.out.println("Started loading groups from page " + (currentPage));
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}

