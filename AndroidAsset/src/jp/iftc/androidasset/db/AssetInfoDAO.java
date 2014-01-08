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

/**
 * @author 0a6055 資産情報用 データアクセスクラス
 */
public class AssetInfoDAO {

	private static final String TAG = AssetInfoDAO.class.getSimpleName();
	@SuppressWarnings("unused")
	private final AssetInfoDAO self = this;

	private AssetsDbOpenHelper helper = null;
	private Context mContext;

	/*
	 *
	 */
	public AssetInfoDAO(Context context) {
		helper = new AssetsDbOpenHelper(context);
		mContext = context;
	}

	/*
	 * 資産情報の保存＜IDがNULLならInsert,それ以外ならUpdateで全項目更新＞
	 *
	 * @param AssetInfo 保存対象のデータ
	 *
	 * @return 保存したデータ　Error：null
	 */
	public AssetInfo save(AssetInfo assetInfo) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetInfo result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(AssetInfo.COLUMN_ID, assetInfo.getId());
			values.put(AssetInfo.COLUMN_ASSET_NUMBER,
					assetInfo.getAssetNumber());
			values.put(AssetInfo.COLUMN_ASSET_TYPE, assetInfo.getAssetType());
			values.put(AssetInfo.COLUMN_OBTAIN_TYPE, assetInfo.getObtainType());
			values.put(AssetInfo.COLUMN_LEASE_DEADLINE,
					assetInfo.getLeaseDeadline());
			values.put(AssetInfo.COLUMN_STATE, assetInfo.getState());
			values.put(AssetInfo.COLUMN_MANAGEMENT_GROUP,
					assetInfo.getManagementGroup());
			values.put(AssetInfo.COLUMN_INSTALLATION_LOCATION,
					assetInfo.getInstallationLocation());
			values.put(AssetInfo.COLUMN_MANAGEMENT_MEMBER_ID,
					assetInfo.getManagementMemberId());
			values.put(AssetInfo.COLUMN_MANAGEMENT_MEMBER_NAME,
					assetInfo.getManagementMemberName());
			values.put(AssetInfo.COLUMN_USE_MEMBER_ID,
					assetInfo.getUseMemberId());
			values.put(AssetInfo.COLUMN_USE_MEMBER_NAME,
					assetInfo.getUseMemberName());
			values.put(AssetInfo.COLUMN_RETURN_DATE, assetInfo.getReturnDate());
			values.put(AssetInfo.COLUMN_CHECK_RESULT,
					assetInfo.getCheckResult());
			values.put(AssetInfo.COLUMN_CHECK_MEMO, assetInfo.getCheckMemo());
			values.put(AssetInfo.COLUMN_CHECK_DATE, assetInfo.getCheckDate());
			if(assetInfo.getLatitude() != 0 && assetInfo.getLongitude() != 0){
				values.put(AssetInfo.COLUMN_LATITUDE, assetInfo.getLatitude());
				values.put(AssetInfo.COLUMN_LONGITUDE, assetInfo.getLongitude());
			}
			Long rowId = assetInfo.getId();

			int updateCount = 0;

			// IDがnullの場合はinsert
			if (rowId == null) {
				rowId = db.insert(AssetInfo.TABLE_NAME, null, values);
				if (rowId < 0) {
					// エラー処理
					Log.w(TAG, "save Insert Error");
					throw new SQLException();
				}
				Log.v(TAG, "save Insert Success!");
			} else {
				updateCount = db.update(AssetInfo.TABLE_NAME, values,
						AssetInfo.COLUMN_ID + "=?",
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
			result = load(rowId);
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			result = null;
		} finally {
			db.close();
		}
		return result;

	}

