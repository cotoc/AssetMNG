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

import jp.iftc.androidasset.db.AssetImage;
import jp.iftc.androidasset.db.AssetImageDAO;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * @author 0a6055
 *
 */
public class SaveMapBitmap {
	@SuppressWarnings("unused")
	private static final String TAG = SaveMapBitmap.class.getSimpleName();
	private final SaveMapBitmap self = this;

	private Context mContext;
	private Bitmap mBitmap;

	public SaveMapBitmap(Context context){
		mContext = context;
	}

	public void setScreen(View view){
	    View content = view;
	    mBitmap = content.getDrawingCache();

	}

	public void saveScreen(){

		AssetImage asImage = new AssetImage();
		asImage.setAssetImageBitmap(mBitmap);
		asImage.setType(AssetImage.TYPE_MAP);

		AssetImageDAO dao = new AssetImageDAO(mContext);
		asImage = dao.save(asImage);
	}

}
