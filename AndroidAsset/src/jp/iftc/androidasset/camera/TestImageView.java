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

package jp.iftc.androidasset.camera;

import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetImage;
import jp.iftc.androidasset.db.AssetImageDAO;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * @author 0a6055
 *
 */
public class TestImageView extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = TestImageView.class.getSimpleName();
	private final TestImageView self = this;

	public final static String RECORD_ID = "id";

	private Long mRecId;

	private MatrixImageView  imgView;
	private MatrixImageView  imgView2;

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.iftc.androidasset.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_imageview);

		imgView = (MatrixImageView) findViewById(R.id.img_view);
		imgView2 = new MatrixImageView(this);

		imgView2.setBackgroundResource(R.color.transparent);

		Intent intent = getIntent();
		mRecId = intent.getLongExtra(RECORD_ID, 0);

		Bitmap bitmap = loadImageData(mRecId);
		if (bitmap != null){
			imgView.setImageBitmap(bitmap);
		} else {
			return;
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.img_layout);
		layout.addView(imgView2);


	}

	private Bitmap loadImageData(Long id){
		AssetImageDAO dao = new AssetImageDAO(this);
		Bitmap bitmap = null;
		AssetImage img = dao.loadAssetImage(id);

		if(img != null)
			bitmap = img.getAssetImageBitmap();

		return bitmap;

	}

}

