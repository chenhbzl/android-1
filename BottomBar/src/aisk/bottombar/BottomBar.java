package aisk.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BottomBar extends HorizontalScrollView {
	private ImageView mSelectedBtn;
	private ImageView mBtn1;
	private ImageView mBtn2;
	private ImageView mBtn3;
	private ImageView mBtn4;
	private Paint paint;
	private Triangle curTriangle = null;
	private Triangle tarTriangle = null;
	private ImageView mBtn5;
	private ImageView mBtn6;
	private ImageView mBtn7;
	
	public BottomBar(Context context) {
		this(context, null);
	}
	
	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		setHorizontalScrollBarEnabled(false);
		LayoutInflater.from(context).inflate(R.layout.bottom_bar, this, true);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		
		mBtn1 = (ImageView)findViewById(R.id.btn1);
		mBtn2 = (ImageView)findViewById(R.id.btn2);
		mBtn3 = (ImageView)findViewById(R.id.btn3);
		mBtn4 = (ImageView)findViewById(R.id.btn4);
		mBtn5 = (ImageView)findViewById(R.id.btn5);
		mBtn6 = (ImageView)findViewById(R.id.btn6);
		mBtn7 = (ImageView)findViewById(R.id.btn7);
		
//		TypedArray a = context.obtainStyledAttributes(attrs, 
//				R.styleable.BottomBar);
//		mSelectedBtn = a.getInt(R.styleable.BottomBar_selected_btn, 1);
//		a.recycle();
			
		View.OnClickListener clickBtn = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSelectedBtn = (ImageView)view;
				//Log.d("Selected Button:", String.valueOf(mSelectedBtn));

				tarTriangle.left = view.getLeft() + view.getWidth()/2 - 10;
				tarTriangle.right = view.getLeft() + view.getWidth()/2 + 10;
				tarTriangle.top = view.getTop() - 4;
				tarTriangle.bottom = view.getTop() + 13.3f;
				invalidate();
			}
		};
		
		mBtn1.setOnClickListener(clickBtn);
		mBtn2.setOnClickListener(clickBtn);
		mBtn3.setOnClickListener(clickBtn);
		mBtn4.setOnClickListener(clickBtn);
		mBtn5.setOnClickListener(clickBtn);
		mBtn6.setOnClickListener(clickBtn);
		mBtn7.setOnClickListener(clickBtn);
		setSelectedBtn(mBtn1);
	}
	
	
	public void setSelectedBtn(ImageView i) {
		mSelectedBtn = i;
	}
	
	public ImageView getSelectedBtn() {
		return mSelectedBtn;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.d("Validated!", "Should not always validate");
		int step = getWidth()/30;
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Paint.Style.FILL);
		if (curTriangle == null){
			curTriangle = new Triangle(
					mBtn1.getLeft() + mBtn1.getWidth()/2 - 10,
					mBtn1.getTop() - 4,
					mBtn1.getLeft() + mBtn1.getWidth()/2 + 10,
					mBtn1.getTop() + 13.3f
					);
		}
		if (tarTriangle == null){
			tarTriangle = new Triangle(
					mBtn1.getLeft() + mBtn1.getWidth()/2 - 10,
					mBtn1.getTop() - 4,
					mBtn1.getLeft() + mBtn1.getWidth()/2 + 10,
					mBtn1.getTop() + 13.3f
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
