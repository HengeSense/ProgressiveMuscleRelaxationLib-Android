package com.t2.pmr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class KeyframeView extends FrameLayout {

	protected Handler mHandler;
	protected long mAnimationStart;
	protected List<CancelableRunnable> mRunnables = new ArrayList<CancelableRunnable>();

	public KeyframeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public KeyframeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KeyframeView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mHandler = new Handler();
	}

	public class CancelableRunnable implements Runnable {
		protected boolean mCanceled;

		public CancelableRunnable() {
			mRunnables.add(this);
		}

		public void run() {
			mRunnables.remove(this);
		}
	}

	public boolean postAtTime(long time, CancelableRunnable action) {
		return getHandler().postDelayed(action, time);
	}

	public boolean postAtTimeFromStart(double relativeTime, CancelableRunnable action) {
		Log.d("Bleh", "Scheduled for " + relativeTime + " seconds");
		return postAtTime((long) (relativeTime * 1000), action);
	}

	public void cancelAllRunnables() {
		for (CancelableRunnable r : mRunnables) {
			r.mCanceled = true;
			mHandler.removeCallbacks(r);
		}
		mRunnables.clear();
	}

	@Override
	public Handler getHandler() {
		return mHandler;
	}

	@Override
	protected void onDetachedFromWindow() {
		cancelAllRunnables();
		super.onDetachedFromWindow();
	}

}
