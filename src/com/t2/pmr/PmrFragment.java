package com.t2.pmr;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;

public class PmrFragment extends Fragment implements OnClickListener, AnimationListener, OnSharedPreferenceChangeListener, OnKeyListener {

	private static final Caption[] captions = Caption.values();

	private boolean mCaptionsEnabled;
	private MediaPlayer mAudioPlayer;
	private CaptionPlayer mCaptionPlayer;
	private long mStartTime;
	private long mCurrentTime;
	private int mAudioOffset;
	private int mCaptionIndex;
	private boolean mStarted, mPaused;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCaptionsEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_pmr_captions),
				true);
		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mStarted = savedInstanceState.getBoolean("started");
			if (mStarted) {
				mStartTime = savedInstanceState.getLong("start_time");
				mCurrentTime = savedInstanceState.getLong("current_time");
				mCaptionIndex = savedInstanceState.getInt("caption_index");
				mAudioOffset = savedInstanceState.getInt("audio_offset");
			}
		}

		getView().findViewById(R.id.lbl_start).setVisibility(mStarted ? View.GONE : View.VISIBLE);
		View wrapper = getView().findViewById(R.id.lay_pmr_wrapper);
		wrapper.setOnKeyListener(this);
		wrapper.setOnClickListener(this);
		wrapper.requestFocus();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getString(R.string.pref_pmr_captions))) {
			mCaptionsEnabled = sharedPreferences.getBoolean(key, true);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mStarted) {
			start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onAnimationEnd(Animation animation) {
		getView().findViewById(R.id.lbl_start).setVisibility(View.INVISIBLE);
		start();
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

	private void onStartOrPause() {
		if (mStarted) {
			if (mPaused) {
				mPaused = false;
				start();

			} else {
				pause(true);
				mPaused = true;
			}
			return;
		}
		mStarted = true;
		Animation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setFillAfter(true);
		anim.setDuration(2000);
		anim.setAnimationListener(this);
		getView().findViewById(R.id.lbl_start).startAnimation(anim);
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) && event.getAction() == KeyEvent.ACTION_UP) {
			onStartOrPause();
			return true;
		}
		return false;

	}

	public void onClick(View v) {
		onStartOrPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pmr_view, null);
	}

	@Override
	public void onPause() {
		super.onPause();

		pause(false);
	}

	private void pause(boolean clicked) {
		if (mPaused && clicked) {
			return;
		}

		if (mCaptionPlayer != null) {
			mCaptionPlayer.stop();
		}
		getPmrView().cancelAllRunnables();

		if (mAudioPlayer != null) {
			try {
				if (mAudioPlayer.isPlaying()) {
					Log.d("Bleh", "audio offset " + mAudioOffset);
					mAudioOffset = mAudioPlayer.getCurrentPosition();
					mCurrentTime = System.currentTimeMillis();
					mAudioPlayer.reset();
				}
			} catch (IllegalStateException e) {
			}

			mAudioPlayer.release();
		}

		if (clicked) {
			mStartTime = mCurrentTime - mAudioOffset;
			getPmrView().pause(mStartTime, mCurrentTime);
		}
		Log.d("Bleh", "pausing " + ((mCurrentTime - mStartTime) / 1000.0f) + " seconds in");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mStarted) {
			outState.putBoolean("started", mStarted);
			outState.putBoolean("paused", mPaused);
			outState.putLong("start_time", mStartTime);
			outState.putLong("current_time", System.currentTimeMillis());
			outState.putInt("caption_index", mCaptionIndex);
			if (mAudioPlayer != null) {
				if (mPaused) {
					outState.putInt("audio_offset", mAudioOffset);
				} else {
					outState.putInt("audio_offset", mAudioPlayer.getCurrentPosition());
				}
			}
		}
	}

	public void start() {
		if (mAudioPlayer != null) {
			try {
				mAudioPlayer.reset();
			} catch (IllegalStateException e) {
			}
			mAudioPlayer.release();
		}

		mAudioPlayer = new MediaPlayer();
		mAudioPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		});
		mAudioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});

		AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.pmr);

		if (fd != null) {
			try {
				mAudioPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
				fd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mAudioPlayer.setVolume(1, 1);

			mAudioPlayer.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {

					if (mAudioOffset > 0) {
						mAudioPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
							public void onSeekComplete(MediaPlayer mp) {
								if (mAudioPlayer.isPlaying()) {
									return;
								}
								mCaptionPlayer = new CaptionPlayer();
								mCaptionPlayer.start();
								mAudioPlayer.start();
								long now = System.currentTimeMillis();
								// This is done because we lose some time in the
								// recreation
								// shift
								mStartTime = mCurrentTime - mAudioPlayer.getCurrentPosition();
								getPmrView().start(mStartTime, mCurrentTime);
								Log.d("Bleh", "starting " + ((mCurrentTime - mStartTime) / 1000.0f) + " seconds in");
							}
						});
						mAudioPlayer.seekTo((int) (mAudioOffset));
					} else {
						mStartTime = System.currentTimeMillis();
						mCaptionPlayer = new CaptionPlayer();
						mCaptionPlayer.start();
						mAudioPlayer.start();
						getPmrView().start(0, 0);
					}
				}
			});
			mAudioPlayer.prepareAsync();
		}

	}

	private CaptionView getCaptionView() {
		if (getView() == null) {
			return null;
		}
		return (CaptionView) getView().findViewById(R.id.lbl_caption);
	}

	private PmrView getPmrView() {
		if (getView() == null) {
			return null;
		}
		return (PmrView) getView().findViewById(R.id.lay_pmr);
	}

	private final class CaptionPlayer implements Runnable {
		private Handler mHandler;
		private boolean mVisible;
		private String mCurrentCaption;
		private boolean mRestored;

		public synchronized void run() {
			if (getCaptionView() == null) {
				return;
			}

			if (!mVisible) {
				Caption c = captions[mCaptionIndex];
				mCurrentCaption = c.getText();
				if (mCaptionsEnabled) {
					getCaptionView().setText(mCurrentCaption);
					if (!mRestored) {
						// show it
						AlphaAnimation alpha = new AlphaAnimation(0, 1);
						alpha.setInterpolator(new LinearInterpolator());
						alpha.setDuration(500);
						alpha.setFillAfter(true);
						alpha.setFillBefore(true);
						alpha.setFillEnabled(true);
						getCaptionView().startAnimation(alpha);
					} else {
						getCaptionView().setVisibility(View.VISIBLE);
					}
				}
				mRestored = false;
				mVisible = true;
				mHandler.postDelayed(this, c.getEndOffset() - mAudioPlayer.getCurrentPosition());
			} else {
				// hide it
				if (mCaptionsEnabled) {
					AlphaAnimation alpha = new AlphaAnimation(1, 0);
					alpha.setInterpolator(new LinearInterpolator());
					alpha.setDuration(500);
					alpha.setFillAfter(true);
					alpha.setFillBefore(true);
					alpha.setFillEnabled(true);
					getCaptionView().startAnimation(alpha);
				} else {
					getCaptionView().setVisibility(View.INVISIBLE);
				}

				mVisible = false;
				mCaptionIndex++;

				// check if we are beyond our bounds yet..if so, stop this
				if (mCaptionIndex >= captions.length)
				{
					stop();
				}
				else
				{
					Caption c = captions[mCaptionIndex];
					mHandler.postDelayed(this, c.getStartOffset() - mAudioPlayer.getCurrentPosition());
				}
			}
		}

		public void start() {
			mHandler = getPmrView().getHandler();
			mVisible = false;

			if (mCaptionIndex >= captions.length) {
				return;
			}

			Caption cap = captions[mCaptionIndex];
			if (mStartTime + cap.getStartOffset() <= System.currentTimeMillis()) {
				mRestored = true;
				run();
			} else {
				mHandler.postDelayed(this, cap.getStartOffset() - (System.currentTimeMillis() - mStartTime));
			}
		}

		public void stop() {
			if (mHandler != null) {
				mHandler.removeCallbacks(this);
				mHandler = null;
			}
		}
	}

}