package aisk.bottombar;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

public class Triangle implements Parcelable {

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
