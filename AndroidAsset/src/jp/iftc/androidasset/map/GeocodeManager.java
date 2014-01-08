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
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

/**
 * GeoCodeから住所に変換する
 * @author 0a6055
 *
 */
public class GeocodeManager {
	@SuppressWarnings("unused")
	private static final String TAG = GeocodeManager.class.getSimpleName();
	private final GeocodeManager self = this;

	// 座標から住所文字列へ変換
	public static String point2address(double latitude, double longitude, Context context)
		throws IOException
	{
		String address_string = new String();

		// 変換実行
		Geocoder coder = new Geocoder(context, Locale.JAPAN);
		List<Address> list_address = coder.getFromLocation(latitude, longitude, 1);

		if (!list_address.isEmpty()){

			// 変換成功時は，最初の変換候補を取得
			Address address = list_address.get(0);
			StringBuffer sb = new StringBuffer();

			// adressの大区分から小区分までを改行で全結合
			String s;
			for (int i = 0; (s = address.getAddressLine(i)) != null; i++){
				sb.append( s + "\n" );
			}

			address_string = sb.toString();
		}

		return address_string;
	}
}
