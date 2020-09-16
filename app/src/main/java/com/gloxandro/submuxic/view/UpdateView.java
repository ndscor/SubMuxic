/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package com.gloxandro.submuxic.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.recyclerview.widget.RecyclerView;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.domain.MusicDirectory;
import com.gloxandro.submuxic.util.DrawableTint;
import com.gloxandro.submuxic.util.SilentBackgroundTask;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import static com.gloxandro.submuxic.activity.SubsonicActivity.theme;
import static com.gloxandro.submuxic.util.ThemeUtil.THEME_LIGHT;

public abstract class UpdateView<T> extends LinearLayout {
	private static final String TAG = UpdateView.class.getSimpleName();
	private static final WeakHashMap<UpdateView, ?> INSTANCES = new WeakHashMap<UpdateView, Object>();

	protected static Handler backgroundHandler;
	protected static Handler uiHandler;
	private static Runnable updateRunnable;
	private static int activeActivities = 0;

	protected Context context;
	protected T item;
	protected RatingBar ratingBar;
	protected ImageButton starButton;
	protected ImageView moreButton;
	protected View coverArtView;
	
	protected boolean exists = false;
	protected boolean pinned = false;
	protected boolean shaded = false;
	protected boolean starred = false;
	protected boolean isStarred = false;
	protected int isRated = 0;
	protected int rating = 0;
	protected SilentBackgroundTask<Void> imageTask = null;
	protected Drawable startBackgroundDrawable;
	
	protected final boolean autoUpdate;
	protected boolean checkable;
	
