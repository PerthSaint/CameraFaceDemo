package com.example.testfacecamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class FaceView extends ImageView {
	private static final String TAG = "FaceView";
	private Context mContext;
	private Paint mLinePaint;
	private Face[] mFaces;
	private Matrix mMatrix = new Matrix();
	private RectF mRect = new RectF();
	private Drawable mFaceIndicator = null;

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
		mContext = context;
		mFaceIndicator = getResources().getDrawable(R.drawable.ic_launcher);
	}

	public void setFaces(Face[] faces) {
		this.mFaces = faces;
		invalidate();
	}

	public void clearFaces() {
		mFaces = null;
		invalidate();
	}

	private int camareId;

	public void setIsMirror(int _camareId) {
		camareId = _camareId;
	}

	boolean isMirror = false;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (mFaces == null || mFaces.length < 1) {
			return;
		}

		isMirror = false;
		if (camareId == CameraInfo.CAMERA_FACING_BACK) {
			isMirror = false; // 后置Camera无需mirror
		} else if (camareId == CameraInfo.CAMERA_FACING_FRONT) {
			isMirror = true; // 前置Camera需要mirror
		}

		Util.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
		canvas.save();
		mMatrix.postRotate(0); // Matrix.postRotate默认是顺时针
		canvas.rotate(-0); // Canvas.rotate()默认是逆时针
		int len = mFaces.length;
		for (int i = 0; i < len; i++) {
			mRect.set(mFaces[i].rect);
			mMatrix.mapRect(mRect);
			mFaceIndicator.setBounds(Math.round(mRect.left),
					Math.round(mRect.top), Math.round(mRect.right),
					Math.round(mRect.bottom));
			// mFaceIndicator.draw(canvas);
			canvas.drawRect(mRect, mLinePaint);
			Face f = mFaces[i];
			Log.e("---", f.leftEye + "--" + f.rightEye + "---" + f.mouth
					+ "---" + f.rect.toShortString() + "---" + f.score + "--");
		}
		if (len > 0) {
//			mMatrix.postRotate(0);
			
			
			Face f = mFaces[0];
//			mMatrix.mapRect(mRect);
			
			
			
			float[] p1=new float[2];
			float[] p2=new float[2];
			float[] p3=new float[2];
			float[] p11=new float[]{f.leftEye.x, f.leftEye.y};
			float[] p21=new float[]{ f.rightEye.x,
				f.rightEye.y};
			float[] p31=new float[]{f.mouth.x, f.mouth.y};
			
			
//			mMatrix.mapPoints(pts)
			mMatrix.mapPoints(p1, p11);
			mMatrix.mapPoints(p2, p21);
			mMatrix.mapPoints(p3, p31);
			
			canvas.drawLine(p1[0], p1[1], p2[0],p2[1], mLinePaint);
			canvas.drawLine(p3[0], p3[1], p2[0],p2[1],
					mLinePaint);
			canvas.drawLine(p3[0], p3[1], p1[0], p1[1],
					mLinePaint);
//			mMatrix.mapPoints(new float[] {,});
//			mMatrix.mapPoints(new float[] { f.leftEye.x, f.leftEye.y });
//			mMatrix.mapPoints(new float[] { f.leftEye.x, f.leftEye.y });

		}
		// Log.e("ondraw....", mFaces.length + "人数");

		canvas.restore();
		super.onDraw(canvas);
	}

	private void initPaint() {
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// int color = Color.rgb(0, 150, 255);
		int color = Color.rgb(98, 212, 68);
		// mLinePaint.setColor(Color.RED);
		mLinePaint.setColor(color);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5f);
		mLinePaint.setAlpha(180);
	}
}