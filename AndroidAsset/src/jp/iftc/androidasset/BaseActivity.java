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

package jp.iftc.androidasset;

import com.google.android.maps.MapActivity;

import jp.iftc.androidasset.camera.CameraTest;
import jp.iftc.androidasset.camera.TestImageView;
import jp.iftc.androidasset.fileio.FileExportTask;
import jp.iftc.androidasset.fileio.FileImportTask;
import jp.iftc.androidasset.gallery.ImageExplorer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author 0a6055 AndroidAssetアプリケーション　Activityベースクラス
 *
 */
public class BaseActivity extends MapActivity {
	private static final String TAG = BaseActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);

        return true;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        int itemid = item.getItemId();
		switch (itemid) {
		case R.id.menu_manage:		//ファイル
			Log.v(TAG,"ファイル click!");
			//管理画面
//            intent = new Intent(getApplicationContext(), AdminActivity.class);
//            startActivity(intent);
			break;
		case R.id.menu_barcode:		//バーコード読み取り
			//バーコード読取画面
			Log.v(TAG,"btn_barcode click!");

			intent = new Intent(getApplicationContext(), BarcodeReaderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

			break;
		case R.id.menu_list:		//一覧
			//一覧画面
            intent = new Intent(getApplicationContext(), AssetsListScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			Log.v(TAG,"btn_assetlist click!");
			break;
		case R.id.item4:		//終了
			Log.v(TAG,"終了 click!");
//			finish();
			moveTaskToBack(true);
			break;
		case R.id.admin_import:
			// インポート
			Log.v(TAG, "btn_import click!");
			// データ取得タスクの実行
			FileImportTask inpTask = new FileImportTask(this);
			inpTask.execute();
			break;
		case R.id.admin_export:
			// エクスポート
			Log.v(TAG, "btn_export click!");
			// データ取得タスクの実行
			FileExportTask expTask = new FileExportTask(this);
			expTask.execute();
			break;
		case R.id.menu_camera:
			// エクスポート
			Log.v(TAG, "btn_export click!");
			// カメラActivity
			intent = new Intent(getApplicationContext(), CameraTest.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			break;
		case R.id.menu_gallery:
			// GalleryActivity
			Log.v(TAG, "btn_export click!");
			//ImageExplorer
			intent = new Intent(getApplicationContext(), ImageExplorer.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			break;
		default:
			Log.v(TAG,"else click!?");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/* (非 Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
