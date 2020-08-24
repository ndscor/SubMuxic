package com.gloxandro.submuxic.util;

import android.view.View;

import com.gloxandro.submuxic.activity.SubsonicFragmentActivity;
import com.gloxandro.submuxic.fragments.SubsonicFragment;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public abstract class TabBackgroundTask<T> extends BackgroundTask<T> {

    private final SubsonicFragment tabFragment;

    public TabBackgroundTask(SubsonicFragment fragment) {
        super(fragment.getActivity());
        tabFragment = fragment;
    }

    @Override
    public void execute() {
		queue.offer(task = new Task() {
			@Override
			public void onDone(T result) {
				done(result);
			}

			@Override
			public void onError(Throwable t) {
				error(t);
			}
		});
    }

	@Override
    public boolean isCancelled() {
        return !tabFragment.isAdded() || cancelled.get();
    }

    @Override
    public void updateProgress(final String message) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                tabFragment.updateProgress(message);
            }
        });
    }
}