	/**
	 * 資産情報確認結果の保存＜IDがNULLならInsert,それ以外ならUpdateで確認結果のみ登録＞
	 *
	 * @param AssetInfo 保存対象のデータ
	 *
	 * @return 保存結果 Error: null
	 */
	public AssetInfo saveCheckResult(AssetInfo assetInfo, int insertFlg) {

//		//確認結果OK登録の場合現在位置をセット
//		if(assetInfo.getCheckResultInt() == AssetInfo.CHECK_OK){
//			setLocateion(assetInfo);
//		}

		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetInfo result = null;
		try {
			ContentValues values = new ContentValues();

			//Insertのときは、IDもセットする。
			//TODO Insertで、IDが入っているときだけ、IDもセットする。
			if( insertFlg == 1 && assetInfo.getId() != null ){
				values.put(AssetInfo.COLUMN_ID, assetInfo.getId());
			}

//			values.put(AssetInfo.COLUMN_ASSET_NUMBER,assetInfo.getAssetNumber());

			values.put(AssetInfo.COLUMN_CHECK_RESULT,
					assetInfo.getCheckResult());
			values.put(AssetInfo.COLUMN_CHECK_MEMO, assetInfo.getCheckMemo());
			values.put(AssetInfo.COLUMN_CHECK_DATE, getNow()); //現在日時をセット

			if(assetInfo.getLatitude() != 0 && assetInfo.getLongitude() != 0){
				values.put(AssetInfo.COLUMN_LATITUDE, assetInfo.getLatitude());
				values.put(AssetInfo.COLUMN_LONGITUDE, assetInfo.getLongitude());
			}

			Long rowId = assetInfo.getId();
			int updateCount = 0;
			// IDがnullの場合はinsert
			if (rowId == null || insertFlg == 1) {
				rowId = db.insert(AssetInfo.TABLE_NAME, null, values);
				if (rowId < 0) {
					// エラー処理
					Log.e(TAG, "saveCheckResult Insert Error");
					throw new SQLException();
				}
			} else {
				updateCount = db.update(AssetInfo.TABLE_NAME, values,
						AssetInfo.COLUMN_ID + "=?",
						new String[] { String.valueOf(rowId) });
				if (updateCount != 1) {
					// エラー処理
					Log.e(TAG, "saveCheckResult UPDATE Error : Update ID = "
							+ String.valueOf(rowId) + "| Update Count : "
							+ String.valueOf(updateCount));
					throw new SQLException();
				}
			}
			result = load(rowId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} finally {
			db.close();
		}
		return result;

	}

	/**
	 * 1レコードの削除
	 *
	 * @param AssetInfo
	 *            削除対象のオブジェクト
	 */
	public void delete(AssetInfo assetInfo) {
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
			deleteCount = db.delete(AssetInfo.TABLE_NAME, AssetInfo.COLUMN_ID
					+ "=?", new String[] { String.valueOf(assetInfo.getId()) });
			if (deleteCount != 1) {
				// エラー処理
				Log.w(TAG,
						"delete Delete Error : Update ID = "
								+ String.valueOf(assetInfo.getId())
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
	 * 資産情報全レコードの削除
	 *
	 * @param db
	 */
	private void deleteAssetsAll(SQLiteDatabase db) {

//		try {
//			db = helper.getWritableDatabase();
//
//		} catch (SQLiteException e) {
//			// TODO: handle exception
//			Log.w(TAG, e.toString());
//			return;
//		}

		int deleteCount = 0;
		try {
			deleteCount = db.delete(AssetInfo.TABLE_NAME, null, null);
			if (deleteCount < 1) {
				// エラー処理
				Log.w(TAG,
						"deleteAll Delete Error Delete Count : "
								+ String.valueOf(deleteCount));
			}
		} finally {
//			db.close();
		}
	}

	/**
	 * IDで資産情報を読み込む
	 *
	 * @param rowId
	 * @return
	 */
	public AssetInfo loadAssetInfo(Long rowId) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetInfo assetInfo = null;
		try {
			Cursor cursor = db.query(AssetInfo.TABLE_NAME, null,
					AssetInfo.COLUMN_ID + "=?",
					new String[] { String.valueOf(rowId) }, null, null, null);

			if(cursor.moveToFirst()){
				assetInfo = getAssetInfo(cursor);
			} else {
				assetInfo = null;		//データがない場合NULL
			}

		} finally {
			db.close();
		}

		return assetInfo;
	}


	/**
	 * IDで資産情報を読み込む
	 *
	 * @param rowId
	 * @return
	 */
	private AssetInfo load(Long rowId) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		AssetInfo assetInfo = null;
		try {
			Cursor cursor = db.query(AssetInfo.TABLE_NAME, null,
					AssetInfo.COLUMN_ID + "=?",
					new String[] { String.valueOf(rowId) }, null, null, null);
			cursor.moveToFirst();
			assetInfo = getAssetInfo(cursor);
		} finally {
			db.close();
		}

		return assetInfo;
	}

	/**
	 * 一覧を取得する
	 *
	 * @return 検索結果
	 */
	public List<AssetInfo> list() {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		List<AssetInfo> assetInfoList;
		try {
			Cursor cursor = db.query(AssetInfo.TABLE_NAME, null, null, null,
					null, null, AssetInfo.COLUMN_ID);
			assetInfoList = new ArrayList<AssetInfo>();
			if( cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					assetInfoList.add(getAssetInfo(cursor));
					cursor.moveToNext();
				}
			}
		} finally {
			db.close();
		}
		return assetInfoList;
	}

    /**
     * 指定カラムの一覧を取得する
     * @param column カラム名:String型
     * @return assetNarrowList 検索結果:ArrayList<String>型
     */
    public ArrayList<String> getNarrowColumnList( String column ) {
        SQLiteDatabase db;
        String[] columns = { column };
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }
        ArrayList<String> assetNarrowList = new ArrayList<String>();
        try {
            Cursor cursor = db.query(AssetInfo.TABLE_NAME, columns, null, null, column, null, null );
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (cursor.getString( cursor.getColumnIndex( column ) ) != null ){
                        assetNarrowList.add( cursor.getString( cursor.getColumnIndex( column ) ) );
                    } else {
                        assetNarrowList.add( "null" );
                    }
                    cursor.moveToNext();
                }

            }
        } finally {
            db.close();
        }
        return assetNarrowList;
    }

    /**
     * 絞込み実行後の一覧を取得する
     * @param columns カラム名:String型
     * @param narrows 条件:String型
     * @return assetInfoList 検索結果:List<String>型
     */
    public List<AssetInfo> getNarrowList( String[] columns , String[] narrows ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        String sqlstr = "select *" +
                        " from " + AssetInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

	                buf += columns[i] + " = \"" + narrows[i] + "\"";

	            }
	        }
        }

        if ( whereFlag == true ){
            sqlstr += " where ";
        }

        sqlstr += buf +" order by " + AssetInfo.COLUMN_ID;

        List<AssetInfo> assetInfoList;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
            assetInfoList = new ArrayList<AssetInfo>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    assetInfoList.add(getAssetInfo(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            db.close();
        }
        return assetInfoList;
    }

    /**
     * 絞込み実行後の一覧を取得する
     * @param columns カラム名:String型
     * @param narrows 条件:String型
     * @return assetInfoList 検索結果:List<String>型
     */
    public int getNarrowListCount( String[] columns , String[] narrows ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return 0;
        }

        String sqlstr = "select count(*)" +
                        " from " + AssetInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

	                if (narrows[i].equals("null") ){
                        buf += columns[i] + " IS NULL";
	                } else {
	                    buf += columns[i] + " = \"" + narrows[i] + "\"";
	                }
	            }
	        }
        }

        if ( whereFlag == true ){
            sqlstr += " where ";
        }

        sqlstr += buf;

        int listCount = 0;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
