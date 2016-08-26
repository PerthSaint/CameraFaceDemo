package com.example.testfacecamera;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

	SurfaceView preview;
	Camera camera;
	GoogleFaceDetect googleFaceDetect;
	FaceView faceView;

	private int camareId;
	MainHandler mMainHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		
		
		
		
		
		
		
		
		setContentView(R.layout.activity_main);
		preview = (SurfaceView) findViewById(R.id.preview);
		faceView = (FaceView) findViewById(R.id.mfaceview);
		SurfaceHolder holder = preview.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(mback); // 回调接口

		mMainHandler = new MainHandler();
		googleFaceDetect = new GoogleFaceDetect(getApplicationContext(),
				mMainHandler);
		findViewById(R.id.start).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startGoogleFaceDetect();
			}
		});
		findViewById(R.id.end).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopGoogleFaceDetect();
			}
		});
		camareId = CameraInfo.CAMERA_FACING_FRONT;
		CheckBox cb = (CheckBox) findViewById(R.id.changeCamare);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {// 准备开启前置
					camareId = CameraInfo.CAMERA_FACING_FRONT;
				} else { // 后置
					camareId = CameraInfo.CAMERA_FACING_BACK;
				}
				onSwtichCamare(camareId);
			}
		});

	}

	private boolean isSwtiching = false;

	private void onSwtichCamare(int mcamareId) {
		if (!isSwtiching) {

			if (null != camera) {
				stopGoogleFaceDetect();
				mMainHandler.removeMessages(GoogleFaceDetect.UPDATE_FACE_RECT);
				mMainHandler.removeCallbacks(doStartRunnable);
				camera.stopPreview();
				camera.setPreviewCallbackWithBuffer(null);
				camera.release();
				camera = null;
			}
			camera = Camera.open(mcamareId);
			Parameters sp = camera.getParameters();
			try {
				camera.setPreviewDisplay(preview.getHolder());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			List<Size> mlist = sp.getSupportedPreviewSizes();
			for (int i = 0; i < mlist.size(); i++) {
				Size s = mlist.get(i);
				Log.e("s" + i, s.width + "*" + s.height);
			}
			sp.setPreviewSize(640, 480);
			sp.set("max-num-detected-faces-hw", 2);
			camera.setDisplayOrientation(90);
			camera.setParameters(sp);

			try {
				camera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
				if (camera != null) {
					camera.release();
					camera = null;
				}
			}
			mMainHandler.postDelayed(doStartRunnable, 1000);

			isSwtiching = false;
		}

	}

	private Runnable doStartRunnable = new Runnable() {

		@Override
		public void run() {
			startGoogleFaceDetect();
		}
	};
	private final int MSG_START_FACEDETECT = 1021;

	private class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GoogleFaceDetect.UPDATE_FACE_RECT:
				// Log.e("rehandleMessage----", "===");
				Face[] faces = (Face[]) msg.obj;
//				if (faces.length > 0) {
//					Face f = faces[0];
//					Log.e("---", f.leftEye + "--" + f.rightEye + "---"
//							+ f.mouth + "---" + f.rect.toShortString() + "---"
//							+ f.score + "--");
					faceView.setFaces(faces);
//				}
				break;
			case MSG_START_FACEDETECT:
				startGoogleFaceDetect();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	private void startGoogleFaceDetect() {
		Camera.Parameters params = camera.getParameters();
		int max = params.getMaxNumDetectedFaces();
		Log.e("max", max + " startGoogleFaceDetect");
		// if (params.getMaxNumDetectedFaces() > 0) {
		if (faceView != null) {
			faceView.clearFaces();
			faceView.setVisibility(View.VISIBLE);
		}
		faceView.setIsMirror(camareId);
		camera.setFaceDetectionListener(googleFaceDetect);
		camera.startFaceDetection();
		// }
	}

	private void stopGoogleFaceDetect() {
		Log.e("stopGoogleFaceDetect", "stopGoogleFaceDetect");
		camera.setFaceDetectionListener(null);
		camera.stopFaceDetection();

		faceView.clearFaces();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		// 释放相机
		if (camera != null) {
			camera.release();
			camera = null;
		}

	}

	Callback mback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

			// if (null != camera) {
			// camera.startPreview();
			// }
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			onSwtichCamare(camareId);

		}
	};

}
