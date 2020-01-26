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

package com.gloxandro.submuxic.adapter;

import android.content.Context;

import java.util.List;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.domain.Bookmark;
import com.gloxandro.submuxic.domain.MusicDirectory;
import com.gloxandro.submuxic.util.Util;
import com.gloxandro.submuxic.view.SongView;
import com.gloxandro.submuxic.view.UpdateView;

public class BookmarkAdapter extends SectionAdapter<MusicDirectory.Entry> {
	private final static String TAG = BookmarkAdapter.class.getSimpleName();
	
	public BookmarkAdapter(Context activity, List<MusicDirectory.Entry> bookmarks, OnItemClickedListener listener) {
		super(activity, bookmarks);
		this.onItemClickedListener = listener;
		checkable = true;
	}

	@Override
	public UpdateView.UpdateViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
		return new UpdateView.UpdateViewHolder(new SongView(context));
	}

	@Override
	public void onBindViewHolder(UpdateView.UpdateViewHolder holder, MusicDirectory.Entry item, int viewType) {
		SongView songView = (SongView) holder.getUpdateView();
		Bookmark bookmark = item.getBookmark();

		songView.setObject(item, true);

		// Add current position to duration
		TextView durationTextView = (TextView) songView.findViewById(R.id.song_duration);
		String duration = durationTextView.getText().toString();
		durationTextView.setText(Util.formatDuration(bookmark.getPosition() / 1000) + " / " + duration);
	}

	@Override
	public int getItemViewType(MusicDirectory.Entry item) {
		return EntryGridAdapter.VIEW_TYPE_SONG;
	}

	@Override
	public void onCreateActionModeMenu(Menu menu, MenuInflater menuInflater) {
		if(Util.isOffline(context)) {
			menuInflater.inflate(R.menu.multiselect_media_offline, menu);
		} else {
			menuInflater.inflate(R.menu.multiselect_media, menu);
		}

		menu.removeItem(R.id.menu_remove_playlist);
	}
}
