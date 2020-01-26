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
import android.view.ViewGroup;
import com.gloxandro.submuxic.domain.Genre;
import com.gloxandro.submuxic.view.FastScroller;
import com.gloxandro.submuxic.view.GenreView;
import com.gloxandro.submuxic.view.UpdateView;

import java.util.List;

public class GenreAdapter extends SectionAdapter<Genre> implements FastScroller.BubbleTextGetter{
	public static int VIEW_TYPE_GENRE = 1;

	public GenreAdapter(Context context, List<Genre> genres, OnItemClickedListener listener) {
        super(context, genres);
		this.onItemClickedListener = listener;
    }

	@Override
	public UpdateView.UpdateViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
		return new UpdateView.UpdateViewHolder(new GenreView(context));
	}

	@Override
	public void onBindViewHolder(UpdateView.UpdateViewHolder holder, Genre item, int viewType) {
		holder.getUpdateView().setObject(item);
	}

	@Override
	public int getItemViewType(Genre item) {
		return VIEW_TYPE_GENRE;
	}

	@Override
	public String getTextToShowInBubble(int position) {
		return getNameIndex(getItemForPosition(position).getName());
	}
}
