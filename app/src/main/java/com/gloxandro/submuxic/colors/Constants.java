/*
 * Andoku - a sudoku puzzle game for Android.
 * Copyright (C) 2009  Markus Wiederkehr
 *
 * This file is part of Andoku.
 *
 * Andoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Andoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Andoku.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gloxandro.submuxic.colors;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;

public class Constants {
	private static final String PREFIX = Constants.class.getPackage().getName() + ".";


	public static final boolean LOG_V = false;
	public static int dpToPx(int dp) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_MEDIUM);
		return Math.round(px);
	}
	public static int getStatusBarColor(int color1) {
		int color2 = Color.parseColor("#000000");
		return blendColors(color1, color2, 0.9f);
	}
	public static int blendColors(int color1, int color2, float ratio) {
		final float inverseRation = 1f - ratio;
		float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
		float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
		float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
		return Color.rgb((int) r, (int) g, (int) b);
	}

	private Constants() {
	}
}
