/*
  This file is part of Subsonic.
	Subsonic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	Subsonic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with Subsonic. If not, see <http://www.gnu.org/licenses/>.
	Copyright 2015 (C) Scott Jackson
*/

package com.gloxandro.submuxic.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.adapter.SectionAdapter;
import com.gloxandro.submuxic.service.MusicService;
import com.gloxandro.submuxic.service.MusicServiceFactory;
import com.gloxandro.submuxic.util.Constants;
import com.gloxandro.submuxic.util.ProgressListener;
import com.gloxandro.submuxic.util.TabBackgroundTask;
import com.gloxandro.submuxic.view.FastScroller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class SelectRecyclerFragment<T> extends SubsonicFragment implements SectionAdapter.OnItemClickedListener<T> {
	private static final String TAG = SelectRecyclerFragment.class.getSimpleName();
	protected RecyclerView recyclerView;
	public static RecyclerView recyclerViewDivider;
	protected FastScroller fastScroller;
	protected SectionAdapter<T> adapter;
	protected UpdateTask currentTask;
	protected List<T> objects;
	protected boolean serialize = true;
	protected boolean largeAlbums = false;
	protected boolean pullToRefresh = true;
	protected boolean backgroundUpdate = true;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if(bundle != null && serialize) {
			objects = (List<T>) bundle.getSerializable(Constants.FRAGMENT_LIST);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(serialize) {
			outState.putSerializable(Constants.FRAGMENT_LIST, (Serializable) objects);
		}
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		rootView = inflater.inflate(R.layout.abstract_recycler_fragment, container, false);

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
		refreshLayout.setOnRefreshListener(this);
		refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recycler);
		recyclerViewDivider = (RecyclerView) rootView.findViewById(R.id.fragment_recycler);
		recyclerViewDivider.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

		fastScroller = (FastScroller) rootView.findViewById(R.id.fragment_fast_scroller);
		setupLayoutManager();

		if(pullToRefresh) {
			setupScrollList(recyclerView);
		} else {
			refreshLayout.setEnabled(false);
		}

		if(objects == null) {
			refresh(false);
		} else {
			recyclerView.setAdapter(adapter = getAdapter(objects));
		}

		return rootView;
	}



	public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
		private Drawable mDivider;

		public SimpleDividerItemDecoration(Context context) {
			TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.default_divider });
			int resourceId = typedArray.getResourceId(0, 0);
			typedArray.recycle();
			mDivider = context.getResources().getDrawable(resourceId);
			}



		@Override
		public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
			int left = parent.getPaddingStart();
			int right = parent.getWidth() - parent.getPaddingEnd();

			int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = parent.getChildAt(i);

				RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

				int top = child.getBottom() + params.bottomMargin;
				int bottom = top + mDivider.getIntrinsicHeight();

				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
	}

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		if(!primaryFragment) {
			return;
		}

		menuInflater.inflate(getOptionsMenu(), menu);
		onFinishSetupOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setIsOnlyVisible(boolean isOnlyVisible) {
		boolean update = this.isOnlyVisible != isOnlyVisible;
		super.setIsOnlyVisible(isOnlyVisible);
		if(update && adapter != null) {
			RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
			if(layoutManager instanceof GridLayoutManager) {
				((GridLayoutManager) layoutManager).setSpanCount(getRecyclerColumnCount());
			}
		}
	}

	@Override
	protected void refresh(final boolean refresh) {
		int titleRes = getTitleResource();
		if(titleRes != 0) {
			setTitle(getTitleResource());
		}
		if(backgroundUpdate) {
			recyclerView.setVisibility(View.GONE);
		}
		
		// Cancel current running task before starting another one
		if(currentTask != null) {
			currentTask.cancel();
		}

		currentTask = new UpdateTask(this, refresh);

		if(backgroundUpdate) {
			currentTask.execute();
		} else {
			objects = new ArrayList<T>();

			try {
				objects = getObjects(null, refresh, null);
			} catch (Exception x) {
				Log.e(TAG, "Failed to load", x);
			}

			currentTask.done(objects);
		}
	}

	public SectionAdapter getCurrentAdapter() {
		return adapter;
	}

	private void setupLayoutManager() {
		setupLayoutManager(recyclerView, largeAlbums);
	}

	public abstract int getOptionsMenu();
	public abstract SectionAdapter<T> getAdapter(List<T> objs);
	public abstract List<T> getObjects(MusicService musicService, boolean refresh, ProgressListener listener) throws Exception;
	public abstract int getTitleResource();
	
	public void onFinishRefresh() {
		
	}

	private class UpdateTask extends TabBackgroundTask<List<T>> {
		private boolean refresh;

		public UpdateTask(SubsonicFragment fragment, boolean refresh) {
			super(fragment);
			this.refresh = refresh;
		}

		@Override
		public List<T> doInBackground() throws Exception {
			MusicService musicService = MusicServiceFactory.getMusicService(context);

			objects = new ArrayList<T>();

			try {
				objects = getObjects(musicService, refresh, this);
			} catch (Exception x) {
				Log.e(TAG, "Failed to load", x);
			}

			return objects;
		}

		@Override
		public void done(List<T> result) {
			if (result != null && !result.isEmpty()) {
				recyclerView.setAdapter(adapter = getAdapter(result));
				if(!fastScroller.isAttached()) {
					fastScroller.attachRecyclerView(recyclerView);
				}

				onFinishRefresh();
				recyclerView.setVisibility(View.VISIBLE);
			} else {
				setEmpty(true);
			}

			currentTask = null;
		}
	}
}