//            assetInfoList = new ArrayList<AssetInfo>();
            if( cursor.moveToFirst()) {
            	listCount = cursor.getInt(0);
//                while (!cursor.isAfterLast()) {
//                    assetInfoList.add(getAssetInfo(cursor));
//                    cursor.moveToNext();
//                }
            }
        } finally {
            db.close();
        }
        return listCount;
    }
    /**
     * 指定ページのデータリストを取得する
     * @param columns：カラム名:String型
     * @param narrows：条件:String型
     * @param page：ページ番号(0 ～ )
     * @param row：1ページのデータ数
     * @return 1ページ分のデータリスト
     */
    public List<AssetInfo> getPageDataList( String[] columns , String[] narrows , int page, int row ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";
        int offset = 0;

        if(page >= 0){
        	offset = page * row ;
        }

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        String sqlstr = "select *" +
                        " from " + AssetInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

                    if (narrows[i].equals("null") ){
                        buf += columns[i] + " IS NULL";
                    } else {
                        buf += columns[i] + " = \"" + narrows[i] + "\"";
                    }
	            }
	        }

	        if ( whereFlag == true ){
	            sqlstr += " where ";
	        }
        }

        sqlstr += buf +" order by " + AssetInfo.COLUMN_ID;
        sqlstr += " limit " + Integer.toString(row) + " offset " + Integer.toString(offset);

        Log.v(TAG, "SQL:" + sqlstr);

        List<AssetInfo> assetInfoList;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
            assetInfoList = new ArrayList<AssetInfo>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    assetInfoList.add(getAssetInfo(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            db.close();
        }
        return assetInfoList;
    }
	/**
	 * カーソルからオブジェクトへの変換
	 *
	 * @param cursor
	 * @return AssetInfo
	 * 			カーソルから読み込んだデータをAssetInfoクラスに編成
	 */
	private AssetInfo getAssetInfo(Cursor cursor) {
		AssetInfo assetInfo = new AssetInfo();

		assetInfo.setId(cursor.getLong(cursor
				.getColumnIndex(AssetInfo.COLUMN_ID)));
		assetInfo.setAssetNumber(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_ASSET_NUMBER)));
		assetInfo.setAssetType(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_ASSET_TYPE)));
		assetInfo.setObtainType(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_OBTAIN_TYPE)));
		assetInfo.setLeaseDeadline(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_LEASE_DEADLINE)));
		assetInfo.setState(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_STATE)));
		assetInfo.setManagementGroup(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_MANAGEMENT_GROUP)));
		assetInfo.setInstallationLocation(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_INSTALLATION_LOCATION)));
		assetInfo.setManagementMemberId(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_MANAGEMENT_MEMBER_ID)));
		assetInfo.setManagementMemberName(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_MANAGEMENT_MEMBER_NAME)));
		assetInfo.setUseMemberId(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_USE_MEMBER_ID)));
		assetInfo.setUseMemberName(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_USE_MEMBER_NAME)));
		assetInfo.setReturnDate(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_RETURN_DATE)));
		assetInfo.setCheckResult(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_CHECK_RESULT)));
		assetInfo.setCheckMemo(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_CHECK_MEMO)));
		assetInfo.setCheckDate(cursor.getString(cursor
				.getColumnIndex(AssetInfo.COLUMN_CHECK_DATE)));
		assetInfo.setLatitude(cursor.getDouble(cursor
				.getColumnIndex(AssetInfo.COLUMN_LATITUDE)));
		assetInfo.setLongitude(cursor.getDouble(cursor
				.getColumnIndex(AssetInfo.COLUMN_LONGITUDE)));

		return assetInfo;
	}

	/**
	 * リストのデータをすべてDBに登録する＜全件Insert＞
	 *
	 * @param AssetInfoList
	 *            ： 登録対象のリスト
	 * @return recCount : 登録したデータの件数 エラーの場合-1
	 */
	public int saveList(List<AssetInfo> assetInfoList) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			Log.w(TAG, e.toString());
			return -1;
		}

