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
import android.os.AsyncTask;

/**
 * @author 0a6055
 *
 */
public class FileImportTask extends AsyncTask<Object, Integer, Integer> {
	@SuppressWarnings("unused")
	private static final String TAG = FileImportTask.class.getSimpleName();
	@SuppressWarnings("unused")
	private final FileImportTask self = this;

	// 処理中ダイアログ
	private ProgressDialog progressDialog = null;

	/** 呼出元のActivity */
	private Activity activity = null;

	private ImportTaskCallback callback;

	/**
	 * DownloadImageTaskのコールバックインターフェイス
	 *
	 *
	 */
	public interface ImportTaskCallback {
		/**
		 * 画像のダウンロードが成功した時に呼ばれるメソッド
		 *
		 * @param recCount
		 *            インポートしたデータの件数
		 */
		void onSuccessImport(Integer recCount);

		/**
		 * 画像のダウンロードが失敗した時に呼ばれるメソッド
		 *
		 * @param resId
		 *            エラーメッセージのリソースID
		 */
		void onFailedImport(int resId);
	}

	/**
	 * コンストラクタです。
	 * <p>
	 * UIスレッド処理です。
	 * </p>
	 *
	 * @param activity
	 *            Activity
	 */
	public FileImportTask(Activity activity) {
		this.activity = activity;

		try {
			this.callback = (ImportTaskCallback) activity;
		} catch (ClassCastException e) {
			this.callback = null;
		}
	}

	@Override
	protected void onPreExecute() {
		// バックグラウンドの処理前にUIスレッドでダイアログ表示
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage("XMLファイルをインポートをしています・・・");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected Integer doInBackground(Object... params) {
		// ファイルのインポートをバックグラウンドで実行
		int recCount;
		FileImport fileImp = new FileImport(activity);
		recCount = fileImp.xmlFileImport();
		return recCount;
	}

	@Override
	protected void onPostExecute(Integer recCount) {
		// 処理中ダイアログをクローズ
		progressDialog.dismiss();

		// 終了ダイアログを表示します。
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		// タイトルを設定する
		alertDialog.setTitle("インポート");
		if (recCount < 0) {
			String message = null;
			switch (recCount) {
			case FileImport.DBACCESS_ERROR:
				message = "インポートデータのDB登録でエラーが発生しました。";
				break;
			case FileExport.SD_NOT_READY:
				message = "SDカードが挿入されていません。";
				break;
			case FileImport.FILE_NOTFOUND:
				message = "インポートするファイルが存在しません。SDカードの　/data/ に assets_info.xml を作成してください。";
				break;
			case FileImport.XML_FILE_FORMAT_ERROR:
				message = "インポートするXMLファイルの形式が不正です。";
				break;
			default:
				// メッセージ内容を設定する --Error--
				message = "ファイルのインポートが異常終了しました。";
				break;
			}
			// メッセージ内容を設定する --Error--
			alertDialog.setMessage(message);

			if( this.callback != null){
				// エラーをコールバックで返す
				callback.onFailedImport(recCount);
			}

		} else {
			// メッセージ内容を設定する
			alertDialog.setMessage("ファイルのインポートが完了しました。　登録件数："
					+ Integer.toString(recCount));
			if( this.callback != null){
				// インポートしたデータ数をコールバックでを返す
				callback.onSuccessImport(recCount);
			}
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
}
