/*
   Copyright 2009 adamrocker

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

// CameZonActivity.java
package jp.iftc.androidasset;

import jp.iftc.androidasset.R;
import jp.iftc.androidasset.barcode.FinderView;
import jp.iftc.androidasset.barcode.Preview;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 画像のプレビューからバーコードを取得し、Amazonにデータを探しにいく
 * -- バーコードリーダ　サンプルActivity --
 * @author 0a6055
 *
 */
public class BarcodeReaderActivity extends BaseActivity {
    @SuppressWarnings("unused")
    private static final String TAG = AssetsListScreenActivity.class.getSimpleName();
    private final BarcodeReaderActivity self = this;

 	private static final int INCORRECT_BARCODE_TYPE = 0;
 	private static final int ENCODE_SUCCESS = 1;

	private FinderView mFinderView;
	private Preview mPreview;
    boolean mDualPane;

	// メソッド間の受け渡しのためだけに使う
	private String mBarcodeType;
	// バーコードタイプが解析対象外の場合に表示するダイアログの説明文
	private String mIncorrectBarcodeError;

	/* (非 Javadoc)
	 * @see jp.iftc.androidasset.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 画面を明るい状態に保つ
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

      setContentView(R.layout.barcode_reader);
//        setContentView(R.layout.assetcamerascreen_layout);

		// プレビュー用のファインダーを表示する
		mFinderView = (FinderView) findViewById(R.id.viewfinder_view);
		mPreview = (Preview) findViewById(R.id.preview_surface);
		mPreview.setFinder(mFinderView);

		requestAutoFocus();

		mIncorrectBarcodeError = "Incorrect barcode type. Only EAN-13 is analyzable.";

//        // 詳細情報用の fragment があるかどうかチェック
//        View detailsFrame = findViewById(R.id.assetdetails);
//        mDualPane = detailsFrame != null
//                && detailsFrame.getVisibility() == View.VISIBLE;
//
//        // 縦置き時は詳細はいらない & カメラ用初期表示
//        if ( mDualPane ) showDetails(-1);

	}

//    //詳細の表示
//    private void showDetails(int index) {
//        Context context=getApplication();
//
//        //フラグメントの切り換え
//        if ( mDualPane ) {
//            AssetDetailFragment fragment=AssetDetailFragment.newInstance(index);
//            FragmentTransaction ft=getFragmentManager().beginTransaction();
//            ft.replace(R.id.assetdetails,fragment);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.commit();
//        }
//        //アクティビティの起動
//        else {
//            Intent intent=new Intent(context,AssetsDetailedScreenActivity.class);
//            intent.putExtra("index",index);
//            startActivity(intent);
//        }
//    }


	public void requestAutoFocus() {
		mPreview.requestAutoFocus();
	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
//		mPreview.closeCamera();
		Log.v(TAG,"onPause!");
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPreview.closeCamera();
	}

	// バーコードの内容が解析できな場合にダイアログを表示する
	public void showIncorrectDialog(String barcodeType) {
		mBarcodeType = barcodeType;
		showDialog(INCORRECT_BARCODE_TYPE);
	}

	// ダイアログの表示。ボタンが押されると再度解析を開始する
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INCORRECT_BARCODE_TYPE:
			return new AlertDialog.Builder(BarcodeReaderActivity.this)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle("不正なバーコードを読み込みました。バーコード読取を継続しますか？")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// オートフォーカス開始
									mPreview.requestAutoFocus();
								}
							})
					.setNegativeButton("CANCEL",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// オートフォーカス開始
//									mPreview.requestAutoFocus();
									finish();
								}
							}).create();

		case ENCODE_SUCCESS:		//バーコード解析成功後の再解析確認ダイアログの表示
			return new AlertDialog.Builder(BarcodeReaderActivity.this)
			.setIcon(R.drawable.alert_dialog_icon)
			.setTitle("バーコード読取を継続しますか？")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// オートフォーカス開始
							mPreview.requestAutoFocus();
						}
					})
			.setNegativeButton("CANCEL",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// このActivityを終了する。
//							mPreview.requestAutoFocus();
							finish();
						}
					}).create();

		}
		return null;
	}

	// バーコード解析完了後、再解析確認のダイアログを表示する
	public void showReEncodeDialog() {
		showDialog(ENCODE_SUCCESS);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menu_barcode = (MenuItem)menu.findItem(R.id.menu_barcode);
		menu_barcode.setVisible(false);

        return true;
	}
}