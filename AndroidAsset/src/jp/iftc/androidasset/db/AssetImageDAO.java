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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

/**
 * @author 0a6055 資産情報用 データアクセスクラス
 */
public class AssetImageDAO {

	private static final String TAG = AssetImageDAO.class.getSimpleName();
	@SuppressWarnings("unused")
	private final AssetImageDAO self = this;

	private AssetsDbOpenHelper helper = null;
	private Context mContext;

	/*
	 *
	 */
	public AssetImageDAO(Context context) {
		helper = new AssetsDbOpenHelper(context);
		mContext = context;
	}

	/*
	 * 画像データの保存
	 *
	 * @param AssetImage 保存対象のデータ
	 *
	 * @return 保存したデータ　Error：null
	 */
	public AssetImage save(AssetImage assetImage) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetImage result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(AssetImage.COLUMN_ID, assetImage.getId());
			values.put(AssetImage.COLUMN_ASSET_ID, assetImage.getAssetId());
			values.put(AssetImage.COLUMN_TYPE, assetImage.getType());
			values.put(AssetImage.COLUMN_IMAGE, assetImage.getAssetImage());
			values.put(AssetImage.COLUMN_THUM, assetImage.getAssetThum());

			Long rowId = assetImage.getId();

			int updateCount = 0;

			// IDがnullの場合はinsert
			if (rowId == null) {
				rowId = db.insert(AssetImage.TABLE_NAME, null, values);
				if (rowId < 0) {
					// エラー処理
					Log.w(TAG, "save Insert Error");
					throw new SQLException();
				}
				Log.v(TAG, "save Insert Success!");
			} else {
				updateCount = db.update(AssetImage.TABLE_NAME, values,
						AssetImage.COLUMN_ID + "=?",
						new String[] { String.valueOf(rowId) });
				if (updateCount != 1) {
					// エラー処理
					Log.w(TAG,
							"save UPDATE Error : Update ID = "
									+ String.valueOf(rowId)
									+ "| Update Count : "
									+ String.valueOf(updateCount));
					throw new SQLException();
				}
				Log.v(TAG, "save update Success!");
			}
			result = loadAssetImage(rowId);
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			result = null;
		} finally {
			db.close();
		}

		Toast.makeText(mContext, "管理ID:[" + result.getAssetId() + "]" + " ID [" + String.valueOf(result.getId()) + "]" + '\n' + " イメージデータ登録完了！", Toast.LENGTH_LONG).show();

		return result;

	}

	/**
	 * 資産IDで画像情報を読み込む
	 *
	 * @param rowId
	 * @return
	 */
	public AssetImage loadAssetImage(Long assetId) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetImage assetImage = null;
		try {
			Cursor cursor = db.query(AssetImage.TABLE_NAME, null,
					AssetImage.COLUMN_ID + "=?",
					new String[] { String.valueOf(assetId) }, null, null, null);
			if( cursor.moveToFirst() ){
				assetImage = getAssetImage(cursor);
			}
		} finally {
			db.close();
		}

		return assetImage;
	}

	/**
	 * 資産IDで画像情報を読み込む
	 *
	 * @param rowId
	 * @return
	 */
	public List<AssetImage> loadAllAssetImage() {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		List<AssetImage> assetImageList = new ArrayList<AssetImage>();
		try {
			Cursor cursor = db.query(AssetImage.TABLE_NAME, null, null, null, null, null, AssetImage.COLUMN_ASSET_ID);
			if( cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					assetImageList.add(getAssetImage(cursor));
					cursor.moveToNext();
				}
			}
		} finally {
			db.close();
		}

		return assetImageList;
	}

	/**
	 * カーソルからオブジェクトへの変換
	 *
	 * @param cursor
	 * @return AssetImage
	 * 			カーソルから読み込んだデータをAssetImageクラスに編成
	 */
	private AssetImage getAssetImage(Cursor cursor) {
		AssetImage assetImage = new AssetImage();

		assetImage.setId(cursor.getLong(cursor
				.getColumnIndex(AssetImage.COLUMN_ID)));
		assetImage.setAssetId(cursor.getLong(cursor
				.getColumnIndex(AssetImage.COLUMN_ASSET_ID)));
		assetImage.setType(cursor.getInt(cursor
				.getColumnIndex(AssetImage.COLUMN_TYPE)));
		assetImage.setAssetImage(cursor.getBlob(cursor
				.getColumnIndex(AssetImage.COLUMN_IMAGE)));
		assetImage.setAssetThum(cursor.getBlob(cursor
				.getColumnIndex(AssetImage.COLUMN_THUM)));

		return assetImage;
	}



	/**
	 * 1レコードの削除
	 *
	 * @param AssetImage
	 *            削除対象のオブジェクト
	 */
	public void delete(AssetImage assetImage) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return;
		}

		int deleteCount = 0;
		try {
			deleteCount = db.delete(AssetImage.TABLE_NAME, AssetImage.COLUMN_ID
					+ "=?", new String[] { String.valueOf(assetImage.getId()) });
			if (deleteCount != 1) {
				// エラー処理
				Log.w(TAG,
						"delete Delete Error : Update ID = "
								+ String.valueOf(assetImage.getId())
								+ "| Delete Count : "
								+ String.valueOf(deleteCount));
				throw new SQLException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} finally {
			db.close();
		}
	}



	/**
	 * 資産IDで画像情報を読み込む
	 *
	 * @param assetId
	 * @return
	 */
	public List<AssetImage> loadAssetImageForID(Long assetId) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		List<AssetImage> assetImage = null;
		try {
			Cursor cursor = db.query(AssetImage.TABLE_NAME, null,
					AssetImage.COLUMN_ASSET_ID + "=?",
					new String[] { String.valueOf(assetId) }, null, null, null);

			assetImage = new ArrayList<AssetImage>();
			if( cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					assetImage.add(getAssetImage(cursor));
					cursor.moveToNext();
				}
			}

		} finally {
			db.close();
		}

		return assetImage;
	}

	/**
	 * 資産IDで画像情報を読み込む
	 *
	 * @param assetId
	 * @return
	 */
	public List<AssetImage> loadAssetImageForID(Long assetId, int type) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		List<AssetImage> assetImage = null;
		try {
			Cursor cursor = db.query(AssetImage.TABLE_NAME, null,
					AssetImage.COLUMN_ASSET_ID + "=? and " + AssetImage.COLUMN_TYPE + "=?",
					new String[] { String.valueOf(assetId), String.valueOf(type) }, null, null, null);

			assetImage = new ArrayList<AssetImage>();
			if( cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					assetImage.add(getAssetImage(cursor));
					cursor.moveToNext();
				}
			}

		} finally {
			db.close();
		}

		return assetImage;
	}

	/**
	 * 一覧を取得する
	 *
	 * @return 検索結果
	 */
	public List<AssetImage> getAllImagelist() {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		List<AssetImage> assetImageList;
		try {
			Cursor cursor = db.query(AssetImage.TABLE_NAME, null, null, null,
					null, null, AssetImage.COLUMN_ID);
			assetImageList = new ArrayList<AssetImage>();
			if( cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					assetImageList.add(getAssetImage(cursor));
					cursor.moveToNext();
				}
			}
		} finally {
			db.close();
		}
		return assetImageList;
	}
}