	public UpdateView(Context context) {
		this(context, true);
	}
	public UpdateView(Context context, boolean autoUpdate) {
		super(context);
		this.context = context;
		this.autoUpdate = autoUpdate;
		
		setLayoutParams(new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		if(autoUpdate) {
			INSTANCES.put(this, null);
		}
		startUpdater();
	}
	
	@Override
	public void setPressed(boolean pressed) {
		
	}
	
	public void setObject(T obj) {
		if(item == obj) {
			return;
		}

		item = obj;
		if(imageTask != null) {
			imageTask.cancel();
			imageTask = null;
		}
		if(coverArtView != null && coverArtView instanceof ImageView) {
			((ImageView) coverArtView).setImageDrawable(null);
		}
		setObjectImpl(obj);
		updateBackground();
		update();
	}
	public void setObject(T obj1, Object obj2) {
		setObject(obj1, null);
	}
	protected abstract void setObjectImpl(T obj);
	
	private static synchronized void startUpdater() {
		if(uiHandler != null) {
			return;
		}
		
		uiHandler = new Handler();
		// Needed so handler is never null until thread creates it
		backgroundHandler = uiHandler;
		updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateAll();
            }
        };
		
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				backgroundHandler = new Handler(Looper.myLooper());
				uiHandler.post(updateRunnable);
				Looper.loop();
			}
		}, "UpdateView").start();
    }

	public static synchronized void triggerUpdate() {
		if(backgroundHandler != null) {
			uiHandler.removeCallbacksAndMessages(null);
			backgroundHandler.removeCallbacksAndMessages(null);
			uiHandler.post(updateRunnable);
		}
	}

    private static void updateAll() {
        try {
			// If nothing can see this, stop updating
			if(activeActivities == 0) {
				activeActivities--;
				return;
			}

			List<UpdateView> views = new ArrayList<UpdateView>();
            for (UpdateView view : INSTANCES.keySet()) {
                if (view.isShown()) {
					views.add(view);
                }
            }
			if(views.size() > 0) {
				updateAllLive(views);
			} else {
				uiHandler.postDelayed(updateRunnable, 2000L);
			}
        } catch (Throwable x) {
            Log.w(TAG, "Error when updating song views.", x);
        }
    }
	private static void updateAllLive(final List<UpdateView> views) {
		final Runnable runnable = new Runnable() {
			@Override
            public void run() {
				try {
					for(UpdateView view: views) {
						view.update();
					}
				} catch (Throwable x) {
					Log.w(TAG, "Error when updating song views.", x);
				}
				uiHandler.postDelayed(updateRunnable, 1000L);
			}
		};
		
		backgroundHandler.post(new Runnable() {
			@Override
            public void run() {
				try {
					for(UpdateView view: views) {
						view.updateBackground();
					}
					uiHandler.post(runnable);
				} catch (Throwable x) {
					Log.w(TAG, "Error when updating song views.", x);
				}
			}
		});
	}

	public static boolean hasActiveActivity() {
		return activeActivities > 0;
	}

	public static void addActiveActivity() {
		activeActivities++;

		if(activeActivities == 0 && uiHandler != null && updateRunnable != null) {
			activeActivities++;
			uiHandler.post(updateRunnable);
		}
	}
	public static void removeActiveActivity() {
		activeActivities--;
	}

	public static MusicDirectory.Entry findEntry(MusicDirectory.Entry entry) {
		for(UpdateView view: INSTANCES.keySet()) {
			MusicDirectory.Entry check = null;
			if(view instanceof SongView) {
				check = ((SongView) view).getEntry();
			} else if(view instanceof AlbumView) {
				check = ((AlbumView) view).getEntry();
			}

			if(check != null && entry != check && check.getId().equals(entry.getId())) {
				return check;
			}
		}

		return null;
	}
	
	protected void updateBackground() {
		
	}
	protected void update() {
		if(moreButton != null) {
			if(exists || pinned) {
				if(!shaded) {
					if (THEME_LIGHT.equals(theme)) {
						moreButton.setImageResource(exists ? R.drawable.catch_black : R.drawable.download_black);
						shaded = true;
					} else {
						moreButton.setImageResource(exists ? R.drawable.catch_white : R.drawable.download_white);
						shaded = true;
					}

				}
			} else {
				if(shaded) {
					moreButton.setImageResource(DrawableTint.getDrawableRes(context, R.attr.download_none));


					if (THEME_LIGHT.equals(theme)) {
						moreButton.setImageResource(DrawableTint.getDrawableRes(context, R.attr.download_menu_light));
						shaded = false;
					} else {
						moreButton.setImageResource(DrawableTint.getDrawableRes(context, R.attr.download_none));
						shaded = false;
					}

				}
			}
		}
		
		if(starButton != null) {
			if(isStarred) {
				if(!starred) {
					if(starButton.getDrawable() == null) {
						starButton.setImageResource(DrawableTint.getDrawableRes(context, R.attr.starred));

					}
					starButton.setVisibility(View.VISIBLE);
					starred = true;
				}
			} else {
				if(starred) {
					starButton.setVisibility(View.GONE);
					starred = false;
				}
			}
		}

		if(ratingBar != null && isRated != rating) {
			if(isRated > 0 && rating == 0) {
				ratingBar.setVisibility(View.VISIBLE);
			} else if(isRated == 0 && rating > 0) {
				ratingBar.setVisibility(View.GONE);
			}

			ratingBar.setRating(isRated);
			rating = isRated;
		}

		if(coverArtView != null && coverArtView instanceof RecyclingImageView) {
			RecyclingImageView recyclingImageView = (RecyclingImageView) coverArtView;
			if(recyclingImageView.isInvalidated()) {
				onUpdateImageView();
			}
		}
	}

	public boolean isCheckable() {
		return checkable;
	}
	public void setChecked(boolean checked) {
		View child = getChildAt(0);
		if (checked && startBackgroundDrawable == null) {
			startBackgroundDrawable = child.getBackground();

			if (THEME_LIGHT.equals(theme)) {
				child.setBackgroundColor(DrawableTint.getColorRes(context, R.attr.colorControlHighlight));
			} else {
				child.setBackgroundColor(DrawableTint.getColorRes(context, R.attr.selected_effect));

			}
		} else if (!checked && startBackgroundDrawable != null) {
			child.setBackground(startBackgroundDrawable);
			startBackgroundDrawable = null;
		}
	}

	public void onClick() {

	}

	public void onUpdateImageView() {

	}

	public static class UpdateViewHolder<T> extends RecyclerView.ViewHolder {
		private UpdateView updateView;
		private View view;
		private T item;

		public UpdateViewHolder(UpdateView itemView) {
			super(itemView);

			this.updateView = itemView;
			this.view = itemView;
		}

		// Different is so that call is not ambiguous
		public UpdateViewHolder(View view, boolean different) {
			super(view);
			this.view = view;
		}

		public UpdateView<T> getUpdateView() {
			return updateView;
		}
		public View getView() {
			return view;
		}
		public void setItem(T item) {
			this.item = item;
		}
		public T getItem() {
			return item;
		}
	}
}

