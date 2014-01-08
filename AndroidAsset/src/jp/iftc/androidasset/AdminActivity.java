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

import jp.iftc.androidasset.fileio.FileExportTask;
import jp.iftc.androidasset.fileio.FileImportTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 0a6055
 *
 */
public class AdminActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = AdminActivity.class.getSimpleName();
	@SuppressWarnings("unused")
	private final AdminActivity self = this;

	private Button btn_import;
	private Button btn_export;

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.iftc.androidasset.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin);

		btn_import = (Button) findViewById(R.id.btn_import);
		btn_export = (Button) findViewById(R.id.btn_export);

		btn_import.setOnClickListener(this);
		btn_export.setOnClickListener(this);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch (v.getId()) {
		case R.id.btn_import:
			// インポート
			Log.v(TAG, "btn_import click!");
			// データ取得タスクの実行
			FileImportTask inpTask = new FileImportTask(this);
			inpTask.execute();
			break;
		case R.id.btn_export:
			// エクスポート
			Log.v(TAG, "btn_export click!");
			// データ取得タスクの実行
			FileExportTask expTask = new FileExportTask(this);
			expTask.execute();
			break;
		default:
			break;
		}
	}
}
