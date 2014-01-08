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

package jp.iftc.androidasset.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author 0a6055 資産管理DBオープン用 ヘルパークラス
 */
public class AssetsDbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = AssetsDbOpenHelper.class.getSimpleName();
	@SuppressWarnings("unused")
	private final AssetsDbOpenHelper self = this;

	// データベース名の定数
	private static final String DB_NAME = "ASSETS_INFO";

	// バージョン
	private static final int VERSION = 3;

	/**
	 * @param context
	 */
	public AssetsDbOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase) データベースを新規に作成した後に呼ばれる。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 内部にテーブルを作成する。
		db.beginTransaction();

		try {
			// テーブルの生成
			StringBuilder createSql = new StringBuilder();
			createSql.append("create table " + AssetInfo.TABLE_NAME + " (");
			createSql.append(AssetInfo.COLUMN_ID + " integer primary key autoincrement not null,");
			createSql.append(AssetInfo.COLUMN_ASSET_NUMBER + " text,");
			createSql.append(AssetInfo.COLUMN_ASSET_TYPE + " text,");
			createSql.append(AssetInfo.COLUMN_OBTAIN_TYPE + " text,");
			createSql.append(AssetInfo.COLUMN_LEASE_DEADLINE + " text,");
			createSql.append(AssetInfo.COLUMN_STATE + " text,");
			createSql.append(AssetInfo.COLUMN_MANAGEMENT_GROUP + " text,");
			createSql.append(AssetInfo.COLUMN_INSTALLATION_LOCATION + " text,");
			createSql.append(AssetInfo.COLUMN_MANAGEMENT_MEMBER_ID + " text,");
			createSql.append(AssetInfo.COLUMN_MANAGEMENT_MEMBER_NAME + " text,");
			createSql.append(AssetInfo.COLUMN_USE_MEMBER_ID + " text,");
			createSql.append(AssetInfo.COLUMN_USE_MEMBER_NAME + " text,");
			createSql.append(AssetInfo.COLUMN_RETURN_DATE + " text,");
			createSql.append(AssetInfo.COLUMN_CHECK_RESULT + " text,");
			createSql.append(AssetInfo.COLUMN_CHECK_MEMO + " text,");
			createSql.append(AssetInfo.COLUMN_CHECK_DATE + " text,");
			createSql.append(AssetInfo.COLUMN_LATITUDE + " real,");
			createSql.append(AssetInfo.COLUMN_LONGITUDE + " real");
			createSql.append(")");

			db.execSQL(createSql.toString());

			db.execSQL(createImegeTable().toString());

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			db.endTransaction();
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			// アップグレードコード
			String createSql = null;
//			db.execSQL("ALTER TABLE " + AssetInfo.TABLE_NAME + " add "
//					+ AssetInfo.COLUMN_LATITUDE + " TEXT, "
//					+ AssetInfo.COLUMN_LONGITUDE + " REAL");

			createSql = "ALTER TABLE " + AssetInfo.TABLE_NAME + " add "
					+ AssetInfo.COLUMN_LATITUDE + " TEXT, "
					+ AssetInfo.COLUMN_LONGITUDE + " REAL";

			Log.v(TAG, "SQL:" + createSql);
			db.execSQL(createSql);
		case 2:
			db.execSQL(createImegeTable().toString());
		default:
			break;
		}

	}

	private StringBuilder createImegeTable() {
		// イメージ格納用テーブルの生成
		StringBuilder createSql = new StringBuilder();
		createSql.append("create table " + AssetImage.TABLE_NAME + " (");
		createSql.append(AssetImage.COLUMN_ID
				+ " integer primary key autoincrement not null,");
		createSql.append(AssetImage.COLUMN_ASSET_ID + " integer,");
		createSql.append(AssetImage.COLUMN_TYPE + " integer,");
		createSql.append(AssetImage.COLUMN_IMAGE + " blob,");
		createSql.append(AssetImage.COLUMN_THUM + " blob ");
		createSql.append(")");

		Log.v(TAG, "SQL:" + createSql);
		return createSql;

	}

}
