/*
 * 
 * ProgressiveMuscleRelaxationLib
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

final class CaptionView extends TextView {
	private Paint mPaint;

	public CaptionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CaptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CaptionView(Context ctx) {
		super(ctx);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setColor(0xA0404040);
		mPaint.setStyle(Style.FILL);
		setTextColor(0xFFFFFFFF);
		setTextSize(18);
		setGravity(Gravity.CENTER);
		setPadding(30, 10, 30, 10);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getText() == null || getText().length() == 0) {
			return;
		}

		RectF r = new RectF(10, 0, getWidth() - 10, getHeight());
		mPaint.setAlpha(getPaint().getAlpha());
		canvas.drawRoundRect(r, 10, 10, mPaint);
		super.onDraw(canvas);
	}
}