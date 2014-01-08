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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * @author 0a6055
 *
 */
public class FileImport {

	private static final String TAG = FileImport.class.getSimpleName();
	@SuppressWarnings("unused")
	private final FileImport self = this;

	//ASSETS_INFO DBアクセスクラス
	AssetInfoDAO dao;

	// 読み込みファイル名
	public static final String FILE_NAME = "assets_info.xml";

	public static final int FILE_NOTFOUND = -9;
	public static final int XML_FILE_FORMAT_ERROR = -8;
	public static final int DBACCESS_ERROR = -7;
	public static final int SD_NOT_READY = -6;
	public static final int IMPORT_ERROR = -1;

	private Context mContext;

	private List<AssetInfo> mListAssetInfo;

	/**
	 * @param context
	 */
	public FileImport(Context context) {
		mContext = context;
		dao = new AssetInfoDAO(context);
	}

	/**
	 * XMLファイルを読み込み、DBに登録する
	 * @return saveRecCount 登録データ件数　/ エラーの場合 < 0
	 */
	public int xmlFileImport() {

		CheckStorageState chkSDState = new CheckStorageState(mContext);
		if( chkSDState.checkState() !=  CheckStorageState.AVAILABLE){
			return SD_NOT_READY;
		}

		int recCount = 0;
		int result = 0;
		result = xmlFileLoad();
		if (mListAssetInfo == null) {
			return result;
		}
		recCount = mListAssetInfo.size();
		Log.d(TAG,"Load Rec Couont --" + Integer.toString(recCount));

		int saveRecCount;
		// 読み込んだデータをDBに登録する
		saveRecCount = dao.saveList(mListAssetInfo);
		if (saveRecCount < 0) {
			// ERROR
			Log.d(TAG, "登録エラー");
			saveRecCount= DBACCESS_ERROR;
		}
		Log.d(TAG,"Save Rec Couont --" + Integer.toString(saveRecCount));
		return saveRecCount;
	}

	/**
	 * ファイルインポート 　FILE_NAMEで指定した、XMLファイルを読み込む
	 * @return 読み込み結果
	 * 			0：成功
	 * 			FILE_NOTFOUND：ファイルなし
	 * 			XML_FILE_FORMAT_ERROR：ファイル形式不正
	 * 			IMPORT_ERROR：その他 ファイル読み込みエラー
	 */
	public int xmlFileLoad() {

		// SDカードパスを取得する。
        String SDFile = Environment.getExternalStorageDirectory() + "/data/" + FILE_NAME;
		File file = new File(SDFile);

		List<AssetInfo> lisetAssetInfo = new ArrayList<AssetInfo>();

		int result = 0;

		FileInputStream fis;
		try {
			fis = new FileInputStream(file);

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(fis, "UTF-8");

			// タグ名
			String tag = "";
			// 値
			String value = "";
			// 1レコード分のデータを保管
			AssetInfo assetInfo = null;
			// XMLの解析
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {

				switch (type) {
				case XmlPullParser.START_TAG: // 開始タグ
					tag = parser.getName();
					if (parser.getName().equals("Android_Export")) {
						assetInfo = new AssetInfo();
					}
					break;
				case XmlPullParser.TEXT: // タグの内容
					value = parser.getText();
					// 空白で取得したものは全て処理対象外とする
					if (value.trim().length() != 0) {
						// 取得した結果をassetInfoに設定
						if (tag.equals("ID")) {
							assetInfo.setId(Long.parseLong(value));
						} else if (tag.equals(AssetInfo.COLUMN_ASSET_NUMBER)) {
							assetInfo.setAssetNumber(value);
						} else if (tag.equals(AssetInfo.COLUMN_ASSET_TYPE)) {
							assetInfo.setAssetType(value);
						} else if (tag.equals(AssetInfo.COLUMN_CHECK_DATE)) {
							assetInfo.setCheckDate(value);
						} else if (tag.equals(AssetInfo.COLUMN_CHECK_MEMO)) {
							assetInfo.setCheckMemo(value);
						} else if (tag.equals(AssetInfo.COLUMN_CHECK_RESULT)) {
							assetInfo.setCheckResult(value);
						} else if (tag
								.equals(AssetInfo.COLUMN_INSTALLATION_LOCATION)) {
							assetInfo.setInstallationLocation(value);
						} else if (tag.equals(AssetInfo.COLUMN_LEASE_DEADLINE)) {
							assetInfo.setLeaseDeadline(value);
						} else if (tag
								.equals(AssetInfo.COLUMN_MANAGEMENT_GROUP)) {
							assetInfo.setManagementGroup(value);
						} else if (tag
								.equals(AssetInfo.COLUMN_MANAGEMENT_MEMBER_ID)) {
							assetInfo.setManagementMemberId(value);
						} else if (tag
								.equals(AssetInfo.COLUMN_MANAGEMENT_MEMBER_NAME)) {
							assetInfo.setManagementMemberName(value);
						} else if (tag.equals(AssetInfo.COLUMN_OBTAIN_TYPE)) {
							assetInfo.setObtainType(value);
						} else if (tag.equals(AssetInfo.COLUMN_RETURN_DATE)) {
							assetInfo.setReturnDate(value);
						} else if (tag.equals(AssetInfo.COLUMN_STATE)) {
							assetInfo.setState(value);
						} else if (tag.equals(AssetInfo.COLUMN_USE_MEMBER_ID)) {
							assetInfo.setUseMemberId(value);
						} else if (tag.equals(AssetInfo.COLUMN_USE_MEMBER_NAME)) {
							assetInfo.setUseMemberName(value);
						} else if (tag.equals(AssetInfo.COLUMN_LATITUDE)) {
							assetInfo.setLatitude(Double.parseDouble(value));
						} else if (tag.equals(AssetInfo.COLUMN_LONGITUDE)) {
							assetInfo.setLongitude(Double.parseDouble(value));
						}

					}
					break;
				case XmlPullParser.END_TAG: // 終了タグ
					if (parser.getName().equals("Android_Export")
							&& assetInfo != null) {
						lisetAssetInfo.add(assetInfo);
						assetInfo = null;
					}
					break;
				}
			}

			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			lisetAssetInfo = null;
			result = FILE_NOTFOUND;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			lisetAssetInfo = null;
			result = XML_FILE_FORMAT_ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			lisetAssetInfo = null;
			result = IMPORT_ERROR;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			lisetAssetInfo = null;
			result = IMPORT_ERROR;
		} finally {

		}

		mListAssetInfo = lisetAssetInfo;

		return result;
	}



}
