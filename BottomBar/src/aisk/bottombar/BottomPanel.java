package aisk.bottombar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BottomPanel extends LinearLayout {
	private Paint paint;
	private ImageView mBtn;
	private Triangle curTriangle = null;

	public BottomPanel(Context context) {
		this(context, null);
	}
	
	public BottomPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		LayoutInflater.from(context).inflate(R.layout.bottom_panel, this, true);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		
		mBtn = (ImageView)findViewById(R.id.btn);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Paint.Style.FILL);
		if (curTriangle == null){
			curTriangle = new Triangle(
					mBtn.getLeft() + mBtn.getWidth()/2 - 10,
					mBtn.getTop() - 4,
					mBtn.getLeft() + mBtn.getWidth()/2 + 10,
					mBtn.getTop() + 13.3f
					);
		}
		canvas.drawPath(curTriangle.getPath(), paint);
	}
	
	public ImageView getChildImageView(){
		return mBtn;
	}
	
}
