/*
 * Copyright © 2011 Infotec Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Preview.java
package jp.iftc.androidasset.barcode;

import java.util.List;

import jp.iftc.androidasset.BarcodeReaderActivity;
import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import jp.iftc.androidasset.map.GPSLocation;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

// プレビューの表示とデータの解析を行う
public class Preview extends SurfaceView implements SurfaceHolder.Callback{

	private static final String TAG = Preview.class.getSimpleName();

	// オートフォーカスした画像を解析する回数
	private static final int TRY_PREVIEW = 1;

	private BarcodeReaderActivity mBarcodReaderActivity;
	private Camera mCamera;
	private FinderView mFinder;
	private SurfaceHolder mHolder;
	private Rect mFramingRect;		// 解析エリア
	private Point mResolution;		// 画像の縦横ピクセル数
	private Point mViewSize;		// 画像の縦横ピクセル数
	private Decoder mDecoder;		// バーコードデコーダ
	private int previewCount;
	private boolean isPreviewing = false;

	protected Camera.Size mPreviewSize;	//プレビューサイズ
	protected Camera.Size mPictureSize;	//画像サイズ
	private List<Camera.Size> mPreviewSizeList;
	private List<Camera.Size> mPictureSizeList;


	/**
	 * コンストラクタ
	 *
	 * @param context
	 * @param attrs
	 */
	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG, "new Preview");

		mDecoder = new Decoder();
