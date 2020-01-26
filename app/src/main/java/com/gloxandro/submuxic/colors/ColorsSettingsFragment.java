package com.gloxandro.submuxic.colors;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.util.Util;
import com.google.android.material.appbar.AppBarLayout;

import static com.gloxandro.submuxic.util.Util.KEY_BOTTOMNAVIGATIONBAR_COLOR;
import static com.gloxandro.submuxic.util.Util.KEY_PRIMARY_COLOR;
import static com.gloxandro.submuxic.util.Util.KEY_STATUS_COLOR;

public class ColorsSettingsFragment extends PreferenceFragment
		implements OnPreferenceClickListener, OnPreferenceChangeListener {

	private Preference preference;

	public ColorsSettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_appearance);
		//General
		//Theme
		Preference preferencePrimaryColor = findPreference(KEY_PRIMARY_COLOR);
		Preference preferenceStatusColor = findPreference(KEY_STATUS_COLOR);
		Preference preferencesNavigationColor = findPreference(KEY_BOTTOMNAVIGATIONBAR_COLOR);
		preferenceStatusColor.setOnPreferenceChangeListener(this);
		preferencesNavigationColor.setOnPreferenceChangeListener(this);
		preferencePrimaryColor.setOnPreferenceChangeListener(this);
		preferencePrimaryColor.setOnPreferenceClickListener(this);




		//Advanced

	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
			getActivity().recreate();
			return true;

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference instanceof PreferenceScreen ) {
			setUpNestedScreen((PreferenceScreen) preference);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
		final Dialog dialog = preferenceScreen.getDialog();

		View listRoot = dialog.findViewById(android.R.id.list);
		LinearLayout root = null;
		try {
			root = (LinearLayout) listRoot.getParent();
		} catch (Exception e) {
			try {
				root = (LinearLayout) listRoot.getParent().getParent();
			} catch (Exception e1) {
				try {
					root = (LinearLayout) listRoot.getParent().getParent().getParent();
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
		}
		if (null == root) {
			return;
		}
		AppBarLayout appBar = (AppBarLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.toolbar, root, false);
		root.addView(appBar, 0);

		Toolbar toolbar = (Toolbar) appBar.getChildAt(0);
		toolbar.setTitle(preferenceScreen.getTitle());
		int color = Util.getPrimaryColor(getActivity());
		toolbar.setBackgroundColor(color);
		int statusBarColor = Constants.getStatusBarColor(Util.getPrimaryColor(getActivity()));
			dialog.getWindow().setStatusBarColor(statusBarColor);

		}
	}
