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

package jp.iftc.androidasset.fileio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author 0a6055
 *
 */
public class FileExportTask extends AsyncTask<Object, Integer, Integer>
		implements OnCancelListener {
	private static final String TAG = FileExportTask.class.getSimpleName();
	@SuppressWarnings("unused")
	private final FileExportTask self = this;

	// 処理中ダイアログ
	private ProgressDialog progressDialog = null;

	/** 呼出元のActivity */
	private Activity activity = null;

	private FileExport fileExp;

	/**
	 * コンストラクタです。 UIスレッド処理です。
	 *
	 * @param activity
	 *            Activity
	 */
	public FileExportTask(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		// バックグラウンドの処理前にUIスレッドでダイアログ表示
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("Please wait");
		progressDialog.setMessage("エクスポートしています・・・");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(this);
		// キャンセルボタン処理を設定する
		progressDialog.setButton("キャンセル",
				new ProgressDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//キャンセルフラグ ON
						fileExp.setCancelFlg(true);
						cancel(true);
					}
				});
		progressDialog.show();
	}

	@Override
	protected Integer doInBackground(Object... params) {
		// ファイルのエクスポートをバックグラウンドで実行
		int recCount;
		fileExp = new FileExport(activity);
		recCount = fileExp.xmlFileExport();
		return recCount;
	}

	@Override
	protected void onPostExecute(Integer recCount) {
		// 処理中ダイアログをクローズ
		progressDialog.dismiss();

		//
		// 終了ダイアログを表示します。
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		// タイトルを設定する
		alertDialog.setTitle("エクスポート");
		if (recCount < 0) {
			String message = null;
			switch (recCount){
			case FileExport.DBACCESS_ERROR:
				message = "出力データの抽出に失敗しました。";
				break;
			case FileExport.SD_NOT_READY:
				message = "SDカードが挿入されていないか、読み取り専用です。";
				break;
			case FileExport.FILE_NOTFOUND:
				message = "エクスポートするディレクトリが存在しません。";
				break;
			default:
				// メッセージ内容を設定する  --Error--
				message = "ファイルのインポートが異常終了しました。";
				break;
			}			// メッセージ内容を設定する --Error--
			alertDialog.setMessage(message);

		} else {
			// メッセージ内容を設定する
			alertDialog.setMessage("エクスポートが完了しました。　出力件数："
					+ Integer.toString(recCount));
		}

		// 確認ボタン処理を設定する
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						activity.setResult(Activity.RESULT_OK);
					}
				});
		alertDialog.create();
		alertDialog.show();
	}

	@Override
	protected void onCancelled() {
		Log.d(TAG, "onCancelled");
		//
		// 処理：ダイアログを表示します。
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		// タイトルを設定する
		alertDialog.setTitle("エクスポート");
		// メッセージ内容を設定する --キャンセル--
		alertDialog.setMessage("エクスポートをキャンセルしました。");
		// 確認ボタン処理を設定する
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						activity.setResult(Activity.RESULT_OK);
					}
				});
		alertDialog.create();
		alertDialog.show();
		progressDialog.dismiss();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.content.DialogInterface.OnCancelListener#onCancel(android.content
	 * .DialogInterface)
	 */
	public void onCancel(DialogInterface dialog) {
		//キャンセルフラグ ON
		fileExp.setCancelFlg(true);
		this.cancel(true);
	}
}
