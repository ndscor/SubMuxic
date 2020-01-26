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
	Copyright 2016 (C) Scott Jackson
*/

package com.gloxandro.submuxic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.gloxandro.submuxic.R;
import com.gloxandro.submuxic.activity.SettingsActivity;
import com.gloxandro.submuxic.activity.SubsonicFragmentActivity;

import java.util.Locale;

public final class ThemeUtil {
	public static final String THEME_DARK = "dark";

	public static String getTheme(Context context) {
		SharedPreferences prefs = Util.getPreferences(context);
		String theme = prefs.getString(Constants.PREFERENCES_KEY_THEME, THEME_DARK);


		return theme;
	}
	public static int getThemeRes(Context context) {
		return getThemeRes(context, getTheme(context));
	}
	public static int getThemeRes(Context context, String theme) {
		if(context instanceof SubsonicFragmentActivity || context instanceof SettingsActivity) {
				if (THEME_DARK.equals(theme)) {
					return R.style.Theme_SubMuxic_Dark_No_Color;
				} else {
					return R.style.Theme_SubMuxic_Light_No_Color;
				}
		} else {
			if (THEME_DARK.equals(theme)) {
				return R.style.Theme_SubMuxic_Dark;
			} else {
				return R.style.Theme_SubMuxic_Light;
			}
		}
	}

	public static void setTheme(Context context, String theme) {
		SharedPreferences.Editor editor = Util.getPreferences(context).edit();
		editor.putString(Constants.PREFERENCES_KEY_THEME, theme);
		editor.commit();
	}



	public static void applyTheme(Context context, String theme) {
		context.setTheme(getThemeRes(context, theme));

		SharedPreferences prefs = Util.getPreferences(context);
		if(prefs.getBoolean(Constants.PREFERENCES_KEY_OVERRIDE_SYSTEM_LANGUAGE, false)) {
			Configuration config = new Configuration();
			config.locale = Locale.ENGLISH;
			context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		}
	}
}
