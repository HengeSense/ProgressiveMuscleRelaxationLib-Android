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