/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.philm.in.fragments.base;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import app.philm.in.Constants;
import app.philm.in.R;
import app.philm.in.view.MovieDetailCardLayout;
import app.philm.in.view.PhilmImageView;
import app.philm.in.view.PinnedSectionListView;
import app.philm.in.view.ViewRecycler;

public abstract class BaseDetailFragment extends BasePhilmMovieFragment
        implements AdapterView.OnItemClickListener{

    private PinnedSectionListView mListView;
    private ListAdapter mAdapter;

    private TextView mEmptyView;
    private PhilmImageView mBigPosterImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = createListAdapter();

        mListView = (PinnedSectionListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mListView.setEmptyView(mEmptyView);

        mBigPosterImageView = (PhilmImageView) view.findViewById(R.id.imageview_poster);
        if (mBigPosterImageView != null) {
            mBigPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBigPosterClicked();
                }
            });
        }
    }

    protected void setEmptyText(int stringId) {
        if (mEmptyView != null) {
            mEmptyView.setText(stringId);
        }
    }

    protected PhilmImageView getBigPosterView() {
        return mBigPosterImageView;
    }

    protected boolean hasBigPosterView() {
        return mBigPosterImageView != null;
    }

    protected void onBigPosterClicked() {
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    protected abstract ListAdapter createListAdapter();

    protected PinnedSectionListView getListView() {
        return mListView;
    }

    protected ListAdapter getListAdapter() {
        return mAdapter;
    }

    protected interface DetailType<E> {

        public String name();

        public int ordinal();

        public int getLayoutId();

        public int getViewType();

        public boolean isEnabled();

    }

    protected abstract class BaseDetailAdapter<E extends DetailType> extends BaseAdapter
            implements PinnedSectionListView.PinnedSectionListAdapter {

        private List<E> mListItems;

        public void setItems(List<E> listItems) {
            mListItems = listItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mListItems != null ? mListItems.size() : 0;
        }

        @Override
        public E getItem(int position) {
            return mListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).ordinal();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public abstract int getViewTypeCount();

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getViewType();
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).isEnabled();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final E item = getItem(position);

            if (view == null) {
                final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(item.getLayoutId(), viewGroup, false);
            }

            // Now bind to the view
            bindView(item, view);

            return view;
        }

        protected abstract void bindView(final E item, final View view);

        protected void populateDetailGrid(
                final ViewGroup layout,
                final MovieDetailCardLayout cardLayout,
                final View.OnClickListener seeMoreClickListener,
                final BaseAdapter adapter) {

            final ViewRecycler viewRecycler = new ViewRecycler(layout);
            viewRecycler.recycleViews();

            if (!adapter.isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = adapter.getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = adapter.getView(i, viewRecycler.getRecycledView(), layout);
                    layout.addView(view);
                }

                final boolean showSeeMore = numItems < adapter.getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);
            }

            viewRecycler.clearRecycledViews();
        }

        protected void rebindView(final E item) {
            if (Constants.DEBUG) {
                Log.d(getClass().getSimpleName(), "rebindView. Item: " + item.name());
            }

            ListView listView = getListView();

            for (int i = 0, z = listView.getChildCount(); i < z; i++) {
                View child = listView.getChildAt(i);
                if (child != null && child.getTag() == item) {
                    bindView(item, child);
                    return;
                }
            }
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return false;
        }
    }

}
