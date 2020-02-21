package com.gloxandro.submuxic.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.adapter.MainAdapter;
import com.gloxandro.submuxic.adapter.SectionAdapter;
import com.gloxandro.submuxic.domain.ServerInfo;
import com.gloxandro.submuxic.service.MusicService;
import com.gloxandro.submuxic.service.MusicServiceFactory;
import com.gloxandro.submuxic.util.Constants;
import com.gloxandro.submuxic.util.LoadingTask;
import com.gloxandro.submuxic.util.ProgressListener;
import com.gloxandro.submuxic.util.UserUtil;
import com.gloxandro.submuxic.util.Util;
import com.gloxandro.submuxic.view.UpdateView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends SelectRecyclerFragment<Integer> {
	private static final String TAG = MainFragment.class.getSimpleName();
	public static final String SONGS_LIST_PREFIX = "songs-";
	public static final String SONGS_NEWEST = SONGS_LIST_PREFIX + "newest";
	public static final String SONGS_TOP_PLAYED = SONGS_LIST_PREFIX + "topPlayed";
	public static final String SONGS_RECENT = SONGS_LIST_PREFIX + "recent";
	public static final String SONGS_FREQUENT = SONGS_LIST_PREFIX + "frequent";
	protected static String theme;
	public MainFragment() {
		super();
		pullToRefresh = false;
		serialize = false;
		backgroundUpdate = false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.main, menu);
		onFinishSetupOptionsMenu(menu);

		try {
			if (!ServerInfo.canRescanServer(context) || !UserUtil.isCurrentAdmin()) {
				menu.setGroupVisible(R.id.rescan_server, false);
			}
		} catch (Exception e) {
			Log.w(TAG, "Error on setting madsonic invisible", e);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.menu_about:
				aboutdialog();
				return true;
			case R.id.whatsnew:
				showWhatsNewDialog();
				return true;
			case R.id.videos:
				showVideos();
				return true;
			case R.id.menu_rescan:
				rescanServer();
				return true;
		}

		return false;
	}

	private void aboutdialog() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());

		View view               = inflater.inflate(R.layout.about, null);


		WebView aboutWebView = (WebView) view.findViewById(R.id.aboutWebView);
		aboutWebView.setBackgroundColor(0);
			aboutWebView.loadUrl("file:///android_asset/about_light.html");


		androidx.appcompat.app.AlertDialog.Builder builder         = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

		builder.setView(view).setTitle(this.getString(R.string.menu_about))

				.setNegativeButton((R.string.common_ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void showWhatsNewDialog() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());

		View view               = inflater.inflate(R.layout.about, null);


		WebView aboutWebView = (WebView) view.findViewById(R.id.aboutWebView);
		aboutWebView.setBackgroundColor(0);
			aboutWebView.loadUrl("file:///android_asset/changelog_light.html");


		androidx.appcompat.app.AlertDialog.Builder builder         = new androidx.appcompat.app.AlertDialog.Builder(getActivity());

		builder.setView(view).setTitle(this.getString(R.string.whatsnew))
				.setNegativeButton((R.string.common_ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	@Override
	public int getOptionsMenu() {
		return 0;
	}

	@Override
	public SectionAdapter getAdapter(List objs) {
		List<List<Integer>> sections = new ArrayList<>();
		List<String> headers = new ArrayList<>();
		List<Integer> albums = new ArrayList<>();
		albums.add(R.string.main_albums_newest);
		albums.add(R.string.main_albums_random);
		if (ServerInfo.checkServerVersion(context, "1.8")) {
			albums.add(R.string.main_albums_alphabetical);
		}
		if (!Util.isTagBrowsing(context)) {
			albums.add(R.string.main_albums_highest);
		}
		albums.add(R.string.main_albums_starred);
		albums.add(R.string.main_albums_genres);
		albums.add(R.string.main_albums_year);
		albums.add(R.string.main_albums_recent);
		albums.add(R.string.main_albums_frequent);

		sections.add(albums);
		headers.add("albums");

		if (ServerInfo.isMadsonic6(context)) {
			List<Integer> songs = new ArrayList<>();

			songs.add(R.string.main_songs_newest);
			if (ServerInfo.checkServerVersion(context, "2.0.1")) {
				songs.add(R.string.main_songs_top_played);
			}
			songs.add(R.string.main_songs_recent);
			if (ServerInfo.checkServerVersion(context, "2.0.1")) {
				songs.add(R.string.main_songs_frequent);
			}

			sections.add(songs);
			headers.add("songs");
		}


		return new MainAdapter(context, headers, sections, this);
	}

	@Override
	public List<Integer> getObjects(MusicService musicService, boolean refresh, ProgressListener listener) throws Exception {
		return Arrays.asList(0);
	}

	@Override
	public int getTitleResource() {
		return R.string.common_appname;
	}

	private void showAlbumList(String type) {
		if ("genres".equals(type)) {
			SubsonicFragment fragment = new SelectGenreFragment();
			replaceFragment(fragment);
		} else if ("years".equals(type)) {
			SubsonicFragment fragment = new SelectYearFragment();
			replaceFragment(fragment);
		} else {
			// Clear out recently added count when viewing
			if ("newest".equals(type)) {
				SharedPreferences.Editor editor = Util.getPreferences(context).edit();
				editor.putInt(Constants.PREFERENCES_KEY_RECENT_COUNT + Util.getActiveServer(context), 0);
				editor.commit();
			}

			SubsonicFragment fragment = new SelectDirectoryFragment();
			Bundle args = new Bundle();
			args.putString(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE, type);
			args.putInt(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_SIZE, 20);
			args.putInt(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_OFFSET, 0);
			fragment.setArguments(args);

			replaceFragment(fragment);
		}
	}


	public void showVideos() {
		if (ServerInfo.checkServerVersion(context, "1.8")) {
			SubsonicFragment fragment = new SelectVideoFragment();
			replaceFragment(fragment);
		}

	}


	private void rescanServer() {
		new LoadingTask<Void>(context, false) {
			@Override
			protected Void doInBackground() throws Throwable {
				MusicService musicService = MusicServiceFactory.getMusicService(context);
				musicService.startRescan(context, this);
				return null;
			}

			@Override
			protected void done(Void value) {
				Util.toast(context, R.string.main_scan_complete);
			}
		}.execute();
	}




	@Override
	public void onItemClicked(UpdateView<Integer> updateView, Integer item) {
		if (item == R.string.main_albums_newest) {
			showAlbumList("newest");
		} else if (item == R.string.main_albums_random) {
			showAlbumList("random");
		} else if (item == R.string.main_albums_highest) {
			showAlbumList("highest");
		} else if (item == R.string.main_albums_recent) {
			showAlbumList("recent");
		} else if (item == R.string.main_albums_frequent) {
			showAlbumList("frequent");
		} else if (item == R.string.main_albums_starred) {
			showAlbumList("starred");
		} else if(item == R.string.main_albums_genres) {
			showAlbumList("genres");
		} else if(item == R.string.main_albums_year) {
			showAlbumList("years");
		} else if(item == R.string.main_albums_alphabetical) {
			showAlbumList("alphabeticalByName");
		} else if(item == R.string.main_videos) {
			showVideos();
		} else if (item == R.string.main_songs_newest) {
			showAlbumList(SONGS_NEWEST);
		} else if (item == R.string.main_songs_top_played) {
			showAlbumList(SONGS_TOP_PLAYED);
		} else if (item == R.string.main_songs_recent) {
			showAlbumList(SONGS_RECENT);
		} else if (item == R.string.main_songs_frequent) {
			showAlbumList(SONGS_FREQUENT);
		}
	}

	@Override
	public void onCreateContextMenu(Menu menu, MenuInflater menuInflater, UpdateView<Integer> updateView, Integer item) {}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem, UpdateView<Integer> updateView, Integer item) {
		return false;
	}
}
