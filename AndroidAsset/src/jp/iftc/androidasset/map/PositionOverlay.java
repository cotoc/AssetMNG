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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * @author 0a6055
 *
 */
public class PositionOverlay extends Overlay {
	@SuppressWarnings("unused")
	private static final String TAG = PositionOverlay.class.getSimpleName();
	@SuppressWarnings("unused")
	private final PositionOverlay self = this;

	private double mCurrentLatitude;
	private double mCurrentLongitude;

	/**
	 *
	 */
	public void setCurrentLocation(double latitude, double longitude) {
		mCurrentLatitude = latitude;
		mCurrentLongitude = longitude;
	}

	/* (非 Javadoc)
	 * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// 現在地を描画
		drawCurrentLocation(canvas, mapView, shadow);
		super.draw(canvas, mapView, shadow);
	}

	private final int mRadius = 5;

	/**
	 * @param canvas
	 * @param mapView
	 * @param shadow
	 */
	private void drawCurrentLocation(Canvas canvas, MapView mapView,
			boolean shadow) {
		if(shadow == false){

			Projection projection = mapView.getProjection();

			Double lat = mCurrentLatitude * 1E6;
			Double lon = mCurrentLongitude * 1E6;

			//GeoPointを作成
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lon.intValue());

			//画面の解像度に合わせて描画位置を変換する
			Point point = new Point();
			projection.toPixels(geoPoint, point);

			//描画範囲設定
			RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius);

			//背景を設定
			RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 65, point.y + mRadius);

			//描画設定
			Paint paint = new Paint();
			paint.setARGB(255, 180, 180, 180);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);

			Paint backPaint = new Paint();
			backPaint.setARGB(230, 50, 50, 50);
			backPaint.setAntiAlias(true);

			//現在地を描画
			canvas.drawOval(oval, paint);
			canvas.drawRoundRect(backRect, 5, 5, backPaint);
			canvas.drawText("ここ", point.x + 2 * mRadius, point.y, paint);

		}

	}

	/* (非 Javadoc)
	 * @see com.google.android.maps.Overlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return false;
	}
}
