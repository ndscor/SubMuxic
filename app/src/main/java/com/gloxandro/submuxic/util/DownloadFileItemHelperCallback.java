package com.gloxandro.submuxic.util;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gloxandro.submuxic.adapter.SectionAdapter;
import com.gloxandro.submuxic.fragments.SubsonicFragment;
import com.gloxandro.submuxic.service.DownloadFile;
import com.gloxandro.submuxic.service.DownloadService;
import com.gloxandro.submuxic.view.SongView;
import com.gloxandro.submuxic.view.UpdateView;

import org.eclipse.jetty.util.ArrayQueue;

import java.util.Queue;

public class DownloadFileItemHelperCallback extends ItemTouchHelper.SimpleCallback {
	private static final String TAG = DownloadFileItemHelperCallback.class.getSimpleName();

	private SubsonicFragment fragment;
	private boolean mainList;

	private BackgroundTask pendingTask = null;
	private Queue pendingOperations = new ArrayQueue();

	public DownloadFileItemHelperCallback(SubsonicFragment fragment, boolean mainList) {
		super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		this.fragment = fragment;
		this.mainList = mainList;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder fromHolder, RecyclerView.ViewHolder toHolder) {
		int from = fromHolder.getAdapterPosition();
		int to = toHolder.getAdapterPosition();
		getSectionAdapter().moveItem(from, to);

		synchronized (pendingOperations) {
			pendingOperations.add(new Pair<>(from, to));
			updateDownloadService();
		}
		return true;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		SongView songView = (SongView) ((UpdateView.UpdateViewHolder) viewHolder).getUpdateView();
		DownloadFile downloadFile = songView.getDownloadFile();

		getSectionAdapter().removeItem(downloadFile);
		synchronized (pendingOperations) {
			pendingOperations.add(downloadFile);
			updateDownloadService();
		}
	}

	public DownloadService getDownloadService() {
		return fragment.getDownloadService();
	}
	public SectionAdapter getSectionAdapter() {
		return fragment.getCurrentAdapter();
	}

	private void updateDownloadService() {
		if(pendingTask == null) {
			final DownloadService downloadService = getDownloadService();
			if(downloadService == null) {
				return;
			}

			pendingTask = new SilentBackgroundTask<Void>(downloadService) {
				@Override
				protected Void doInBackground() throws Throwable {
					boolean running = true;
					while(running) {
						Object nextOperation = null;
						synchronized (pendingOperations) {
							if(!pendingOperations.isEmpty()) {
								nextOperation = pendingOperations.remove();
							}
						}

						if(nextOperation != null) {
							if(nextOperation instanceof Pair) {
								Pair<Integer, Integer> swap = (Pair) nextOperation;
								downloadService.swap(mainList, swap.getFirst(), swap.getSecond());
							} else if(nextOperation instanceof DownloadFile) {
								DownloadFile downloadFile = (DownloadFile) nextOperation;
								if(mainList) {
									downloadService.remove(downloadFile);
								} else {
									downloadService.removeBackground(downloadFile);
								}
							}
						} else {
							running = false;
						}
					}

					synchronized (pendingOperations) {
						pendingTask = null;

						// Start a task if this is non-empty.  Means someone added while we were running operations
						if(!pendingOperations.isEmpty()) {
							updateDownloadService();
						}
					}
					return null;
				}
			};
			pendingTask.execute();
		}
	}
}
