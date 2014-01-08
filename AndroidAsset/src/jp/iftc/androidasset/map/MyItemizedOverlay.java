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

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author 0a6055
 *
 */
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private List<GeoPoint> points = new ArrayList<GeoPoint>();

	/**
	 * @param defaultMarker
	 */
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		populate();

	}

	@SuppressWarnings("unused")
	private static final String TAG = MyItemizedOverlay.class.getSimpleName();
	private final MyItemizedOverlay self = this;

	/*
	 * (非 Javadoc)
	 *
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		// TODO 自動生成されたメソッド・スタブ
		return mOverlays.size();
	}

	public void addPin(GeoPoint point, String title, String snippet) {
		mOverlays.add(new OverlayItem(point, title, snippet));
		setLastFocusedIndex(-1);
		populate();

    }

//    public void addPoint(GeoPoint point) {
//        this.points.add(point);
//        populate();
//    }
//
    public void clearPoint() {
        mOverlays.clear();
        setLastFocusedIndex(-1);
        populate();
    }
}
