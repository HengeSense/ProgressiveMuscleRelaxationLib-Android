/*
 * 
 * ProgressiveMuscleRelaxationLib
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: ProgressiveMuscleRelaxationLib001
 * Government Agency Original Software Title: ProgressiveMuscleRelaxationLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
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
