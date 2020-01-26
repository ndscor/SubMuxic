package com.gloxandro.submuxic.view.compat;

import android.content.Context;
import android.os.Bundle;

import androidx.mediarouter.app.MediaRouteControllerDialog;
import androidx.mediarouter.app.MediaRouteControllerDialogFragment;

import com.gloxandro.submuxic.util.ThemeUtil;

public class CustomMediaRouteControllerDialogFragment extends MediaRouteControllerDialogFragment {
	@Override
	public MediaRouteControllerDialog onCreateControllerDialog(Context context, Bundle savedInstanceState) {
		return new MediaRouteControllerDialog(context, ThemeUtil.getThemeRes(context));
	}
}
