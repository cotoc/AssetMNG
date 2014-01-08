/*
 * Copyright © 2012 Infotec Inc. All Rights Reserved.
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

package jp.iftc.androidasset.map;

import java.io.IOException;
import java.util.List;

import jp.iftc.androidasset.R;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * @author 0a6055
 *
 */
public class AssetMapDialogFragment extends DialogFragment implements
		android.content.DialogInterface.OnClickListener {
	private static final String TAG = AssetMapDialogFragment.class
			.getSimpleName();
	@SuppressWarnings("unused")
	private final AssetMapDialogFragment self = this;

	public static final boolean DYNAMIC_LOCATION = true; // 現在位置情報表示
	public static final boolean STATIC_LOCATION = false; // 指定位置情報表示

	private MapView mMapView;
	private MapController mapController;
	private double mLocationLat;
	private double mLocationLon;
	// public PositionOverlay mPositionOverlay;
	private FrameLayout mMapHolder;
	public MyItemizedOverlay mItemizedOverlay;

	private TextView mAddress;

	/**
	 * Create a new instance of as an argument.
	 */
	public static AssetMapDialogFragment newInstance(boolean getLocateFlg,
			double lat, double lon) {
		AssetMapDialogFragment f = new AssetMapDialogFragment();

		// パラメータを格納する
		Bundle args = new Bundle();

		args.putBoolean("getLocateFlg", getLocateFlg);
		args.putDouble("lat", lat);
		args.putDouble("lon", lon);
		args.putLong("asset_id", -1);
		f.setArguments(args);

		return f;
	}

	/**
	 * Create a new instance of as an argument.
	 */
	public static AssetMapDialogFragment newInstance(boolean getLocateFlg,
			double lat, double lon, long assetID) {

		AssetMapDialogFragment f = new AssetMapDialogFragment();

		// パラメータを格納する
		Bundle args = new Bundle();

		args.putBoolean("getLocateFlg", getLocateFlg);
		args.putDouble("lat", lat);
		args.putDouble("lon", lon);
		args.putLong("asset_id", assetID);
		f.setArguments(args);

		return f;
	}

//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		super.onCreateDialog(savedInstanceState);
//
//		//
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		View view = inflater.inflate(R.layout.map_fragment, null, false);
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//		mMapHolder = (FrameLayout) view.findViewById(R.id.mapHolder);
//		mAddress = (TextView) view.findViewById(R.id.text_address);
//
////		builder.setPositiveButton("OK", this);
//		Button btn = (Button) view.findViewById(R.id.btn_map_ok);
//        btn.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//            	dismiss(); // ダイアログを閉じる
//            }
//        });
//		Button btn_save = (Button) view.findViewById(R.id.btn_map_save);
//		btn_save.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//            	//地図の画像を保存
//            }
//        });
//
//		builder.setView(view);
//
//		return builder.create();
//	}

	/* (非 Javadoc)
	 * @see android.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	/* (非 Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment, null, false);

		mMapHolder = (FrameLayout) view.findViewById(R.id.mapHolder);
		mAddress = (TextView) view.findViewById(R.id.text_address);
		Button btn = (Button) view.findViewById(R.id.btn_map_ok);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	dismiss(); // ダイアログを閉じる
            }
        });
		Button btn_returm = (Button) view.findViewById(R.id.btn_map_return);
		btn_returm.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	//表示している場所を元に戻す。
            	updateWithNewLocation();
            }
        });

		Button btn_save = (Button) view.findViewById(R.id.btn_map_save);
		if(getArguments().getLong("asset_id") < 0){
			LinearLayout liner = (LinearLayout)  view.findViewById(R.id.liner_btn);
			liner.removeView(btn_save);
		} else {
			btn_save.setOnClickListener(new OnClickListener() {
	            public void onClick(View view) {
	            	//地図の画像を保存
	            	SaveMapBitmap savemap = new SaveMapBitmap(getActivity());
	            	savemap.setScreen(mMapView);
	            	savemap.saveScreen();

	            }
	        });
		}

        return view;
	}

	/**
	 * @param location
	 */
	private void updateWithNewLocation() {

		if (getArguments().getBoolean("getLocateFlg") == DYNAMIC_LOCATION) {
			// 位置情報の取得
			GPSLocation gps = new GPSLocation(this.getActivity());
			mLocationLat = gps.getmLocationLat();
			mLocationLon = gps.getmLocationLon();
		} else {
			mLocationLat = getArguments().getDouble("lat");
			mLocationLon = getArguments().getDouble("lon");
		}

		Log.v(TAG,
				"----updateWithNewLocation---- lat:"
						+ Double.toString(mLocationLat) + " lon:"
						+ Double.toString(mLocationLon));

		// mPositionOverlay.setCurrentLocation(mLocationLat, mLocationLon);

		Double lat = mLocationLat * 1E6;
		Double lon = mLocationLon * 1E6;
		GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());

		mItemizedOverlay.clearPoint();
		mItemizedOverlay.addPin(point, "", "");

		// 指定位置までスクロール
		mapController.animateTo(point);

		mMapView.invalidate();

		String address = "[位置情報からの住所取得に失敗しました。]";

		try {
			address = GeocodeManager.point2address(mLocationLat, mLocationLon,getActivity());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		mAddress.setText(address);
	}

	/**
	 * @param location
	 */
	public void setLocation(double lat, double lon) {

		Log.v(TAG, "----setLocation---- lat:" + Double.toString(lat) + " lon:"
				+ Double.toString(lon));

		// パラメータを格納する
		Bundle args = new Bundle();

		args.putDouble("lat", lat);
		args.putDouble("lon", lon);
		args.putLong("asset_id", -1);
		setArguments(args);
	}

	/**
	 * @param location
	 */
	public void setLocation(double lat, double lon, long assetId) {

		Log.v(TAG, "----setLocation---- lat:" + Double.toString(lat) + " lon:"
				+ Double.toString(lon));

		// パラメータを格納する
		Bundle args = new Bundle();

		args.putDouble("lat", lat);
		args.putDouble("lon", lon);
		args.putLong("asset_id", assetId);
		setArguments(args);
	}

	public void onDestroyView() {

		super.onDestroyView();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.content.DialogInterface.OnClickListener#onClick(android.content
	 * .DialogInterface, int)
	 */
	public void onClick(DialogInterface dialog, int which) {

		dismiss(); // ダイアログを閉じる
	}


	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.content.DialogInterface.OnDismissListener#onDismiss(android.content
	 * .DialogInterface)
	 */
	public void onDismiss(DialogInterface dialog) {

		((ViewGroup) mMapView.getParent()).removeView(mMapView);

		super.onDismiss(dialog);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		mMapView.requestLayout();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.DialogFragment#onStart()
	 */
	@Override
	public void onStart() {

		Log.v(TAG, "----onStart----");

		String apiKey = "";
//API Keyをリリース/デバッグで使い分ける方法。
//        if(isDebugAble(getActivity())){
//        	Log.v(TAG, "debug");
//        	apiKey = getString(R.string.map_api_key_debug);
//        }else{
//        	Log.v(TAG, "release");
//        	apiKey = getString(R.string.map_api_key);
//        }

        apiKey = getString(R.string.map_api_key);

        if (mMapView == null) {
			mMapView = new MapView(getActivity(), apiKey);
			mMapView.setClickable(true);
			// 地図表示設定
			mMapView.setSatellite(false);
			// mMapView.setStreetView(false);
//↓ これがTrueだと、Map表示 → Touch → 閉じる → 再表示 → Touch で落ちます。
//			mMapView.setBuiltInZoomControls(true);


			mMapView.setOnTouchListener(new OnTouchListener() {
			    public boolean onTouch(View v, MotionEvent event) {
			        return false;
			    }

			});
			mMapView.setDrawingCacheEnabled(true);


		}

        if(this.mMapHolder != null){
        	if( mMapHolder.indexOfChild(mMapView)  > 0 ){
        		((ViewGroup) mMapView.getParent()).removeView(mMapView);
        	}
//			LayoutParams params = new MapView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0, 0, Alignment.ALIGN_NORMAL);
        	this.mMapHolder.addView(mMapView, 0);
        }




		if (mapController == null) {
			mapController = mMapView.getController();
			// 地図の拡大率を設定する
			mapController.setZoom(17);
		}

		// if(mPositionOverlay == null){
		// //地図への情報表示
		// mPositionOverlay = new PositionOverlay();
		//
		// List<Overlay> overlays = mMapView.getOverlays();
		// overlays.add(mPositionOverlay);
		// }

		if (mItemizedOverlay == null) {
			Drawable pin = getResources().getDrawable(
					R.drawable.ic_maps_indicator_current_position);
			// 地図への情報表示
			mItemizedOverlay = new MyItemizedOverlay(pin);

			List<Overlay> overlays = mMapView.getOverlays();
			overlays.add(mItemizedOverlay);
		}

		updateWithNewLocation();

		if(getArguments().getLong("asset_id") < 0){
			LinearLayout liner = (LinearLayout)  this.getView().findViewById(R.id.liner_btn);
			Button btn_save = (Button) this.getView().findViewById(R.id.btn_map_save);
			liner.removeView(btn_save);
		}

		// TODO 自動生成されたメソッド・スタブ
		super.onStart();
	}


	public static boolean  isDebugAble(Context ctx){
		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = manager.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(TAG,e.toString());
			return false;
		}
		if((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) return true;
		return false;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.DialogFragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v(TAG, "----onStop----");

		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
	}
}
