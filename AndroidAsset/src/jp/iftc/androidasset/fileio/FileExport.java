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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * @author 0a6055
 *
 */
public class FileExport {
	private static final String TAG = FileExport.class.getSimpleName();
	@SuppressWarnings("unused")
	private final FileExport self = this;

	// ASSETS_INFO DBアクセスクラス
	AssetInfoDAO dao;

	// 読み込みファイル名
	public static final String FILE_NAME = "assets_info_export.xml";

	public static final int FILE_NOTFOUND = -9;
	public static final int DBACCESS_ERROR = -7;
	public static final int SD_NOT_READY = -6;
	public static final int EXPORT_ERROR = -1;

	// データタグ
	private static String DATA_ROOT = "dataroot";
	private static String TABLE_NAME = "ANDROID_DATA";

	private boolean cancelFlg = false;
	private Context mContext;

	/**
	 * コンストラクタ
	 *
	 * @param context
	 */
	public FileExport(Context context) {
		mContext = context;
		dao = new AssetInfoDAO(context);
	}

	/**
	 * @return
	 */
	public int xmlFileExport() {

		CheckStorageState chkSDState = new CheckStorageState(mContext);
		if( chkSDState.checkState() != CheckStorageState.AVAILABLE ){
			return SD_NOT_READY;
		}

		// 出力するデータをDBから取得する
		List<AssetInfo> listAssetInfo;
		listAssetInfo = getAssetInfo();

		if (getAssetInfo().size() < 1) {
			return DBACCESS_ERROR;
		}

		// SDカードパスを取得する。
		File path = Environment.getExternalStorageDirectory();

		String sdFile = null;

/* ※より安全にSDカードを使用するなら、以下でアプリケーションの利用可能なパスを取得するべき。
//	    File path = mContext.getExternalFilesDir(null);
//	    if (path == null) {
//	    	return SD_NOT_READY;
//	    }
*/

		// SD /data/ ディレクトリ生成
		File outDir = new File(path, "data");
		// /data/ のディレクトリが SD カードになければ作成します。
		if (outDir.exists() == false) {
		    outDir.mkdir();
		}

		sdFile = path + "/data/" + FILE_NAME;


		XmlSerializer serializer = Xml.newSerializer();
		int recCount = 0;
		try {
			FileOutputStream fos = new FileOutputStream(sdFile);
			serializer.setOutput(fos, "UTF-8");

			serializer.startDocument("UTF-8", true); // ヘッダー

			serializer.startTag("", DATA_ROOT);
			for (recCount = 0; recCount < listAssetInfo.size(); recCount++) {

				// キャンセルの判定
				if (cancelFlg) {
					fos.close();
					return 0;
				}

				AssetInfo assetInfo = listAssetInfo.get(recCount);
				serializer.startTag("", TABLE_NAME);

				serializer.startTag("", "ID");
				serializer.text(Long.toString(assetInfo.getId()));
				serializer.endTag("", "ID");

				serializer.startTag("", AssetInfo.COLUMN_CHECK_RESULT);
				serializer.text(assetInfo.getCheckResult());
				serializer.endTag("", AssetInfo.COLUMN_CHECK_RESULT);

				serializer.startTag("", AssetInfo.COLUMN_CHECK_DATE);
				serializer.text(assetInfo.getCheckDate());
				serializer.endTag("", AssetInfo.COLUMN_CHECK_DATE);

				serializer.startTag("", AssetInfo.COLUMN_CHECK_MEMO);
				serializer.text(assetInfo.getCheckMemo());
				serializer.endTag("", AssetInfo.COLUMN_CHECK_MEMO);

				serializer.endTag("", TABLE_NAME);
			}
			serializer.endTag("", DATA_ROOT);

			serializer.endDocument(); // フッター

			serializer.flush(); // 出力
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			recCount = FILE_NOTFOUND;

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
			recCount = EXPORT_ERROR;
		}

		return recCount;

	}

	/**
	 * エクスポートするデータをDBから取得する
	 *
	 * @return
	 */
	private List<AssetInfo> getAssetInfo() {

		return dao.list();

	}

	/**
	 * @return cancelFlg
	 */
	public boolean getCancelFlg() {
		return cancelFlg;
	}

	/**
	 * @param cancelFlg
	 *            セットする cancelFlg
	 */
	public void setCancelFlg(Boolean cancelFlg) {
		this.cancelFlg = cancelFlg;
	}

}
