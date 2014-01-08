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

import jp.iftc.androidasset.map.AssetMapDialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 0a6055
 *
 */
public class MainMenu extends BaseActivity implements OnClickListener {
	private static final String TAG = MainMenu.class.getSimpleName();
	@SuppressWarnings("unused")
	private final MainMenu self = this;

	private Button btn_barcode;
	private Button btn_assetlist;
	private Button btn_admin;
	private Button btn_end;

	private FragmentManager mFragmentManager;
	private AssetMapDialogFragment mMapDialog;
	/* (非 Javadoc)
	 * @see jp.iftc.androidasset.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);

		btn_barcode = (Button)findViewById(R.id.btn_barcode);
		btn_assetlist = (Button)findViewById(R.id.btn_assetlist);
		btn_admin = (Button)findViewById(R.id.btn_admin);
		btn_end = (Button)findViewById(R.id.btn_end);

		btn_barcode.setOnClickListener(this);
		btn_assetlist.setOnClickListener(this);
		btn_admin.setOnClickListener(this);
		btn_end.setOnClickListener(this);


		// 地図表示ダイアログを生成
		mFragmentManager = getFragmentManager();
		mMapDialog = AssetMapDialogFragment.newInstance(AssetMapDialogFragment.DYNAMIC_LOCATION,43.062574,141.353635);

	}

	/* (非 Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent;
		switch(v.getId()){
		case R.id.btn_barcode:
			//バーコード読取画面
			Log.v(TAG,"btn_barcode click!");
			intent = new Intent(getApplicationContext(), BarcodeReaderActivity.class);
            startActivity(intent);
			break;
		case R.id.btn_assetlist:
			//一覧画面
            intent = new Intent(getApplicationContext(), AssetsListScreenActivity.class);
            startActivity(intent);
			Log.v(TAG,"btn_assetlist click!");
			break;
		case R.id.btn_admin:
			//管理画面
            intent = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(intent);
			Log.v(TAG,"btn_admin click!");
			break;
		case R.id.btn_end:
//			//アプリケーション終了
			Log.v(TAG,"btn_end click!");
//			finish();
			FragmentManager manager = getFragmentManager();
			AssetMapDialogFragment dialog = AssetMapDialogFragment.newInstance(false,43.062574,141.353635);

			mMapDialog.setLocation(35.697404, 139.698478);

//			AssetInfo assetInfo = new AssetInfo();
//
//			assetInfo.setId( Long.parseLong( "99998"));
//			assetInfo.setCheckResult(Integer.toString(AssetInfo.CHECK_OK));
//			assetInfo.setCheckMemo("位置情報登録テスト2");
//
//			//未登録データの場合、InsertFlgを1に設定する
//			int insertFlg = 1;
//
//			AssetInfoDAO dao = new AssetInfoDAO(this);
//
//			AssetInfo newAssetInfo = dao.saveCheckResult(assetInfo, insertFlg);
//
//			mMapDialog.setLocation(newAssetInfo.getLatitude(), newAssetInfo.getLongitude());
//			mMapDialog.show(mFragmentManager, "dialog");

//			intent = new Intent(getApplicationContext(), FlickSampleActivity.class);
//            startActivity(intent);



			break;
		default:
			break;
		}
	}

//	/* (非 Javadoc)
//	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
//	 */
//	@Override
//	protected boolean isRouteDisplayed() {
//		// TODO 自動生成されたメソッド・スタブ
//		return false;
//	}

}
