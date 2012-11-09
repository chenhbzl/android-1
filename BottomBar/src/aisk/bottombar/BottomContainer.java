package aisk.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BottomContainer extends RelativeLayout {
	
	private BottomBar bottomBar;
	private BottomPanel bottomPanelLeft;
	private BottomPanel bottomPanelRight;
	private ImageView focusedBtn;

	public BottomContainer(Context context) {
		this(context, null);
	}
	
	public BottomContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		LayoutInflater.from(context).inflate(R.layout.bottom_container, this, true);
		findView();
	}

	private void findView() {
		bottomBar = (BottomBar) findViewById(R.id.bottomBar);
		bottomPanelLeft = (BottomPanel) findViewById(R.id.bottomBarLeft);
		bottomPanelRight = (BottomPanel) findViewById(R.id.bottomBarRight);
//		focusedBtn = (ImageView)bottomBar.getSelectedBtn();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = bottomBar.getScrollX();
		focusedBtn = (ImageView)bottomBar.getSelectedBtn();
		if (focusedBtn.getLeft() - x < 0){
			((ImageView)bottomPanelLeft.getChildImageView()).setImageDrawable(focusedBtn.getDrawable());
			bottomPanelLeft.setVisibility(View.VISIBLE);
			bottomPanelRight.setVisibility(View.GONE);
		} else if (focusedBtn.getRight() - x  > getWidth()){
			((ImageView)bottomPanelRight.getChildImageView()).setImageDrawable(focusedBtn.getDrawable());
			bottomPanelRight.setVisibility(View.VISIBLE);
			bottomPanelLeft.setVisibility(View.GONE);
		} else{
			bottomPanelRight.setVisibility(View.GONE);
			bottomPanelLeft.setVisibility(View.GONE);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		bottomBar.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	
}
