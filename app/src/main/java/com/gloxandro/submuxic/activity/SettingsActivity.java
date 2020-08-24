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
package com.gloxandro.submuxic.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.fragments.PreferenceCompatFragment;
import com.gloxandro.submuxic.fragments.SettingsFragment;
import com.gloxandro.submuxic.util.Constants;
import com.gloxandro.submuxic.util.DrawableTint;
import com.gloxandro.submuxic.util.ThemeUtil;
import com.gloxandro.submuxic.util.Util;
import com.gloxandro.submuxic.view.UpdateView;

import static com.gloxandro.submuxic.util.ThemeUtil.THEME_BLACK;
import static com.gloxandro.submuxic.util.ThemeUtil.THEME_BLUE;
import static com.gloxandro.submuxic.util.ThemeUtil.THEME_DARK;
import static com.gloxandro.submuxic.util.ThemeUtil.THEME_LIGHT;

public class SettingsActivity extends SubsonicActivity {
	private static final String TAG = SettingsActivity.class.getSimpleName();
	private PreferenceCompatFragment fragment;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		applyTheme();
		super.onCreate(savedInstanceState);
		if (THEME_LIGHT.equals(theme)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().setStatusBarColor(Color.TRANSPARENT);  // transparent
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

			}
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(0x00000000);  // transparent
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

		}
		lastSelectedPosition = R.id.drawer_settings;
		setContentView(R.layout.settings_activity);

		if (savedInstanceState == null) {
			fragment = new SettingsFragment();
			Bundle args = new Bundle();
			args.putInt(Constants.INTENT_EXTRA_FRAGMENT_TYPE, R.xml.settings);

			fragment.setArguments(args);
			fragment.setRetainInstance(true);

			currentFragment = fragment;
			currentFragment.setPrimaryFragment(true);
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment, currentFragment.getSupportTag() + "").commit();
		}

		Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(mainToolbar);
	}




	@Override
	protected void onResume() {
		super.onResume();
		applyTheme();
		// If this is in onStart is causes crashes when rotating screen in offline mode
		// Actual root cause of error is `drawerItemSelected(newFragment);` in the offline mode branch of code
	}


	@Override
	public void onBackPressed()
	{
		recreate();
		super.onBackPressed();  // optional depending on your needs
	}



	@Override
	protected void onStart() {
		super.onStart();
		Util.registerMediaButtonEventReceiver(this);

		// Make sure to update theme
		if (theme != null && !theme.equals(ThemeUtil.getTheme(this)) ) {
			restart();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			DrawableTint.clearCache();
			return;
		}

		getImageLoader().onUIVisible();
		UpdateView.addActiveActivity();
	}

	public void setStatusColor() {

		int color = Util.getStatusColor(this);
		getWindow().setStatusBarColor(color);

	}



	public void setUpNavigation() {


		int color = Util.getbottomnavigationColor(this);
		getWindow().setNavigationBarColor(color);
	}



	private void applyTheme() {
		theme = ThemeUtil.getTheme(this);
		ThemeUtil.applyTheme(this, theme);
	}

}
