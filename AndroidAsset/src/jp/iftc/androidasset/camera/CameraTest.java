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

import jp.iftc.androidasset.BaseActivity;
import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetImage;
import jp.iftc.androidasset.db.AssetImageDAO;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author 0a6055
 *
 */
public class CameraTest extends BaseActivity {
	@SuppressWarnings("unused")
	private static final String TAG = CameraTest.class.getSimpleName();
	private final CameraTest self = this;

	static final int REQUEST_CAPTURE_IMAGE = 100;

	Button btnTakePicture;
	Button btnSavePicture;
	Button btnLoadPicture;
	ImageView imageView1;
	Bitmap capturedImage;
	AssetImage mAssetImage;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		capturedImage = null;
		mAssetImage = new AssetImage();
		setContentView(R.layout.camera_test);
		findViews();
		setListeners();
	}

	protected void findViews(){
		btnTakePicture = (Button)findViewById(R.id.take_picture);
		btnSavePicture = (Button)findViewById(R.id.save_picture);
		btnLoadPicture = (Button)findViewById(R.id.load_picture);
		imageView1 = (ImageView)findViewById(R.id.imageView1);
	}

	protected void setListeners(){
		btnTakePicture.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(
					intent,
					REQUEST_CAPTURE_IMAGE);
			}
		});
		btnSavePicture.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {

				Log.v(TAG, "btnSavePicture OnClick!------");
				savePicture(capturedImage);
			}

			private void savePicture(Bitmap image) {

				if(image == null) return;

				mAssetImage.setAssetImageBitmap(image);

				AssetImageDAO dao = new AssetImageDAO(self);
				mAssetImage = dao.save(mAssetImage);

			}
		});

		btnLoadPicture.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {

				Log.v(TAG, "btnLoadPicture OnClick!------");
				Long id = mAssetImage.getId();
				if ( id == null ){
					id = Long.getLong("1");
				}

				//ImageView
				Intent intent = new Intent(getApplicationContext(), TestImageView.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(TestImageView.RECORD_ID, id);
	            startActivity(intent);

			}
		});
	}

	@Override
	protected void onActivityResult(
		int requestCode,
		int resultCode,
		Intent data) {
		if(REQUEST_CAPTURE_IMAGE == requestCode
			&& resultCode == Activity.RESULT_OK ){
			capturedImage =
				(Bitmap) data.getExtras().get("data");

			imageView1.setImageBitmap(capturedImage);
		}
	}
}
