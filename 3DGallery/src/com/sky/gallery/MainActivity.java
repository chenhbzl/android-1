package com.sky.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnItemSelectedListener{

	private CustomGallery gallery;
	private ImageAdapter imageAdapter;

	private int[] resIds = new int[] { R.drawable.item1, R.drawable.item2,
			R.drawable.item3, R.drawable.item4, R.drawable.item5,
			R.drawable.item6, R.drawable.item7, R.drawable.item8,
			R.drawable.item9, R.drawable.item10, R.drawable.item11,
			R.drawable.item12, R.drawable.item13, R.drawable.item14,
			R.drawable.item15 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gallery = (CustomGallery) findViewById(R.id.gallery);
		gallery.setSelection(2, true);
		gallery.setAnimationDuration(1000);
		imageAdapter = new ImageAdapter(this);
		gallery.setAdapter(imageAdapter);
		gallery.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// 选中Gallery中某个图像时，在ImageSwitcher组件中放大显示该图像

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	public class ImageAdapter extends BaseAdapter {

		int mGalleryItemBackground;

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
			TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = typedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			typedArray.recycle();
		}

		// 第1点改进，返回一个很大的值，例如，Integer.MAX_VALUE
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = createReflectedImages(mContext, resIds[position%resIds.length]);
			i.setLayoutParams(new CustomGallery.LayoutParams(120, 100));
			i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// 设置的抗锯齿,防止图像在旋转的时候出现锯齿
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			return i;
		}
	}

	/**
	 * 为图片添加倒影
	 * @param mContext
	 * @param imageId
	 * @return
	 */
	public ImageView createReflectedImages(Context mContext, int imageId) {
		Bitmap originalImage = BitmapFactory.decodeResource(
				mContext.getResources(), imageId);
		final int reflectionGap = 4;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(originalImage, 0, 0, null);

		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff,
				TileMode.MIRROR);

		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(bitmapWithReflection);

		return imageView;
	}

}