//		mResultDialog = new ResultDialog(context);
		mBarcodReaderActivity = (BarcodeReaderActivity) context;

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	/*
	 * SurfaceViewが最初に生成されたときに呼び出されるメソッド
	 * (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		// surfaceが作られると共にカメラを開く
		mCamera = Camera.open(getFaceBackCameraId());
		Camera.Parameters cameraParams = mCamera.getParameters();
		mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
		mPictureSizeList = cameraParams.getSupportedPictureSizes();
	}

	/*
	 * SurfaceViewが破棄されるときに呼び出されるメソッド
	 * (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");

		if (mCamera != null && isPreviewing) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			isPreviewing = false;

			mCamera.release();
			mCamera = null;
			// surfaceの破棄時にカメラを解放する
			closeCamera();
		}
	}

	/*
	 * SurfaceViewのサイズなどが変更されたときに呼び出されるメソッド。
	 * (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged / w:" + width + ", h:" + height);

//		refreshScreenResolution();

		if (mCamera != null) {
			if (isPreviewing) {
				mCamera.stopPreview();
			}

			// カメラのパラメタ設定
			Camera.Parameters parameters = mCamera.getParameters();

			Camera.Size previewSize = determinePreviewSize(true, width, height);
			Camera.Size pictureSize = determinePictureSize(previewSize);

			mPreviewSize = previewSize;
			mPictureSize = pictureSize;

			// adjustSurfaceLayoutSize(previewSize, true, width, height);

			configureCameraParameters(parameters, true);

			// SDK1.5からsetPreviewDisplayはIOExceptionをthrowする
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// カメラプレビュー開始
			mCamera.startPreview();
			isPreviewing = true;
			requestAutoFocus();
			requestPreview();
		}

		refreshScreenResolution();
		refreshFrameRect();
	}

	/**
	 * ファインダーをセットする
	 * @param finder
	 */
	public void setFinder(FinderView finder) {
		mFinder = finder;
	}

	/**
	 * カメラを閉じる
	 */
	public void closeCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * ファインダー領域を再設定する
	 */
	private void refreshFrameRect() {
		if (mFinder != null) {
			mFinder.setFramingRect(getFrameRect());
		}
	}

	/**
	 * ファインダーの領域を生成
	 * @return
	 */
	private Rect getFrameRect() {
		Log.i(TAG, "getFramingRect");
		if (mFramingRect == null) {
//			int size = ((mResolution.x < mResolution.y) ? mResolution.x
//					: mResolution.y) * 3 / 4;
//			int leftOffset = (mResolution.x - size) / 2;
//			int topOffset = (mResolution.y - size) / 2;
//			mFramingRect = new Rect(leftOffset, topOffset, leftOffset + size,
//					topOffset + size);
			int size = ((mViewSize.x < mViewSize.y) ? mViewSize.x
					: mViewSize.y) * 3 / 4;
			int leftOffset = (mViewSize.x - size) / 2;
			int topOffset = (mViewSize.y - size) / 2;
			mFramingRect = new Rect(leftOffset, topOffset, leftOffset + size,
					topOffset + size);
		}
		return mFramingRect;
	}

	/**
	 * レイアウトの解像度を取得。
	 * Decoderに渡す１次元配列の縦横ピクセル数となる。
	 *
	 */
	private void refreshScreenResolution() {
		int w = getWidth();
		int h = getHeight();

		mViewSize = new Point(w, h);
		mResolution = new Point(w, h );
//		mResolution = new Point(mPreviewSize.width, mPreviewSize.height);
	}

	/**
	 * オートフォーカスを起動
	 */
	public void requestAutoFocus() {
		Log.i(TAG, "requestAutoFocus");
		if (mCamera != null) {
			mCamera.autoFocus(autoFocusCallback);
		}
	}

	/**
	 * プレビュー画像取得
	 */
	private void requestPreview() {
		Log.i(TAG, "requestPreview");
		if (mCamera != null) {
			mCamera.setPreviewCallback(previewCallback);
		}
	}

	/**
	 * 背面カメラのIDを取得する。
	 *
	 * @return 背面カメラのID
	 */
	private int getFaceBackCameraId() {
		// 端末に搭載されているカメラの数を取得　
		int numberOfCameras = Camera.getNumberOfCameras();
		int faceBackCameraId = 0;

		// 各カメラの情報を取得
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo caminfo = new CameraInfo();
			Camera.getCameraInfo(i, caminfo);

			// カメラの向きを取得
			int facing = caminfo.facing;

			if (facing == CameraInfo.CAMERA_FACING_BACK) {
				// 後部についているカメラの場合
				Log.d("MultiCameraTest", "cameraId=" + Integer.toString(i)
						+ ", this is a back-facing camera");
				faceBackCameraId = i;
			} else if (facing == CameraInfo.CAMERA_FACING_FRONT) {
				// フロントカメラの場合
				Log.d("MultiCameraTest", "cameraId=" + Integer.toString(i)
						+ ", this is a front-facing camera");
			} else {
				Log.d("MultiCameraTest", "cameraId=" + Integer.toString(i)
						+ ", unknown camera?");
			}

			// カメラのOrientation(角度) を取得
			int orient = caminfo.orientation;
			Log.d("MultiCameraTest", "cameraId=" + Integer.toString(i)
					+ ", orientation=" + Integer.toString(orient));
		}

		return faceBackCameraId;
	}

	/**
	 * 利用可能な、プレビューサイズを取得する
	 *
	 * @param cameraParams
	 * @param portrait
	 * @param reqWidth
	 *            must be the value of the parameter passed in surfaceChanged
	 * @param reqHeight
	 *            must be the value of the parameter passed in surfaceChanged
	 * @return Camera.Size object that is an element of the list returned from
	 *         Camera.Parameters.getSupportedPreviewSizes.
	 */
	protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth,
			int reqHeight) {
		// while it is the same as user's view for surface and metrics.
		// That is, width must always be larger than height for setPreviewSize.
		int reqPreviewWidth; // requested width in terms of camera hardware
		int reqPreviewHeight; // requested height in terms of camera hardware

		reqPreviewWidth = reqWidth;
		reqPreviewHeight = reqHeight;

		Log.v(TAG, "requested width w: " + reqWidth + ", h: " + reqHeight);

		// Adjust surface size with the closest aspect-ratio
		float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		Camera.Size retSize = null;
		for (Camera.Size size : mPreviewSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}

		return retSize;
	}

	/**
	 * 利用可能なPictureサイズを取得する
	 * @param previewSize
	 * @return
	 */
	protected Camera.Size determinePictureSize(Camera.Size previewSize) {
		Camera.Size retSize = null;
		for (Camera.Size size : mPictureSizeList) {
			if (size.equals(previewSize)) {
				return size;
			}
		}

		// if the preview size is not supported as a picture size
		float reqRatio = ((float) previewSize.width) / previewSize.height;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		for (Camera.Size size : mPictureSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}

		return retSize;
	}

	/**
	 * @param previewSize
	 * @param portrait
	 * @param availableWidth
	 * @param availableHeight
	 * @return
	 */
	protected boolean adjustSurfaceLayoutSize(Camera.Size previewSize,
			boolean portrait, int availableWidth, int availableHeight) {
		float tmpLayoutHeight, tmpLayoutWidth;
		if (portrait) {
			tmpLayoutHeight = previewSize.width;
			tmpLayoutWidth = previewSize.height;
		} else {
			tmpLayoutHeight = previewSize.height;
			tmpLayoutWidth = previewSize.width;
		}

		float factH, factW, fact;
		factH = availableHeight / tmpLayoutHeight;
		factW = availableWidth / tmpLayoutWidth;

		if (factH < factW) {
			fact = factW;
		} else {
			fact = factH;
		}

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this
				.getLayoutParams();

		int layoutHeight = (int) (tmpLayoutHeight * fact);
		int layoutWidth = (int) (tmpLayoutWidth * fact);

		boolean layoutChanged;
		if ((layoutWidth != this.getWidth())
				|| (layoutHeight != this.getHeight())) {
			layoutParams.height = layoutHeight;
			layoutParams.width = layoutWidth;
			if (mResolution.x >= 0) {
				layoutParams.topMargin = (mResolution.y - layoutHeight) / 2;
				layoutParams.leftMargin = (mResolution.x - layoutWidth) / 2;
			}

			this.setLayoutParams(layoutParams); // this will trigger another
												// surfaceChanged invocation.
			layoutChanged = true;
		} else {
			layoutChanged = false;
		}

		return layoutChanged;
	}

	/**
	 * カメラのパラメータを設定する
	 *   とりあえず、向きについてはなにもしない。
	 * @param cameraParams		設定するカメラパラメータ
	 * @param portrait			向き
	 */
	protected void configureCameraParameters(Camera.Parameters cameraParams,
			boolean portrait) {

		try {
			cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
		} catch(Exception e) {
			e.printStackTrace();
		}

		mCamera.setParameters(cameraParams);
	}

	/**
	 * 資産情報の有無を確認する
	 * @param assetNumber
	 * @return		：0　既存＋未確認、1：なし、9：既存＋確認済み
	 */
	private int checkAssetInfo(long assetNumber){
		AssetInfoDAO dao = new AssetInfoDAO(mBarcodReaderActivity);
		AssetInfo assetInfo;
		int result = 0;

		assetInfo = dao.loadAssetInfo(assetNumber);

		if(assetInfo == null) { // 資産情報なし
			result = AssetInfo.NORECORD;
		} else if (assetInfo.getCheckResultInt() == AssetInfo.CHECK_OK) { //確認ok登録済み
			result = AssetInfo.CHECK_OK;

		} else if (assetInfo.getCheckResultInt() == AssetInfo.CHECK_NG) { //確認NG登録済み
			result = AssetInfo.CHECK_NG;

		} else { //資産情報あり + 確認未
			result = AssetInfo.NO_CHECK;
		}
		return result;

	}

	/**
	 *
	 * 確認ダイアログの表示
	 * @param img
	 * @param result
	 * @param assetInfo
	 * @param chkResult
	 * @return
	 */
      private int showBarcodeResultDaialog(Bitmap img, Long result, AssetInfo assetInfo, int chkResult){

		FragmentManager manager = mBarcodReaderActivity.getFragmentManager();
		BarcodeResultDialogFragment dialog = BarcodeResultDialogFragment.newInstance(img, String.valueOf(result), chkResult);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
	        public void onDismiss(DialogInterface dialog) {
	        	Log.d(TAG,"ダイアログが閉じました。");

				// バーコードリーダ　再起動確認ダイアログを表示する。
				mBarcodReaderActivity.showReEncodeDialog();
	        }
		});
        dialog.show(manager, "dialog");

		return 0;

	}

	/**
	 * 確認結果登録
	 * @param assetNumber ：ID
	 */
	private void updateCheckResult(long assetNumber) {

		AssetInfo assetInfo = new AssetInfo();

		assetInfo.setId( assetNumber );
		assetInfo.setCheckResult(Integer.toString(AssetInfo.CHECK_OK));

		//確認結果OK登録の場合現在位置をセット
		setLocateion(assetInfo);

		int insertFlg = 0;
		AssetInfoDAO dao = new AssetInfoDAO(mBarcodReaderActivity);

		AssetInfo newAsset;
		newAsset = dao.saveCheckResult(assetInfo, insertFlg);

		Toast.makeText(mBarcodReaderActivity, "管理番号:[" + newAsset.getAssetNumber() + "]" + " ID [" + String.valueOf(newAsset.getId()) + "]" + '\n' + " 確認結果を登録しました。", Toast.LENGTH_LONG).show();
	}

	/**
	 *  PreviewCallbackとAutoFocusCallbackの相互再帰呼出しの形となる
	 *  実際にはsetPreviewCallbackとautoFocusのコールバック関数setterで実現している
	 *
	 */
	private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Log.i(TAG, "onPreviewFrame");
			camera.setPreviewCallback(null);
			Result result = mDecoder.decode(data, mResolution.x, mResolution.y,
					getFrameRect());
			if (result != null) {
				previewCount = 0;
				BarcodeFormat code = result.getBarcodeFormat();
				Log.i(TAG, "preview success(" + code + ", " + result.getText()
						+ ")");

				AssetInfo ai = new AssetInfo();

				// EAN13形式のバーコードのみ続きの処理を行う
				if ( code == BarcodeFormat.EAN_13 ) {
                    int countrycode = Integer.parseInt( result.getText().substring(0,2) );

                    // NonPLUコードの判定
				    if ( countrycode>=20 && countrycode<=29 ){

    					Bitmap img = mDecoder.getResultBitmap0();

    //					showBarcodeResultDaialog(img, result, ai, CHECK_UNREGISTERED);

    					//資産情報確認
    					int check_result = 0;
    					Long assetID = Long.parseLong(result.getText().substring(2,12));
    					check_result = checkAssetInfo(assetID);

    					switch ( check_result ) {
    					case AssetInfo.NORECORD:		//未登録
    					case AssetInfo.CHECK_OK:				//確認OK登録済み
    					case AssetInfo.CHECK_NG:				//確認NG登録済み
    						//確認ダイアログ表示
                            showBarcodeResultDaialog(img, assetID, ai, check_result);
    						break;
    					case AssetInfo.NO_CHECK:					//未確認
    						//資産情報あり＋未確認
    						//確認結果登録
    						updateCheckResult(assetID);
    						mBarcodReaderActivity.showReEncodeDialog();
    						break;

    					}
				    } else {
	                    mBarcodReaderActivity.showIncorrectDialog(code.toString());
				    }

				} else {
					// EAN13形式意外のバーコードの場合は
					// 警告ダイアログを表示する
					mBarcodReaderActivity.showIncorrectDialog(code.toString());
				}
			} else {
				Log.i(TAG, "preview failed");
				if (previewCount < TRY_PREVIEW) {
					Log.i(TAG, "retry preview");
					previewCount++;
					requestPreview();
				} else {
					Log.i(TAG, "give up preview");
					previewCount = 0;
					// 解析不能なので、再びオートフォーカスで
					// 画像取得しなおす
					requestAutoFocus();
				}
			}
		}
	};

	/**
	 * PreviewCallbackとAutoFocusCallbackの相互呼出しの形となる
	 */
	private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			Log.i(TAG, "onAutoFocus");
			if(camera != null){
				camera.autoFocus(null);
				// プレビュー画像を取得してバーコード解析する
				requestPreview();
			}
		}
	};

//	/* (非 Javadoc)
//	 * @see android.content.DialogInterface.OnDismissListener#onDismiss(android.content.DialogInterface)
//	 */
//	public void onDismiss(DialogInterface dialog) {
//		// TODO 自動生成されたメソッド・スタブ
//		Log.v(TAG,String.format("onDismiss dialog.toString=%s,",dialog.toString()));
//	}

	/**
	 * 現在の位置情報をセットする
	 * @param assetInfo
	 */
	private void setLocateion(AssetInfo assetInfo){

		//位置情報の取得
		GPSLocation gps = new GPSLocation(mBarcodReaderActivity);
		double locationLat = 0;
		double locationLon = 0;
		if(gps.getmLocation() != null){
			locationLat = gps.getmLocationLat();
			locationLon = gps.getmLocationLon();
		}

		assetInfo.setLatitude(locationLat);
		assetInfo.setLongitude(locationLon);

	}

}
