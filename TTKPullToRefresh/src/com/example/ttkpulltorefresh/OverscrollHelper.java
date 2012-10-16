package com.example.ttkpulltorefresh;

import android.annotation.TargetApi;
import android.util.Log;
import android.view.View;

import com.example.ttkpulltorefresh.PullToRefreshBase.Mode;


@TargetApi(9)
final class OverscrollHelper {

	static final String LOG_TAG = "OverscrollHelper";
	static final float DEFAULT_OVERSCROLL_SCALE = 1f;

	static void overScrollBy(final PullToRefreshBase<?> view, final int deltaY, final int scrollY,
			final boolean isTouchEvent) {
		overScrollBy(view, deltaY, scrollY, 0, isTouchEvent);
	}

	static void overScrollBy(final PullToRefreshBase<?> view, final int deltaY, final int scrollY,
			final int scrollRange, final boolean isTouchEvent) {
		overScrollBy(view, deltaY, scrollY, scrollRange, 0, DEFAULT_OVERSCROLL_SCALE, isTouchEvent);
	}

	static void overScrollBy(final PullToRefreshBase<?> view, final int deltaY, final int scrollY,
			final int scrollRange, final int fuzzyThreshold, final float scaleFactor, final boolean isTouchEvent) {

		// Check that OverScroll is enabled
		if (view.isPullToRefreshOverScrollEnabled()) {
			final Mode mode = view.getMode();

			// Check that we're not disabled, and the event isn't from touch
			if (mode != Mode.DISABLED && !isTouchEvent && deltaY != 0) {
				final int newY = (deltaY + scrollY);

				if (PullToRefreshBase.DEBUG) {
					Log.d(LOG_TAG, "OverScroll. DeltaY: " + deltaY + ", ScrollY: " + scrollY + ", NewY: " + newY
							+ ", ScrollRange: " + scrollRange);
				}

				if (newY < (0 - fuzzyThreshold)) {
					// Check the mode supports the overscroll direction, and
					// then move scroll
					if (mode.canPullDown()) {
						view.setHeaderScroll((int) (scaleFactor * (view.getScrollY() + newY)));
					}
				} else if (newY > (scrollRange + fuzzyThreshold)) {
					// Check the mode supports the overscroll direction, and
					// then move scroll
					if (mode.canPullUp()) {
						view.setHeaderScroll((int) (scaleFactor * (view.getScrollY() + newY - scrollRange)));
					}
				} else if (Math.abs(newY) <= fuzzyThreshold || Math.abs(newY - scrollRange) <= fuzzyThreshold) {
					// Means we've stopped overscrolling, so scroll back to 0
					view.smoothScrollToLonger(0);
				}
			}
		}
	}

	static boolean isAndroidOverScrollEnabled(View view) {
		return view.getOverScrollMode() != View.OVER_SCROLL_NEVER;
	}
}
