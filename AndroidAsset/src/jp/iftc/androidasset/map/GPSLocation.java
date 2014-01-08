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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * GPSデバイスから、現在の位置情報を取得する
 *
 * @author 0a6055
 *
 */
public class GPSLocation {

	private static final String TAG = GPSLocation.class.getSimpleName();
	@SuppressWarnings("unused")
	private final GPSLocation self = this;

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Context mContext;

	private Location mLocation;
	private double mLocationLat;
	private double mLocationLon;
	private String mProvider;

	private Timer locationTimer;
	long time;

	/**
	 *
	 */
	public GPSLocation(Context context) {

		mContext = context;
		mLocation = null;
		// GPS初期化
		initGPSDevice();
	}

	/**
	 * GPSデバイスの初期化
	 */
	private void initGPSDevice() {
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		// 位置情報機能非搭載端末の場合
		if (mLocationManager == null) {
			Log.v(TAG, "位置情報機能非搭載端末");
			return;
		}

		// 最適なGPS取得方法を選択
		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria .ACCURACY_FINE );
		criteria.setBearingRequired(false);		// 方位不要
		criteria.setSpeedRequired(false);		// 速度不要
		criteria.setAltitudeRequired(false);	// 高度不要
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
		mProvider = mLocationManager.getBestProvider(criteria, true);

		// 位置情報が無効になっている場合
		if (mProvider == null) {
			Log.v(TAG, "GPSデバイス OFF");
			return;
		}

//		// Toast の表示と LocationListener の生存時間を決定するタイマーを起動します。
//		locationTimer = new Timer(true);
//		time = 0L;
//		final Handler handler = new Handler();
//		final TimerTask gpsTask = new TimerTask() {
//			public void run() {
//				handler.post(new Runnable() {
//					public void run() {
//						if (time == 1000L) {
//							Toast.makeText(mContext, "現在地を特定しています。",
//									Toast.LENGTH_LONG).show();
//						} else if (time >= (60 * 1000L)) {
//							Toast.makeText(mContext, "現在地を特定できませんでした。",
//									Toast.LENGTH_LONG).show();
//							stopLocationService();
//						}
//						time = time + 1000L;
//					}
//				});
//			}
//		};
//
//		locationTimer.scheduleAtFixedRate(gpsTask, 0L, 1000L);
//
		// 位置情報の取得を開始します。
		mLocationListener = new LocationListener() {
			public void onLocationChanged(final Location location) {
				setLocation(location);
			}

			public void onProviderDisabled(final String provider) {

			}

			public void onProviderEnabled(final String provider) {

			}

			public void onStatusChanged(final String provider,
					final int status, final Bundle extras) {

			}
		};

		mLocationManager.requestLocationUpdates(mProvider, 60000, 0,
				mLocationListener);

		// 最後に取得できた位置情報が5分以内のものであれば有効とします。
		final Location lastKnownLocation = mLocationManager.getLastKnownLocation(mProvider);
//		// XXX - 必要により判断の基準を変更してください。
//		if (lastKnownLocation != null && (new Date().getTime() - lastKnownLocation.getTime()) <= (5 * 60 * 1000L)) {
//			setLocation(lastKnownLocation);
//			return;
//		}

		//とりあえず、古い情報でもよしとする。
		setLocation(lastKnownLocation);

	}

	/**
	 * @return mLocation
	 */
	public Location getmLocation() {
		return mLocation;
	}

	/**
	 * @return mLocationLat
	 */
	public double getmLocationLat() {
		return mLocationLat;
	}

	/**
	 * @return mLocationLon
	 */
	public double getmLocationLon() {
		return mLocationLon;
	}

	/**
	 * 位置情報再読み込み
	 */
	public void reLoadLocation() {
		// 位置情報の取得
		Location location = mLocationManager.getLastKnownLocation(mProvider);

		if (location != null) {
			// GPS情報を更新
			setLocation(location);
		} else {
			Log.v(TAG, "reLoadLocation--- 位置情報取得できず。");
		}
	}

	public void stopLocationService() {
		if (locationTimer != null) {
			locationTimer.cancel();
			locationTimer.purge();
			locationTimer = null;
		}
		if (mLocationManager != null) {
			if (mLocationManager != null) {
				mLocationManager.removeUpdates(mLocationListener);
				mLocationManager = null;
			}
			mLocationManager = null;
		}
	}

	private void setLocation(final Location location) {
		stopLocationService();

		if( location == null ){
			Log.v(TAG, "setLocation--- 位置情報が取得できませんでした。");
			this.mLocation = location;
			this.mLocationLat = 0;
			this.mLocationLon = 0;

		}

		this.mLocation = location;
		this.mLocationLat = location.getLatitude();
		this.mLocationLon = location.getLongitude();

		Log.v(TAG, "setLocation--- 位置情報特定  lat:" + Double.toString(mLocationLat));
	}

}
