/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.ttkpulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T> {

	// ===========================================================
	// Constants
	// ===========================================================

	static final boolean DEBUG = false;

	static final String LOG_TAG = "PullToRefresh";

	static final float FRICTION = 2.0f;

	public static final int SMOOTH_SCROLL_DURATION_MS = 200;
	public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;

	static final int WAITING = 0x0;
	static final int PULL_TO_REFRESH = 0x1;
	static final int RELEASE_TO_REFRESH = 0x2;
	static final int REFRESHING = 0x8;
	static final int MANUAL_REFRESHING = 0x9;

	static final Mode DEFAULT_MODE = Mode.PULL_DOWN_TO_REFRESH;
	static final int INITIAL_STATE = WAITING;

	static final String STATE_STATE = "ptr_state";
	static final String STATE_MODE = "ptr_mode";
	static final String STATE_CURRENT_MODE = "ptr_current_mode";
	static final String STATE_DISABLE_SCROLLING_REFRESHING = "ptr_disable_scrolling";
	static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
	static final String STATE_SUPER = "ptr_super";

	// ===========================================================
	// Fields
	// ===========================================================

	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private float mInitialMotionY;

	private boolean mIsBeingDragged = false;
	private int mState = INITIAL_STATE;
	private Mode mMode =  Mode.BOTH;

	private Mode mCurrentMode;
	T mRefreshableView;
	private FrameLayout mRefreshableViewWrapper;

	private boolean mShowViewWhileRefreshing = true;
	private boolean mDisableScrollingWhileRefreshing = true;
	private boolean mFilterTouchEvents = true;
	private boolean mOverScrollEnabled = true;

	private Interpolator mScrollAnimationInterpolator;

	private LoadingLayout mHeaderLayout;
	private LoadingLayout mFooterLayout;

	private int mHeaderHeight;
	private int mFooterHeight;

	private OnRefreshListener<T> mOnRefreshListener;
	private OnRefreshListener2<T> mOnRefreshListener2;
	private OnPullEventListener<T> mOnPullEventListener;

	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	public PullToRefreshBase(Context context) {
		super(context);
		init(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PullToRefreshBase(Context context, Mode mode) {
		super(context);
		mMode = mode;
		init(context, null);
	}

	 
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (DEBUG) {
			Log.d(LOG_TAG, "addView: " + child.getClass().getSimpleName());
		}

		final T refreshableView = getRefreshableView();

		if (refreshableView instanceof ViewGroup) {
			((ViewGroup) refreshableView).addView(child, index, params);
		} else {
			throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
		}
	}

	public final Mode getCurrentMode() {
		return mCurrentMode;
	}

	 
	public final boolean getFilterTouchEvents() {
		return mFilterTouchEvents;
	}

	 
	public final Mode getMode() {
		return mMode;
	}

	 
	public final T getRefreshableView() {
		return mRefreshableView;
	}

	 
	public final boolean getShowViewWhileRefreshing() {
		return mShowViewWhileRefreshing;
	}

	public final boolean hasPullFromTop() {
		return mCurrentMode == Mode.PULL_DOWN_TO_REFRESH;
	}

	 
	public final boolean isDisableScrollingWhileRefreshing() {
		return mDisableScrollingWhileRefreshing;
	}

	 
	public final boolean isPullToRefreshEnabled() {
		return mMode != Mode.DISABLED;
	}

	 
	public final boolean isPullToRefreshOverScrollEnabled() {
		return VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD && mOverScrollEnabled
				&& OverscrollHelper.isAndroidOverScrollEnabled(mRefreshableView);
	}

	 
	public final boolean isRefreshing() {
		return mState == REFRESHING || mState == MANUAL_REFRESHING;
	}

	 
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled()) {
			return false;
		}

		final int action = event.getAction();

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mIsBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
			return true;
		}

		switch (action) {
			case MotionEvent.ACTION_MOVE: {
				// If we're refreshing, and the flag is set. Eat all MOVE events
				if (mDisableScrollingWhileRefreshing && isRefreshing()) {
					return true;
				}

				if (isReadyForPull()) {
					final float y = event.getY();
					final float dy = y - mLastMotionY;
					final float yDiff = Math.abs(dy);
					final float xDiff = Math.abs(event.getX() - mLastMotionX);

					if (yDiff > mTouchSlop && (!mFilterTouchEvents || yDiff > xDiff)) {
						if (mMode.canPullDown() && dy >= 1f && isReadyForPullDown()) {
							mLastMotionY = y;
							mIsBeingDragged = true;
							if (mMode == Mode.BOTH) {
								mCurrentMode = Mode.PULL_DOWN_TO_REFRESH;
							}
						} else if (mMode.canPullUp() && dy <= -1f && isReadyForPullUp()) {
							mLastMotionY = y;
							mIsBeingDragged = true;
							if (mMode == Mode.BOTH) {
								mCurrentMode = Mode.PULL_UP_TO_REFRESH;
							}
						}
					}
				}
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				if (isReadyForPull()) {
					mLastMotionY = mInitialMotionY = event.getY();
					mLastMotionX = event.getX();
					mIsBeingDragged = false;
				}
				break;
			}
		}

		return mIsBeingDragged;
	}

	 
	public final void onRefreshComplete() {
		if (isRefreshing()) {
			resetHeader();
		}
	}

	 
	public final boolean onTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled()) {
			return false;
		}

		// If we're refreshing, and the flag is set. Eat the event
		if (mDisableScrollingWhileRefreshing && isRefreshing()) {
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
			return false;
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {
				if (mIsBeingDragged) {
					mLastMotionY = event.getY();
					pullEvent();
					return true;
				}
				break;
			}

			case MotionEvent.ACTION_DOWN: {
				if (isReadyForPull()) {
					mLastMotionY = mInitialMotionY = event.getY();
					return true;
				}
				break;
			}

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				if (mIsBeingDragged) {
					mIsBeingDragged = false;

					if (mState == RELEASE_TO_REFRESH) {
						onPullEventFinished();

						if (null != mOnRefreshListener) {
							setRefreshingInternal(true);
							mOnRefreshListener.onRefresh(this);
							return true;

						} else if (null != mOnRefreshListener2) {
							setRefreshingInternal(true);
							if (mCurrentMode == Mode.PULL_DOWN_TO_REFRESH) {
								mOnRefreshListener2.onPullDownToRefresh(this);
							} else if (mCurrentMode == Mode.PULL_UP_TO_REFRESH) {
								mOnRefreshListener2.onPullUpToRefresh(this);
							}
							return true;
						} else {
							// If we don't have a listener, just reset
							resetHeader();
							return true;
						}
					}

					onPullEventFinished();
					resetHeader();
					return true;
				}
				break;
			}
		}

		return false;
	}

	 
	public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
		mDisableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
	}

	 
	public final void setFilterTouchEvents(boolean filterEvents) {
		mFilterTouchEvents = filterEvents;
	}

	 
	public void setLastUpdatedLabel(CharSequence label) {
		if (null != mHeaderLayout) {
			mHeaderLayout.setSubHeaderText(label);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setSubHeaderText(label);
		}

		// Refresh Height as it may have changed
		refreshLoadingViewsHeight();
	}

	 
	public void setLoadingDrawable(Drawable drawable) {
		setLoadingDrawable(drawable, Mode.BOTH);
	}

	 
	public void setLoadingDrawable(Drawable drawable, Mode mode) {
		if (null != mHeaderLayout && mode.canPullDown()) {
			mHeaderLayout.setLoadingDrawable(drawable);
		}
		if (null != mFooterLayout && mode.canPullUp()) {
			mFooterLayout.setLoadingDrawable(drawable);
		}

		// The Loading Height may have changed, so refresh
		refreshLoadingViewsHeight();
	}

	 
	public void setLongClickable(boolean longClickable) {
		getRefreshableView().setLongClickable(longClickable);
	}

	 
	public final void setMode(Mode mode) {
		if (mode != mMode) {
			if (DEBUG) {
				Log.d(LOG_TAG, "Setting mode to: " + mode);
			}
			mMode = mode;
			updateUIForMode();
		}
	}

	public void setOnPullEventListener(OnPullEventListener<T> listener) {
		mOnPullEventListener = listener;
	}

	 
	public final void setOnRefreshListener(OnRefreshListener<T> listener) {
		mOnRefreshListener = listener;
	}

	 
	public final void setOnRefreshListener(OnRefreshListener2<T> listener) {
		mOnRefreshListener2 = listener;
	}

	 
	public void setPullLabel(CharSequence pullLabel) {
		setPullLabel(pullLabel, Mode.BOTH);
	}

	 
	public void setPullLabel(CharSequence pullLabel, Mode mode) {
		if (null != mHeaderLayout && mode.canPullDown()) {
			mHeaderLayout.setPullLabel(pullLabel);
		}
		if (null != mFooterLayout && mode.canPullUp()) {
			mFooterLayout.setPullLabel(pullLabel);
		}
	}

	public final void setPullToRefreshEnabled(boolean enable) {
		setMode(enable ? DEFAULT_MODE : Mode.DISABLED);
	}

	 
	public final void setPullToRefreshOverScrollEnabled(boolean enabled) {
		mOverScrollEnabled = enabled;
	}

	 
	public final void setRefreshing() {
		setRefreshing(true);
	}

	 
	public final void setRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setRefreshingInternal(doScroll);
			mState = MANUAL_REFRESHING;
		}
	}

	 
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		setRefreshingLabel(refreshingLabel, Mode.BOTH);
	}

	 
	public void setRefreshingLabel(CharSequence refreshingLabel, Mode mode) {
		if (null != mHeaderLayout && mode.canPullDown()) {
			mHeaderLayout.setRefreshingLabel(refreshingLabel);
		}
		if (null != mFooterLayout && mode.canPullUp()) {
			mFooterLayout.setRefreshingLabel(refreshingLabel);
		}
	}

	 
	public void setReleaseLabel(CharSequence releaseLabel) {
		setReleaseLabel(releaseLabel, Mode.BOTH);
	}

	 
	public void setReleaseLabel(CharSequence releaseLabel, Mode mode) {
		if (null != mHeaderLayout && mode.canPullDown()) {
			mHeaderLayout.setReleaseLabel(releaseLabel);
		}
		if (null != mFooterLayout && mode.canPullUp()) {
			mFooterLayout.setReleaseLabel(releaseLabel);
		}
	}

	public void setScrollAnimationInterpolator(Interpolator interpolator) {
		mScrollAnimationInterpolator = interpolator;
	}

	 
	public final void setShowViewWhileRefreshing(boolean showView) {
		mShowViewWhileRefreshing = showView;
	}

	protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
	}

	protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
		super.addView(child, -1, params);
	}

	protected LoadingLayout createLoadingLayout(Context context, Mode mode, TypedArray attrs) {
		return new LoadingLayout(context, mode, attrs);
	}

	protected abstract T createRefreshableView(Context context, AttributeSet attrs);

	protected final int getFooterHeight() {
		return mFooterHeight;
	}

	protected final LoadingLayout getFooterLayout() {
		return mFooterLayout;
	}

	protected final int getHeaderHeight() {
		return mHeaderHeight;
	}

	protected final LoadingLayout getHeaderLayout() {
		return mHeaderLayout;
	}

	protected int getPullToRefreshScrollDuration() {
		return SMOOTH_SCROLL_DURATION_MS;
	}

	protected int getPullToRefreshScrollDurationLonger() {
		return SMOOTH_SCROLL_LONG_DURATION_MS;
	}

	protected FrameLayout getRefreshableViewWrapper() {
		return mRefreshableViewWrapper;
	}

	protected final int getState() {
		return mState;
	}

	protected void handleStyledAttributes(TypedArray a) {
	}

	protected abstract boolean isReadyForPullDown();

	protected abstract boolean isReadyForPullUp();

	protected void onPullToRefresh() {
		switch (mCurrentMode) {
			case PULL_UP_TO_REFRESH:
				mFooterLayout.pullToRefresh();
				break;
			case PULL_DOWN_TO_REFRESH:
				mHeaderLayout.pullToRefresh();
				break;
		}
	}

	protected void onReleaseToRefresh() {
		switch (mCurrentMode) {
			case PULL_UP_TO_REFRESH:
				mFooterLayout.releaseToRefresh();
				break;
			case PULL_DOWN_TO_REFRESH:
				mHeaderLayout.releaseToRefresh();
				break;
		}
	}

	 
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;

			mMode = Mode.mapIntToMode(bundle.getInt(STATE_MODE, 0));
			mCurrentMode = Mode.mapIntToMode(bundle.getInt(STATE_CURRENT_MODE, 0));

			mDisableScrollingWhileRefreshing = bundle.getBoolean(STATE_DISABLE_SCROLLING_REFRESHING, true);
			mShowViewWhileRefreshing = bundle.getBoolean(STATE_SHOW_REFRESHING_VIEW, true);

			// Let super Restore Itself
			super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));

			final int viewState = bundle.getInt(STATE_STATE, WAITING);
			if (viewState == REFRESHING || viewState == MANUAL_REFRESHING) {
				setRefreshingInternal(true);
				mState = viewState;
			}
			return;
		}

		super.onRestoreInstanceState(state);
	}

	 
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putInt(STATE_STATE, mState);
		bundle.putInt(STATE_MODE, mMode.getIntValue());
		bundle.putInt(STATE_CURRENT_MODE, mCurrentMode.getIntValue());
		bundle.putBoolean(STATE_DISABLE_SCROLLING_REFRESHING, mDisableScrollingWhileRefreshing);
		bundle.putBoolean(STATE_SHOW_REFRESHING_VIEW, mShowViewWhileRefreshing);
		bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());
		return bundle;
	}

	protected void onPullEventFinished() {
		// Call OnPullEventListener
		if (null != mOnPullEventListener) {
			mOnPullEventListener.onRelease(this, mCurrentMode);
		}
	}

	protected void onPullEventStarted() {
		// Call OnPullEventListener
		if (null != mOnPullEventListener) {
			mOnPullEventListener.onPull(this, mCurrentMode);
		}
	}

	protected void resetHeader() {
		mState = WAITING;
		mIsBeingDragged = false;

		if (mMode.canPullDown()) {
			mHeaderLayout.reset();
		}
		if (mMode.canPullUp()) {
			mFooterLayout.reset();
		}

		smoothScrollTo(0);
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected void setRefreshingInternal(boolean doScroll) {
		mState = REFRESHING;

		if (mMode.canPullDown()) {
			mHeaderLayout.refreshing();
		}
		if (mMode.canPullUp()) {
			mFooterLayout.refreshing();
		}

		if (doScroll) {
			if (mShowViewWhileRefreshing) {
				smoothScrollTo(mCurrentMode == Mode.PULL_DOWN_TO_REFRESH ? -mHeaderHeight : mFooterHeight);
			} else {
				smoothScrollTo(0);
			}
		}
	}

	protected final void smoothScrollTo(int y) {
		smoothScrollTo(y, getPullToRefreshScrollDuration());
	}

	protected final void smoothScrollToLonger(int y) {
		smoothScrollTo(y, getPullToRefreshScrollDurationLonger());
	}

	protected void updateUIForMode() {
		// Remove Header, and then add Header Loading View again if needed
		if (this == mHeaderLayout.getParent()) {
			removeView(mHeaderLayout);
		}
		if (mMode.canPullDown()) {
			addViewInternal(mHeaderLayout, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		// Remove Footer, and then add Footer Loading View again if needed
		if (this == mFooterLayout.getParent()) {
			removeView(mFooterLayout);
		}
		if (mMode.canPullUp()) {
			addViewInternal(mFooterLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		// Hide Loading Views
		refreshLoadingViewsHeight();

		// If we're not using Mode.BOTH, set mCurrentMode to mMode, otherwise
		// set it to pull down
		mCurrentMode = (mMode != Mode.BOTH) ? mMode : Mode.PULL_DOWN_TO_REFRESH;
	}

	private void addRefreshableView(Context context, T refreshableView) {
		mRefreshableViewWrapper = new FrameLayout(context);
		mRefreshableViewWrapper.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addViewInternal(mRefreshableViewWrapper, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
	}

	@SuppressWarnings("deprecation")
	private void init(Context context, AttributeSet attrs) {
		setOrientation(LinearLayout.VERTICAL);

		ViewConfiguration config = ViewConfiguration.get(context);
		mTouchSlop = config.getScaledTouchSlop();

		// Styleables from XML
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);

		if (a.hasValue(R.styleable.PullToRefresh_ptrMode)) {
			mMode = Mode.mapIntToMode(a.getInteger(R.styleable.PullToRefresh_ptrMode, 0));
		}

		// Refreshable View
		// By passing the attrs, we can add ListView/GridView params via XML
		mRefreshableView = createRefreshableView(context, attrs);
		addRefreshableView(context, mRefreshableView);

		// We need to create now layouts now
		mHeaderLayout = createLoadingLayout(context, Mode.PULL_DOWN_TO_REFRESH, a);
		mFooterLayout = createLoadingLayout(context, Mode.PULL_UP_TO_REFRESH, a);

		// Styleables from XML
		if (a.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = a.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				setBackgroundDrawable(background);
			}
		}
		if (a.hasValue(R.styleable.PullToRefresh_ptrAdapterViewBackground)) {
			Drawable background = a.getDrawable(R.styleable.PullToRefresh_ptrAdapterViewBackground);
			if (null != background) {
				mRefreshableView.setBackgroundDrawable(background);
			}
		}
		if (a.hasValue(R.styleable.PullToRefresh_ptrOverScroll)) {
			mOverScrollEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrOverScroll, true);
		}

		// Let the derivative classes have a go at handling attributes, then
		// recycle them...
		handleStyledAttributes(a);
		a.recycle();

		// Finally update the UI for the modes
		updateUIForMode();
	}

	private boolean isReadyForPull() {
		switch (mMode) {
			case PULL_DOWN_TO_REFRESH:
				return isReadyForPullDown();
			case PULL_UP_TO_REFRESH:
				return isReadyForPullUp();
			case BOTH:
				return isReadyForPullUp() || isReadyForPullDown();
		}
		return false;
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private void pullEvent() {
		final int newScrollY;
		final int itemHeight;

		switch (mCurrentMode) {
			case PULL_UP_TO_REFRESH:
				newScrollY = Math.round(Math.max(mInitialMotionY - mLastMotionY, 0) / FRICTION);
				itemHeight = mFooterHeight;
				break;
			case PULL_DOWN_TO_REFRESH:
			default:
				newScrollY = Math.round(Math.min(mInitialMotionY - mLastMotionY, 0) / FRICTION);
				itemHeight = mHeaderHeight;
				break;
		}

		setHeaderScroll(newScrollY);

		if (newScrollY != 0) {
			float scale = Math.abs(newScrollY) / (float) itemHeight;
			switch (mCurrentMode) {
				case PULL_UP_TO_REFRESH:
					mFooterLayout.onPullY(scale);
					break;
				case PULL_DOWN_TO_REFRESH:
					mHeaderLayout.onPullY(scale);
					break;
			}

			if (mState != PULL_TO_REFRESH && itemHeight >= Math.abs(newScrollY)) {
				// If the state is WAITING then we've only just started pulling
				if (mState == WAITING) {
					onPullEventStarted();
				}

				mState = PULL_TO_REFRESH;
				onPullToRefresh();
			} else if (mState == PULL_TO_REFRESH && itemHeight < Math.abs(newScrollY)) {
				mState = RELEASE_TO_REFRESH;
				onReleaseToRefresh();
			}
		}
	}

	private void refreshLoadingViewsHeight() {
		mHeaderHeight = mFooterHeight = 0;

		if (mMode.canPullDown()) {
			measureView(mHeaderLayout);
			mHeaderHeight = mHeaderLayout.getMeasuredHeight();
		}
		if (mMode.canPullUp()) {
			measureView(mFooterLayout);
			mFooterHeight = mFooterLayout.getMeasuredHeight();
		}

		// Hide Loading Views
		switch (mMode) {
			case DISABLED:
				setPadding(0, 0, 0, 0);
			case BOTH:
				setPadding(0, -mHeaderHeight, 0, -mFooterHeight);
				break;
			case PULL_UP_TO_REFRESH:
				setPadding(0, 0, 0, -mFooterHeight);
				break;
			case PULL_DOWN_TO_REFRESH:
			default:
				setPadding(0, -mHeaderHeight, 0, 0);
				break;
		}
	}

	private final void smoothScrollTo(int y, long duration) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}

		if (getScrollY() != y) {
			if (null == mScrollAnimationInterpolator) {
				// Default interpolator is a Decelerate Interpolator
				mScrollAnimationInterpolator = new DecelerateInterpolator();
			}
			mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(getScrollY(), y, duration);
			post(mCurrentSmoothScrollRunnable);
		}
	}

	public static enum Mode {
		DISABLED(0x0),

		PULL_DOWN_TO_REFRESH(0x1),

		PULL_UP_TO_REFRESH(0x2),

		BOTH(0x3);

		public static Mode mapIntToMode(int modeInt) {
			switch (modeInt) {
				case 0x0:
					return DISABLED;
				case 0x1:
				default:
					return PULL_DOWN_TO_REFRESH;
				case 0x2:
					return PULL_UP_TO_REFRESH;
				case 0x3:
					return BOTH;
			}
		}

		private int mIntValue;

		// The modeInt values need to match those from attrs.xml
		Mode(int modeInt) {
			mIntValue = modeInt;
		}

		/**
		 * @return true if this mode permits Pulling Down from the top
		 */
		boolean canPullDown() {
			return this == PULL_DOWN_TO_REFRESH || this == BOTH;
		}

		boolean canPullUp() {
			return this == PULL_UP_TO_REFRESH || this == BOTH;
		}

		int getIntValue() {
			return mIntValue;
		}

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface OnLastItemVisibleListener {
		public void onLastItemVisible();

	}

	public static interface OnPullEventListener<V extends View> {
		public void onPull(final PullToRefreshBase<V> refreshView, Mode direction);

		public void onRelease(final PullToRefreshBase<V> refreshView, Mode direction);

	}

	public static interface OnRefreshListener<V extends View> {

		public void onRefresh(final PullToRefreshBase<V> refreshView);

	}

	public static interface OnRefreshListener2<V extends View> {

		public void onPullDownToRefresh(final PullToRefreshBase<V> refreshView);

		public void onPullUpToRefresh(final PullToRefreshBase<V> refreshView);

	}

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DELAY = 10;

		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final long mDuration;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(int fromY, int toY, long duration) {
			mScrollFromY = fromY;
			mScrollToY = toY;
			mInterpolator = mScrollAnimationInterpolator;
			mDuration = duration;
		}

		 
		public void run() {

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
						* mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				setHeaderScroll(mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
					SDK16.postOnAnimation(PullToRefreshBase.this, this);
				} else {
					postDelayed(this, ANIMATION_DELAY);
				}
			}
		}

		public void stop() {
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}

}
