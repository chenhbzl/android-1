package com.example.ttkpulltorefresh;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;

import com.example.ttkpulltorefresh.PullToRefreshBase.Mode;
import com.example.ttkpulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.example.ttkpulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.example.ttkpulltorefresh.PullToRefreshBase.OnRefreshListener2;


public interface IPullToRefresh<T extends View> {

	/**
	 * Get the mode that this view is currently in. This is only really useful
	 * when using <code>Mode.BOTH</code>.
	 * 
	 * @return Mode that the view is currently in
	 */
	public Mode getCurrentMode();

	public boolean getFilterTouchEvents();

	public Mode getMode();

	public T getRefreshableView();

	public boolean getShowViewWhileRefreshing();

	public boolean isDisableScrollingWhileRefreshing();

	public boolean isPullToRefreshOverScrollEnabled();

	public boolean isPullToRefreshEnabled();

	public boolean isRefreshing();

	public void onRefreshComplete();

	public void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing);

	public void setFilterTouchEvents(boolean filterEvents);

	public void setLastUpdatedLabel(CharSequence label);

	public void setLoadingDrawable(Drawable drawable);

	public void setLoadingDrawable(Drawable drawable, Mode mode);

	public void setMode(Mode mode);
	public void setOnPullEventListener(OnPullEventListener<T> listener);

	public void setOnRefreshListener(OnRefreshListener<T> listener);

	public void setOnRefreshListener(OnRefreshListener2<T> listener);

	public void setPullToRefreshOverScrollEnabled(boolean enabled);

	public void setPullLabel(CharSequence pullLabel);

	public void setPullLabel(CharSequence pullLabel, Mode mode);

	public void setRefreshing();

	public void setRefreshing(boolean doScroll);

	public void setRefreshingLabel(CharSequence refreshingLabel);

	public void setRefreshingLabel(CharSequence refreshingLabel, Mode mode);

	public void setReleaseLabel(CharSequence releaseLabel);

	public void setReleaseLabel(CharSequence releaseLabel, Mode mode);

	public void setScrollAnimationInterpolator(Interpolator interpolator);

	public void setShowViewWhileRefreshing(boolean showView);

}