package aisk.timebar;

import aisk.bottombar.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * to use this widget, you can use following code
 * <code><br/>
 * in activity<br/>
 * TimePicker picker = (TimePicker) findViewById(R.id.picker);<br/>
 * int[] data = {1, 3, 5, 7, 13, 15, 19, 24, 29, 33, 37, 41, 48, 49, 50};<br/>
 * picker.setTimeData(data);<br/>
 * in res/layout/main.xml<br/>
 * 	<aisk.timebar.TimePicker<br/> 
 * 		android:id="@+id/picker"<br/>
 * 	    android:layout_width="fill_parent"<br/>
 * 	    android:layout_height="wrap_content"/><br/>
 * </code><br/>
 * 
 * @author Jayzhou215 
 * @email jayzhou215@ssreader.cn
 * @qq 512582912
 *
 */
public class TimePicker extends RelativeLayout {

	private TimeBar timeBar;
	private TimePanel timePanelLeft;
	private TimePanel timePanelRight;
	private TextView focusedBtn;
	private final int WC = RelativeLayout.LayoutParams.WRAP_CONTENT;
	private final int FP = RelativeLayout.LayoutParams.FILL_PARENT;
	public final int WIDTH = 100;
	public final int HEIGHT = 64;
	public TimePicker(Context context) {
		this(context, null);
	}
	
	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		creatView(context);
	}

	public void setTimeData(int[] data){
		timeBar.setTimeData(data);
	}
	private void creatView(Context context) {
		timeBar = new TimeBar(context);
		timePanelLeft = new TimePanel(context);
		timePanelRight = new TimePanel(context);
		
		LayoutParams lp;
		lp = new LayoutParams(FP, WC);
		lp.addRule(CENTER_VERTICAL, RelativeLayout.TRUE);
		addView((View)timeBar, lp);
		
		lp = new LayoutParams(WC, WC);
		lp.addRule(ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		lp.addRule(CENTER_VERTICAL, RelativeLayout.TRUE);
		addView((View)timePanelLeft, lp);
		
		lp = new LayoutParams(WC, WC);
		lp.addRule(ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		lp.addRule(CENTER_VERTICAL, RelativeLayout.TRUE);
		addView((View)timePanelRight, lp);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = timeBar.getScrollX();
		focusedBtn = (TextView)timeBar.getSelectedView();
		if (focusedBtn != null){
			if (focusedBtn.getLeft() - x < 0){
				((TextView)timePanelLeft.getChildTextView()).setText(focusedBtn.getText());
				timePanelLeft.setVisibility(View.VISIBLE);
				timePanelRight.setVisibility(View.GONE);
			} else if (focusedBtn.getRight() - x  > getWidth()){
				((TextView)timePanelRight.getChildTextView()).setText(focusedBtn.getText());
				timePanelRight.setVisibility(View.VISIBLE);
				timePanelLeft.setVisibility(View.GONE);
			} else{
				timePanelRight.setVisibility(View.GONE);
				timePanelLeft.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		timeBar.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	private class TimeBar extends HorizontalScrollView {
		private TextView mSelectedView;
		
		private Paint paint;
		private Triangle curTriangle = null;
		private Triangle tarTriangle = null;
		private int[] mData;
		private Context mContext;
		private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
		
		public TimeBar(Context context) {
			this(context, null);
		}
		

		public TimeBar(Context context, AttributeSet attrs) {
			super(context, attrs);
			mContext = context;
			setWillNotDraw(false);
			setHorizontalScrollBarEnabled(false);
		}

		public int[] getTimeData(){
			return mData;
		}
		
		public void setTimeData(int[] data) {
			mData = data;
			if (mData == null)
				return;
			LinearLayout layout = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp;
			for (int i = 0; mData.length > i; i++){
				TextView textView = new TextView(mContext);
				textView.setText(Integer.toString(mData[i]));
				textView.setTextColor(Color.WHITE);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(20);
				textView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						mSelectedView = (TextView) view;
						tarTriangle.left = view.getLeft() + view.getWidth()/2 - 10;
						tarTriangle.right = view.getLeft() + view.getWidth()/2 + 10;
						tarTriangle.top = view.getTop() - 4;
						tarTriangle.bottom = view.getTop() + 13.3f;
						invalidate();
					}
				});
				lp = new LinearLayout.LayoutParams(WIDTH, HEIGHT);
				lp.gravity = CENTER_HORIZONTAL;
				layout.addView((View)textView, lp);
				if (i == 0)
					mSelectedView = textView;
			}
			lp = new LinearLayout.LayoutParams(WC, HEIGHT);
			addView(layout);
		}
		
		public void setSelectedView(TextView i) {
			mSelectedView = i;
		}
		
		public TextView getSelectedView() {
			return mSelectedView;
		}

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			//Log.d("Validated!", "Should not always validate");
			if (mSelectedView != null){
				int step = getWidth()/30;
				canvas.drawColor(Color.BLACK);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setColor(Color.LTGRAY);
				paint.setStyle(Paint.Style.FILL);
				if (curTriangle == null){
					curTriangle = new Triangle(
							mSelectedView.getLeft() + mSelectedView.getWidth()/2 - 10,
							mSelectedView.getTop() - 4,
							mSelectedView.getLeft() + mSelectedView.getWidth()/2 + 10,
							mSelectedView.getTop() + 13.3f
							);
				}
				if (tarTriangle == null){
					tarTriangle = new Triangle(
							mSelectedView.getLeft() + mSelectedView.getWidth()/2 - 10,
							mSelectedView.getTop() - 4,
							mSelectedView.getLeft() + mSelectedView.getWidth()/2 + 10,
							mSelectedView.getTop() + 13.3f
							);
				}
				
				if (Math.abs(curTriangle.left - tarTriangle.left) < step) {
					curTriangle.left = tarTriangle.left;
					curTriangle.right = tarTriangle.right;
					curTriangle.reDraw();
				}
				
				if (curTriangle.left > tarTriangle.left){
					curTriangle.left -= step;
					curTriangle.right -= step;
					curTriangle.reDraw();
					invalidate();
				}
				else if (curTriangle.left < tarTriangle.left) {
					curTriangle.left += step;
					curTriangle.right += step;
					curTriangle.reDraw();
					invalidate();
				}
				canvas.drawPath(curTriangle.getPath(), paint);
			}
		}
	}
	
	private class TimePanel extends LinearLayout {
		private Paint paint;
		private TextView mTextView;
		private Triangle curTriangle = null;

		public TimePanel(Context context) {
			this(context, null);
		}
		
		public TimePanel(Context context, AttributeSet attrs) {
			super(context, attrs);
			setWillNotDraw(false);
			
			paint = new Paint();
			paint.setAntiAlias(true);
			
			mTextView = new TextView(context);
			mTextView.setTextColor(Color.WHITE);
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setTextSize(20);
			LinearLayout.LayoutParams lp;
			lp = new LinearLayout.LayoutParams(WIDTH, HEIGHT);
			lp.gravity = CENTER_HORIZONTAL;
			addView((View)mTextView, lp);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawColor(Color.BLACK);
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
			if (curTriangle == null){
				curTriangle = new Triangle(
						mTextView.getLeft() + mTextView.getWidth()/2 - 10,
						mTextView.getTop() - 4,
						mTextView.getLeft() + mTextView.getWidth()/2 + 10,
						mTextView.getTop() + 13.3f
						);
			}
			canvas.drawPath(curTriangle.getPath(), paint);
		}
		
		public TextView getChildTextView(){
			return mTextView;
		}
	}
	
	public class Triangle implements Parcelable{
		public float left;
		public float top;
		public float right;
		public float bottom;
		private Path mPath;
		
		public Path getPath() {
			return mPath;
		}

		public void setPath(Path mPath) {
			this.mPath = mPath;
		}


		public Triangle(float left, float top, float right, float bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			mPath = new Path();
			mPath.moveTo(left, top);
			mPath.lineTo(right, top);
			mPath.lineTo((left + right)/2, bottom);
			mPath.close();
		}

		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(left);
			dest.writeFloat(top);
			dest.writeFloat(right);
			dest.writeFloat(bottom);
		}

		public void reDraw() {
			mPath.reset();
			mPath.moveTo(left, top);
			mPath.lineTo(right, top);
			mPath.lineTo((left + right)/2, bottom);
			mPath.close();
		}

	}
}