//		List<AssetInfo> result = new ArrayList<AssetInfo>();
		int recCount;
		db.beginTransaction();
		try {
			// テーブル内のデータを全件削除
			deleteAssetsAll(db);

			ContentValues values = new ContentValues();
			for (recCount = 0; recCount < assetInfoList.size(); recCount++) {

				AssetInfo assetInfo = assetInfoList.get(recCount);

				values.clear();

				values.put(AssetInfo.COLUMN_ID, assetInfo.getId());
				values.put(AssetInfo.COLUMN_ASSET_NUMBER,
						assetInfo.getAssetNumber());
				values.put(AssetInfo.COLUMN_ASSET_TYPE,
						assetInfo.getAssetType());
				values.put(AssetInfo.COLUMN_OBTAIN_TYPE,
						assetInfo.getObtainType());
				values.put(AssetInfo.COLUMN_LEASE_DEADLINE,
						assetInfo.getLeaseDeadline());
				values.put(AssetInfo.COLUMN_STATE, assetInfo.getState());
				values.put(AssetInfo.COLUMN_MANAGEMENT_GROUP,
						assetInfo.getManagementGroup());
				values.put(AssetInfo.COLUMN_INSTALLATION_LOCATION,
						assetInfo.getInstallationLocation());
				values.put(AssetInfo.COLUMN_MANAGEMENT_MEMBER_ID,
						assetInfo.getManagementMemberId());
				values.put(AssetInfo.COLUMN_MANAGEMENT_MEMBER_NAME,
						assetInfo.getManagementMemberName());
				values.put(AssetInfo.COLUMN_USE_MEMBER_ID,
						assetInfo.getUseMemberId());
				values.put(AssetInfo.COLUMN_USE_MEMBER_NAME,
						assetInfo.getUseMemberName());
				values.put(AssetInfo.COLUMN_RETURN_DATE,
						assetInfo.getReturnDate());
				values.put(AssetInfo.COLUMN_CHECK_RESULT,
						assetInfo.getCheckResult());
				values.put(AssetInfo.COLUMN_CHECK_MEMO,
						assetInfo.getCheckMemo());
				values.put(AssetInfo.COLUMN_CHECK_DATE,
						assetInfo.getCheckDate());
				if(assetInfo.getLatitude() != 0 && assetInfo.getLongitude() != 0){
					values.put(AssetInfo.COLUMN_LATITUDE,
							assetInfo.getLatitude());
					values.put(AssetInfo.COLUMN_LONGITUDE,
							assetInfo.getLongitude());
				}

				long rowId = db.insert(AssetInfo.TABLE_NAME, null, values);
				if (rowId < 0) {
					Log.e(TAG,
							"saveList insert Error　ID:" + String.valueOf(rowId));
					// TODO エラー処理 ↓でいいのか？
					throw new SQLException();
				}
			}
			db.setTransactionSuccessful();

			Log.v(TAG, "saveList insert is succeeded.");

		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			recCount = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return recCount;
	}
	/**
	 * 現在日時を取得する
	 * @return
	 */
	private String getNow(){
		final Calendar calendar = Calendar.getInstance();

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final int second = calendar.get(Calendar.SECOND);
		final int ms = calendar.get(Calendar.MILLISECOND);

		Log.v("year/month/day hour:minute:second",
		    year + "/" + (month + 1) + "/" + day + "/" + " " +
		    hour + ":" + minute + ":" + second + "." + ms);
		return year + "/" + (month + 1) + "/" + day + "/" + " " +
			    hour + ":" + minute + ":" + second + "." + ms;
	}

}
