package com.t2.pmr;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PmrView extends KeyframeView {

	private ImageView mBodyImage;
	private ImageView mHighlightImage;
	private PmrViewState mState;

	private long mRestoredTime;

	public PmrView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PmrView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PmrView(Context context) {
		super(context);
		init();
	}

	public void focusBodyPart(final int rId, final float at) {

		postAtTimeFromStart(at, new CancelableRunnable() {
			@Override
			public void run() {
				if (mCanceled) {
					return;
				}
				mState.mHighlightResourceId = rId;

				Log.d("Bleh", "focusing " + getResources().getResourceEntryName(rId));
				AnimationSet backgroundAnimation = new AnimationSet(true);
				AnimationSet highlightAnimation = new AnimationSet(true);

				int width = getMeasuredWidth();
				int height = getMeasuredHeight();
				float scale = getScale(rId);
				PointF center = getCenter(rId);
				ScaleAnimation scaleAnim = new ScaleAnimation(1, scale, 1, scale);
				TranslateAnimation panAnim = new TranslateAnimation(0, (0.5f * width) - (center.x * width * scale), 0,
						(0.5f * height) - (center.y * height * scale));
				AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
				backgroundAnimation.addAnimation(scaleAnim);
				backgroundAnimation.addAnimation(panAnim);
				backgroundAnimation.setDuration(8000);
				backgroundAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
				backgroundAnimation.setFillAfter(true);
				backgroundAnimation.setFillBefore(true);
				backgroundAnimation.setFillEnabled(true);
				mBodyImage.startAnimation(backgroundAnimation);

				highlightAnimation.addAnimation(scaleAnim);
				highlightAnimation.addAnimation(panAnim);
				highlightAnimation.addAnimation(alpha);
				highlightAnimation.setDuration(8000);
				highlightAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
				highlightAnimation.setFillAfter(true);
				highlightAnimation.setFillBefore(true);
				highlightAnimation.setFillEnabled(true);
				mHighlightImage.setImageResource(rId);
				mHighlightImage.setVisibility(View.VISIBLE);
				mHighlightImage.startAnimation(highlightAnimation);

				super.run();
			}
		});
	}

	public void unfocusBodyPart(final int rId, final float at) {
		postAtTimeFromStart(at, new CancelableRunnable() {

			@Override
			public void run() {
				if (mCanceled) {
					return;
				}

				final PointF center = getCenter(rId);
				final float scale = getScale(rId);

				Log.d("Bleh", "scale " + scale);

				Log.d("Bleh", "unfocusing");
				AnimationSet backgroundAnimation = new AnimationSet(true);
				AnimationSet highlightAnimation = new AnimationSet(true);

				int width = getMeasuredWidth();
				int height = getMeasuredHeight();

				ScaleAnimation scaleAnim = new ScaleAnimation(scale, 1, scale, 1);
				TranslateAnimation panAnim = new TranslateAnimation((0.5f * width) - (center.x * width * scale), 0, (0.5f * height)
						- (center.y * height * scale), 0);
				AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);

				mState.mUnfocusing = true;
				backgroundAnimation.addAnimation(scaleAnim);
				backgroundAnimation.addAnimation(panAnim);
				backgroundAnimation.setDuration(5000);
				backgroundAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
				backgroundAnimation.setFillAfter(true);
				backgroundAnimation.setFillBefore(true);
				backgroundAnimation.setFillEnabled(true);
				backgroundAnimation.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						mState.mUnfocusing = false;
						mState.mHighlightResourceId = 0;
					}
				});
				mBodyImage.startAnimation(backgroundAnimation);

				highlightAnimation.addAnimation(scaleAnim);
				highlightAnimation.addAnimation(panAnim);
				highlightAnimation.addAnimation(alpha);
				highlightAnimation.setDuration(5000);
				highlightAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
				highlightAnimation.setFillAfter(true);
				highlightAnimation.setFillBefore(true);
				highlightAnimation.setFillEnabled(true);
				mHighlightImage.setVisibility(View.VISIBLE);
				mHighlightImage.startAnimation(highlightAnimation);

				super.run();
			}
		});

	}

	public void start(long startTime, long restoredTime) {
		float timeMod = 0;
		if (restoredTime > 0) {
			mRestoredTime = restoredTime;
			mAnimationStart = startTime;
			timeMod = (mRestoredTime - mAnimationStart) / 1000.0f;
			Log.d("Bleh", "timemod " + timeMod);
		} else {
			mAnimationStart = System.currentTimeMillis();
		}

		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();

		final float armsFocus = 62 - timeMod;
		final float headFocus = 115 - timeMod;
		final float shouldersFocus = 184 - timeMod;
		final float stomachFocus = 245 - timeMod;
		final float buttFocus = 294 - timeMod;
		final float feetFocus = 357 - timeMod;
		final float armsUnfocus = 102 - timeMod;
		final float headUnfocus = 175 - timeMod;
		final float shoulderUnfocus = 236 - timeMod;
		final float stomachUnfocus = 282 - timeMod;
		final float buttUnfocus = 337 - timeMod;
		final float feetUnfocus = 411 - timeMod;
		final float finalUnfocus = 458 - timeMod;

		if (armsFocus > 0) {
			focusBodyPart(R.drawable.arms_highlight, armsFocus);
		}

		if (armsUnfocus > 0) {
			unfocusBodyPart(R.drawable.arms_highlight, armsUnfocus);
		}

		if (headFocus > 0) {
			focusBodyPart(R.drawable.head_highlight, headFocus);
		}
		if (headUnfocus > 0) {
			unfocusBodyPart(R.drawable.head_highlight, headUnfocus);
		}

		if (shouldersFocus > 0) {
			focusBodyPart(R.drawable.shoulders_highlight, shouldersFocus);
		}
		if (shoulderUnfocus > 0) {
			unfocusBodyPart(R.drawable.shoulders_highlight, shoulderUnfocus);
		}

		if (stomachFocus > 0) {
			focusBodyPart(R.drawable.stomach_highlight, stomachFocus);
		}
		if (stomachUnfocus > 0) {
			unfocusBodyPart(R.drawable.stomach_highlight, stomachUnfocus);
		}
		if (buttFocus > 0) {
			focusBodyPart(R.drawable.butt_highlight, buttFocus);
		}
		if (buttUnfocus > 0) {
			unfocusBodyPart(R.drawable.butt_highlight, buttUnfocus);
		}
		if (feetFocus > 0) {
			focusBodyPart(R.drawable.feet_highlight, feetFocus);
		}
		if (feetUnfocus > 0) {
			unfocusBodyPart(R.drawable.feet_highlight, feetUnfocus);
		}
		if (finalUnfocus > 0) {
			postAtTimeFromStart(finalUnfocus, new CancelableRunnable() {
				@Override
				public void run() {
					if (mCanceled) {
						return;
					}
					mState.mHighlightResourceId = R.drawable.body_highlight;
					mHighlightImage.setImageResource(R.drawable.body_highlight);

					AlphaAnimation alpha = new AlphaAnimation(0, 1);
					alpha.setInterpolator(new AccelerateDecelerateInterpolator());
					alpha.setDuration(20000);
					alpha.setFillAfter(true);
					alpha.setFillBefore(true);
					alpha.setFillEnabled(true);
					alpha.setZAdjustment(Animation.ZORDER_TOP);
					mHighlightImage.setVisibility(VISIBLE);
					mHighlightImage.startAnimation(alpha);

					super.run();
				}
			});
		}

		if (mState.mHighlightResourceId > 0) {
			restoreAnimation(timeMod, true);
		} else {
			mBodyImage.setVisibility(VISIBLE);
		}

	}

	private void restoreAnimation(float timeMod, boolean start) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();

		final float armsFocus = 62 - timeMod;
		final float headFocus = 115 - timeMod;
		final float shouldersFocus = 184 - timeMod;
		final float stomachFocus = 245 - timeMod;
		final float buttFocus = 294 - timeMod;
		final float feetFocus = 357 - timeMod;
		final float armsUnfocus = 102 - timeMod;
		final float headUnfocus = 175 - timeMod;
		final float shoulderUnfocus = 236 - timeMod;
		final float stomachUnfocus = 282 - timeMod;
		final float buttUnfocus = 337 - timeMod;
		final float feetUnfocus = 411 - timeMod;
		final float finalUnfocus = 458 - timeMod;

		float maxScale = getScale(mState.mHighlightResourceId);
		PointF center = getCenter(mState.mHighlightResourceId);
		float time = 0;
		if (mState.mHighlightResourceId == R.drawable.arms_highlight) {
			time = mState.mUnfocusing ? armsUnfocus : armsFocus;
		} else if (mState.mHighlightResourceId == R.drawable.head_highlight) {
			time = mState.mUnfocusing ? headUnfocus : headFocus;
		} else if (mState.mHighlightResourceId == R.drawable.shoulders_highlight) {
			time = mState.mUnfocusing ? shoulderUnfocus : shouldersFocus;
		} else if (mState.mHighlightResourceId == R.drawable.stomach_highlight) {
			time = mState.mUnfocusing ? stomachUnfocus : stomachFocus;
		} else if (mState.mHighlightResourceId == R.drawable.butt_highlight) {
			time = mState.mUnfocusing ? buttUnfocus : buttFocus;
		} else if (mState.mHighlightResourceId == R.drawable.feet_highlight) {
			time = mState.mUnfocusing ? feetUnfocus : feetFocus;
		}

		AnimationSet backgroundAnimation = new AnimationSet(true);
		AnimationSet highlightAnimation = new AnimationSet(true);

		final float dur = mState.mUnfocusing ? 5 : 8;

		float animLeft = dur + time;
		final float animPerc = Math.abs(1 - (animLeft / dur));
		float actualScale = mState.mUnfocusing ? 1 + (((maxScale - 1) * Math.abs(1 - animPerc))) : 1 + (animPerc * (maxScale - 1));
		long duration = 0;

		float startScale = maxScale;
		float endScale = maxScale;
		float actX = (0.5f * width) - (center.x * width * actualScale);
		float actY = (0.5f * height) - (center.y * height * actualScale);
		float endX = (0.5f * width) - (center.x * width * maxScale);
		float endY = (0.5f * height) - (center.y * height * maxScale);
		float startX = endX;
		float startY = endY;
		float startAlpha = 1;
		float endAlpha = 1;

		if (animLeft > 0) {
			duration = (long) (animLeft * 1000);
			startScale = actualScale;
			endScale = maxScale;
			startX = actX;
			startY = actY;
			startAlpha = animPerc;
			endAlpha = 1;
			if (mState.mUnfocusing) {
				startX = actX;
				endX = 0;
				startY = actY;
				endY = 0;
				startScale = actualScale;
				endScale = 1;
				endAlpha = 0;
				startAlpha = Math.abs(1 - animPerc);
			}

		}

		// if (mPauseScaleX > 0) {
		// float[] vals = new float[9];
		// mBaseMatrix.getValues(vals);
		// startScale = (1.0f / vals[Matrix.MSCALE_X]) * mPauseScaleX;
		// startX = -mPauseTransX;
		// startY = -vals[Matrix.MTRANS_Y] + mPauseTransY;
		//
		// System.out.println(String.format("%.3f, %.3f, %.3f", mPauseTransX,
		// vals[Matrix.MTRANS_X], actX));
		//
		// startAlpha = mPauseAlpha;
		//
		// mPauseTransY = 0;
		// mPauseTransX = 0;
		// mPauseScaleX = 0;
		// mPauseAlpha = 0;
		// }

		mBodyImage.setImageMatrix(null);
		mBodyImage.setScaleType(ScaleType.FIT_CENTER);
		System.out.println(mBodyImage.getImageMatrix());
		mHighlightImage.setAlpha(255);
		mHighlightImage.setImageMatrix(null);
		mHighlightImage.setScaleType(ScaleType.FIT_CENTER);

		Log.d("Bleh", "perc " + animPerc + ", scale " + actualScale + ", max " + maxScale);
		Log.d("Bleh", "endX " + endX + ", endY " + endY + ", startX " + startX + ", startY " + startY);

		ScaleAnimation scaleAnim = new ScaleAnimation(startScale, endScale, startScale, endScale);
		TranslateAnimation panAnim = new TranslateAnimation(startX, endX,
				startY, endY);
		AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
		backgroundAnimation.addAnimation(scaleAnim);
		backgroundAnimation.addAnimation(panAnim);
		backgroundAnimation.setDuration(duration);
		backgroundAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		backgroundAnimation.setFillAfter(true);
		backgroundAnimation.setFillBefore(true);
		backgroundAnimation.setFillEnabled(true);
		if (mState.mUnfocusing) {
			backgroundAnimation.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mState.mUnfocusing = false;
					mState.mHighlightResourceId = 0;
				}
			});
		}
		if (start) {
			mBodyImage.startAnimation(backgroundAnimation);
		} else {
			mBodyImage.setAnimation(backgroundAnimation);
			// mBodyImage.invalidate();
		}
		mBodyImage.setVisibility(VISIBLE);

		highlightAnimation.addAnimation(scaleAnim);
		highlightAnimation.addAnimation(panAnim);
		highlightAnimation.addAnimation(alpha);
		highlightAnimation.setDuration(duration);
		highlightAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		highlightAnimation.setFillAfter(true);
		highlightAnimation.setFillBefore(true);
		highlightAnimation.setFillEnabled(true);
		mHighlightImage.setImageResource(mState.mHighlightResourceId);
		mHighlightImage.setVisibility(View.VISIBLE);
		if (start) {
			mHighlightImage.startAnimation(highlightAnimation);
		} else {
			mHighlightImage.setAnimation(highlightAnimation);
			// mHighlightImage.invalidate();
		}
	}

	private Matrix mBaseMatrix;
	private float mPauseScaleX;
	private float mPauseTransX;
	private float mPauseTransY;
	private float mPauseAlpha;

	public void pause(long startTime, long pauseTime) {
		if (mBodyImage.getAnimation() == null) {
			return;
		}

		int highlight = mState.mHighlightResourceId;
		boolean unfocusing = mState.mUnfocusing;
		Transformation tfn = new Transformation();
		// long timeMod = (long) pauseTime - mAnimationStart;
		mBodyImage.getAnimation().getTransformation(AnimationUtils.currentAnimationTimeMillis(), tfn);
		Matrix matrix = mBodyImage.getImageMatrix();
		mBodyImage.clearAnimation();
		mBodyImage.setScaleType(ScaleType.MATRIX);

		mBaseMatrix = new Matrix(matrix);
		System.out.println(mBaseMatrix);
		final float[] f = new float[9];
		matrix.getValues(f);
		System.out.println(tfn);
		System.out.println(String.format("PreTrans %.3f", f[Matrix.MTRANS_X]));
		Matrix pauseMatrix = new Matrix(tfn.getMatrix());
		pauseMatrix.preTranslate(f[Matrix.MTRANS_X], f[Matrix.MTRANS_Y]);
		pauseMatrix.preScale(f[Matrix.MSCALE_X], f[Matrix.MSCALE_Y], 0.5f, 0.5f);

		pauseMatrix.getValues(f);
		mPauseScaleX = f[Matrix.MSCALE_X];
		mPauseTransX = f[Matrix.MTRANS_X];
		mPauseTransY = f[Matrix.MTRANS_Y];
		mBodyImage.setImageMatrix(pauseMatrix);

		Transformation hTfn = new Transformation();
		mHighlightImage.getAnimation().getTransformation(AnimationUtils.currentAnimationTimeMillis(), hTfn);
		mPauseAlpha = hTfn.getAlpha();
		mHighlightImage.setAlpha((int) (mPauseAlpha * 255));
		mHighlightImage.clearAnimation();
		hTfn.getMatrix().set(pauseMatrix);
		mHighlightImage.setScaleType(ScaleType.MATRIX);
		mHighlightImage.setImageMatrix(hTfn.getMatrix());

		mState.mHighlightResourceId = highlight;
		mState.mUnfocusing = unfocusing;
		System.out.println(tfn);
	}

	private PointF getCenter(int rId) {
		if (rId == R.drawable.arms_highlight) {
			return new PointF(0.5f, 0.45f);
		} else if (rId == R.drawable.head_highlight) {
			return new PointF(0.5f, 0.16f);
		} else if (rId == R.drawable.shoulders_highlight) {
			return new PointF(0.5f, 0.235f);
		} else if (rId == R.drawable.stomach_highlight) {
			return new PointF(0.5f, 0.425f);
		} else if (rId == R.drawable.butt_highlight) {
			return new PointF(0.5f, 0.55f);
		} else if (rId == R.drawable.feet_highlight) {
			return new PointF(0.5f, 0.85f);
		} else {
			return new PointF(0.5f, 0.5f);
		}
	}

	private float getScale(int rId) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		final float scalar = Math.min(width / 600.0f, height / 800.0f);

		if (rId == R.drawable.arms_highlight) {
			return Math.min(width / (430.0f * scalar), height / (300.0f * scalar));
		} else if (rId == R.drawable.head_highlight) {
			return Math.min(width / (155.0f * scalar), height / (155.0f * scalar));
		} else if (rId == R.drawable.shoulders_highlight) {
			return Math.min(width / (300.0f * scalar), height / (170.0f * scalar));
		} else if (rId == R.drawable.stomach_highlight) {
			return Math.min(width / (270.0f * scalar), height / (210.0f * scalar));
		} else if (rId == R.drawable.butt_highlight) {
			return Math.min(width / (250.0f * scalar), height / (315.0f * scalar));
		} else if (rId == R.drawable.feet_highlight) {
			return Math.min(width / (270.0f * scalar), height / (315.0f * scalar));
		} else {
			return 1;
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof PmrViewState) {
			super.onRestoreInstanceState(((PmrViewState) state).getSuperState());
			mState = ((PmrViewState) state);
			if (mState.mStarted) {
				mBodyImage.setVisibility(INVISIBLE);
			}
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable state = super.onSaveInstanceState();
		PmrViewState saveState = new PmrViewState(state);
		saveState.mHighlightResourceId = mState.mHighlightResourceId;
		saveState.mUnfocusing = mState.mUnfocusing;
		saveState.mStarted = mState.mStarted;
		return saveState;
	}

	private static final class PmrViewState extends BaseSavedState {

		private int mHighlightResourceId;
		private boolean mUnfocusing;
		private boolean mStarted;

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<PmrViewState> CREATOR =
				new Parcelable.Creator<PmrViewState>() {
					public PmrViewState createFromParcel(Parcel source) {
						return new PmrViewState(source);
					}

					public PmrViewState[] newArray(int size) {
						return new PmrViewState[size];
					}
				};

		private PmrViewState() {
			super(Parcel.obtain());
		}

		private PmrViewState(Parcel in) {
			super(in);
			mHighlightResourceId = in.readInt();
			mUnfocusing = in.readInt() == 1;
			mStarted = in.readInt() == 1;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mHighlightResourceId);
			dest.writeInt(mUnfocusing ? 1 : 0);
			dest.writeInt(mStarted ? 1 : 0);
		}

		private PmrViewState(Parcelable parcelable) {
			super(parcelable);
		}

	}

	private void init() {
		mBodyImage = new ImageView(getContext());
		mBodyImage.setImageResource(R.drawable.body);
		addView(mBodyImage, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mHighlightImage = new ImageView(getContext());
		mHighlightImage.setImageResource(R.drawable.arms_highlight);
		mHighlightImage.setVisibility(INVISIBLE);
		addView(mHighlightImage, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		mState = new PmrViewState();
	}
}
