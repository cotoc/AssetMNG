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

import jp.iftc.androidasset.AssetNarrowDialogFragment.OnNarrowSelectedListener;
import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.fileio.FileImportTask.ImportTaskCallback;
import jp.iftc.androidasset.flick.FlickControlFragment;
import jp.iftc.androidasset.flick.FlickControlFragment.OnPageChangeListener;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 0A7044 一覧表示画面
 */
public class AssetsListScreenActivity extends BaseActivity implements
		OnNarrowSelectedListener, OnPageChangeListener, ImportTaskCallback {
	private static final String TAG = AssetsListScreenActivity.class
			.getSimpleName();
	private final AssetsListScreenActivity self = this;

	public static final int PAGE_MAX_ROW = 50;
    public static final String NARROW_NULL = "－－－";


	private String mManagement_Groupe = NARROW_NULL;
	private String mInstallation_Location = NARROW_NULL;
	private String mCheck_Result = NARROW_NULL;
	private final String mNarrowColumns[] = {
			AssetInfo.COLUMN_MANAGEMENT_GROUP,
			AssetInfo.COLUMN_INSTALLATION_LOCATION,
			AssetInfo.COLUMN_CHECK_RESULT };
	// private AssetListFragment mListFragment;
	private FlickControlFragment mFlickControlFragment;
	private FrameLayout mFripperHolder;
	private TextView mTxtMaxNum;
	private TextView mTxtNarrows;
	private TextView mDispRowNumber;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		 Log.w(TAG, "onCreate");

		try {
			// レイアウトの指定
			setContentView(R.layout.assetelistscreen_layout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		mFripperHolder = (FrameLayout) findViewById(R.id.FripperHolder);

		mFlickControlFragment = FlickControlFragment.newInstance(PAGE_MAX_ROW);

		fragmentTransaction.replace(R.id.FripperHolder, mFlickControlFragment);
		fragmentTransaction.commit();

		mTxtMaxNum = (TextView) self.findViewById(R.id.footertext_maxnumber);
		mTxtNarrows = (TextView) self.findViewById(R.id.footertext_narrows);
		mDispRowNumber = (TextView) self
				.findViewById(R.id.footertext_disprownumber);

		// setFooterText();

		Button btn1 = (Button) self.findViewById(R.id.btn_footer_narrow);
		btn1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showNarrowDialog();
			}
		});

		Button btn2 = (Button) self.findViewById(R.id.btn_footer_narrowclear);
		btn2.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mManagement_Groupe = NARROW_NULL;
				mInstallation_Location = NARROW_NULL;
				mCheck_Result = NARROW_NULL;

				mFlickControlFragment.loadData(null, null, true);

				setFooterText();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.w(TAG, "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onResume() {
		super.onResume();
		 Log.w(TAG, "onResume");

        // 一覧のデータ読み込み
        String[] narrowData = new String[3];

        narrowData[0] = mManagement_Groupe;
        narrowData[1] = mInstallation_Location;
        narrowData[2] = mCheck_Result;

        mFlickControlFragment.loadData(mNarrowColumns, narrowData, false);
        setFooterText();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.w(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Log.w(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		 Log.w(TAG, "onDestroy");

		 Log.d(TAG, "*** getChangingConfigurations() :" + getChangingConfigurations() + " (" + String.format("0x%08x", getChangingConfigurations()) + ")");


//		if( getChangingConfigurations() == ( ActivityInfo.CONFIG_ORIENTATION |  ActivityInfo.CONFIG_SCREEN_SIZE )){
//			Log.d(TAG, "-- AssetsListScreenActivity onDestroy : CONFIG_ORIENTATION");
//			if(mFlickControlFragment.mMapDialog == null ) return;
//
//			if( mFlickControlFragment.mMapDialog.isVisible() ) mFlickControlFragment.mMapDialog.dismiss();
//		}


	}

    @Override
	protected void onSaveInstanceState(Bundle savedInstanceState ){
		 Log.d(TAG, "*** onSaveInstanceState() ");

		 // Instance保存前に、MapDilogをdismissする。
		 mFlickControlFragment.closeDialog();

		super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("Management_Groupe", mManagement_Groupe);
        savedInstanceState.putString("Installation_Location", mInstallation_Location);
        savedInstanceState.putString("Check_Result", mCheck_Result);

        savedInstanceState.putInt("DispPage", mFlickControlFragment.getActivePage());
        savedInstanceState.putInt("Position", mFlickControlFragment.getSelectedPosition());
        savedInstanceState.putBoolean("MapDispState", mFlickControlFragment.getMapDipState());
	}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);

      mManagement_Groupe = savedInstanceState.getString("Management_Groupe", NARROW_NULL);
      mInstallation_Location = savedInstanceState.getString("Installation_Location", NARROW_NULL);
      mCheck_Result = savedInstanceState.getString("Check_Result", NARROW_NULL);

      mFlickControlFragment.setActivePage(savedInstanceState.getInt("DispPage"));
      mFlickControlFragment.setSelectedPosition(savedInstanceState.getInt("Position"));
      mFlickControlFragment.setMapDipState(savedInstanceState.getBoolean("MapDispState"));

    }


	// 絞込みダイアログの表示
	private void showNarrowDialog() {
		int index = 0;
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("NarrowDialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		DialogFragment df = AssetNarrowDialogFragment.newInstance(index);
		df.show(ft, "NarrowDialog");
	}

	public void onNarrowSelected(String[] narrowData) {
		DialogFragment df = (DialogFragment) getFragmentManager()
				.findFragmentByTag("NarrowDialog");

		if (df != null) {
			mManagement_Groupe = narrowData[0];
			mInstallation_Location = narrowData[1];
			mCheck_Result = narrowData[2];

			df.dismiss();

			mFlickControlFragment.loadData(mNarrowColumns, narrowData, true);

			setFooterText();
		} else {
			Log.w(TAG, "NarrowDialog is missing");
		}
	}

	public void onPageChange(int num) {
		setFooterText();
	}

	public void setFooterText() {
		String buf = "";
		String Management_Groupe = mManagement_Groupe;
        String Installation_Location = mInstallation_Location;
        String Check_Result = getCheckResultChar(mCheck_Result);

		mTxtMaxNum.setText("全 "
				+ String.valueOf(mFlickControlFragment.getListCount()) + "件");

		if (Management_Groupe == NARROW_NULL)
			Management_Groupe = " ";
		if (Installation_Location == NARROW_NULL)
			Installation_Location = " ";
		if (Check_Result == NARROW_NULL)
			Check_Result = " ";

		if (Management_Groupe == " " && Installation_Location == " "
				&& Check_Result == " ") {
			mTxtNarrows.setText("条件なし");
		} else {
			buf += Management_Groupe;

			if (buf.equals(" ")) {
				buf += Installation_Location;
			} else if (Installation_Location != " ") {
				buf += " / " + Installation_Location;
			}

			if (buf.equals("  ")) {
				buf += Check_Result;
			} else if (Check_Result != " ") {
				buf += " / " + Check_Result;
			}

			mTxtNarrows.setText(buf);
		}
		int start;
		int end;

		if (mFlickControlFragment.getListCount() != 0) {
			start = (mFlickControlFragment.getActivePage() - 1) * PAGE_MAX_ROW
					+ 1;
			if (mFlickControlFragment.getActivePage() == mFlickControlFragment
					.getPageTotal()) {
				end = mFlickControlFragment.getListCount();
			} else {
				end = mFlickControlFragment.getActivePage() * PAGE_MAX_ROW;
			}
		} else {
			start = 0;
			end = 0;
		}
		mDispRowNumber.setText(Integer.toString(start) + "～"
				+ Integer.toString(end) + "件を表示中 / ");

	}

	public String getCheckResultChar(String num) {
		// 確認結果の文字列　OK(1),NG(2),未(0,null)
		if (num.equals("0")) {
			return getString(R.string.detailetitle_alert_check_unconfirmed);
		} else if (num.equals("1")) {
			return getString(R.string.detailetitle_btnOK);
		} else if (num.equals("2")) {
			return getString(R.string.detailetitle_btnNG);
		} else {
			return num;
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menu_list = (MenuItem) menu.findItem(R.id.menu_list);
		if(menu_list != null)		menu_list.setVisible(false);

		return true;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * jp.iftc.androidasset.fileio.FileImportTask.ImportTaskCallback#onSuccessImport
	 * (java.lang.Integer)
	 */
	public void onSuccessImport(Integer recCount) {
		// TODO 自動生成されたメソッド・スタブ
		mFlickControlFragment.loadData(null, null, true);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * jp.iftc.androidasset.fileio.FileImportTask.ImportTaskCallback#onFailedImport
	 * (int)
	 */
	public void onFailedImport(int resId) {
		// TODO 自動生成されたメソッド・スタブ
		Toast.makeText(this, "Import Error", Toast.LENGTH_LONG).show();

	}


}
