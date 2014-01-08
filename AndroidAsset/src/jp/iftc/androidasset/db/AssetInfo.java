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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.iftc.androidasset.R;

import org.apache.http.impl.cookie.DateParseException;

import android.net.ParseException;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * @author 0a6055
 *
 *         1レコード分のデータを保持するクラス
 */
public class AssetInfo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = AssetInfo.class.getSimpleName();
	private final AssetInfo self = this;

	// TableName
	public static final String TABLE_NAME = "assets_info";

	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ASSET_NUMBER = "asset_number";
	public static final String COLUMN_ASSET_TYPE = "asset_type";
	public static final String COLUMN_OBTAIN_TYPE = "obtain_type";
	public static final String COLUMN_LEASE_DEADLINE = "lease_deadline";
	public static final String COLUMN_STATE = "state";
	public static final String COLUMN_MANAGEMENT_GROUP = "management_group";
	public static final String COLUMN_INSTALLATION_LOCATION = "installation_location";
	public static final String COLUMN_MANAGEMENT_MEMBER_ID = "management_member_id";
	public static final String COLUMN_MANAGEMENT_MEMBER_NAME = "management_member_name";
	public static final String COLUMN_USE_MEMBER_ID = "use_member_id";
	public static final String COLUMN_USE_MEMBER_NAME = "use_member_name";
	public static final String COLUMN_RETURN_DATE = "return_date";
	public static final String COLUMN_CHECK_RESULT = "check_result";
	public static final String COLUMN_CHECK_MEMO = "check_memo";
	public static final String COLUMN_CHECK_DATE = "check_date";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";

	// 確認結果登録状態
	public static final int NORECORD = 9; // データなし
	public static final int CHECK_OK = 1; // OK登録済み
	public static final int CHECK_NG = 2; // 確NG登録済み
	public static final int NO_CHECK = 0; // 未確認

	// データなし時
	public static final String NO_DATA = "-";

	//取得種別　購入
	public static final String OBTAIN_TYPE_BUY = "購入";

	private static final String TIME_DIFF = "+09:00";
	private static final String DATE_PATTERN = "yyyy/MM/dd hh:mm:ss";
	private static final long LIMIT_DAY = -20;

	private Long id = null;
	private String assetNumber = null;
	private String assetType = null;
	private String obtainType = null;
	private String leaseDeadline = null;
	private String state = null;
	private String managementGroup = null;
	private String installationLocation = null;
	private String managementMemberId = null;
	private String managementMemberName = null;
	private String useMemberId = null;
	private String useMemberName = null;
	private String returnDate = null;
	private String checkResult = null;
	private String checkMemo = null;
	private String checkDate = null;
	private double latitude = 0;
	private double longitude = 0;

	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            セットする id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return assetNumber
	 */
	public String getAssetNumber() {
		if (TextUtils.isEmpty(assetNumber)) {
			return NO_DATA;
		}
		return assetNumber;
	}

	/**
	 * @param assetNumber
	 *            セットする assetNumber
	 */
	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}

	/**
	 * @return assetType
	 */
	public String getAssetType() {
		if (TextUtils.isEmpty(assetType)) {
			return NO_DATA;
		}
		return assetType;
	}

	/**
	 * @param assetType
	 *            セットする assetType
	 */
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	/**
	 * @return obtainType
	 */
	public String getObtainType() {
		if (TextUtils.isEmpty(obtainType)) {
			return NO_DATA;
		}
		return obtainType;
	}

	/**
	 * @param obtainType
	 *            セットする obtainType
	 */
	public void setObtainType(String obtainType) {
		this.obtainType = obtainType;
	}

	/**
	 * @return leaseDeadline
	 */
	public String getLeaseDeadline() {
		if (TextUtils.isEmpty(leaseDeadline)) {
			return NO_DATA;
		}
		return leaseDeadline;
	}

	/**
	 * @return returnDate
	 */
	public String getLeaseDeadlineFormat(String format) {
		if (TextUtils.isEmpty(leaseDeadline)) {
			return NO_DATA;
		}
		if (leaseDeadline.contentEquals("-") ){
    		return NO_DATA;
		}
		return getDateFormat(leaseDeadline, format);
	}

	/**
	 * @param leaseDeadline
	 *            セットする leaseDeadline
	 */
	public void setLeaseDeadline(String leaseDeadline) {
		this.leaseDeadline = leaseDeadline;
	}

	/**
	 * @return state
	 */
	public String getState() {
		if (TextUtils.isEmpty(state)) {
			return NO_DATA;
		}
		return state;
	}

	/**
	 * @param state
	 *            セットする state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return managementGroup
	 */
	public String getManagementGroup() {
		if (TextUtils.isEmpty(managementGroup)) {
			return NO_DATA;
		}
		return managementGroup;
	}

	/**
	 * @param managementGroup
	 *            セットする managementGroup
	 */
	public void setManagementGroup(String managementGroup) {
		this.managementGroup = managementGroup;
	}

	/**
	 * @return installationLocation
	 */
	public String getInstallationLocation() {
		if (TextUtils.isEmpty(installationLocation)) {
			return NO_DATA;
		}
		return installationLocation;
	}

	/**
	 * @param installationLocation
	 *            セットする installationLocation
	 */
	public void setInstallationLocation(String installationLocation) {
		this.installationLocation = installationLocation;
	}

	/**
	 * @return managementMemberId
	 */
	public String getManagementMemberId() {
		if (TextUtils.isEmpty(managementMemberId)) {
			return NO_DATA;
		}
		return managementMemberId;
	}

	/**
	 * @param managementMemberId
	 *            セットする managementMemberId
	 */
	public void setManagementMemberId(String managementMemberId) {
		this.managementMemberId = managementMemberId;
	}

	/**
	 * @return managementMemberName
	 */
	public String getManagementMemberName() {
		if (TextUtils.isEmpty(managementMemberName)) {
			return NO_DATA;
		}
		return managementMemberName;
	}

	/**
	 * @param managementMemberName
	 *            セットする managementMemberName
	 */
	public void setManagementMemberName(String managementMemberName) {
		this.managementMemberName = managementMemberName;
	}

	/**
	 * @return useMemberId
	 */
	public String getUseMemberId() {
		if (TextUtils.isEmpty(useMemberId)) {
			return NO_DATA;
		}
		return useMemberId;
	}

	/**
	 * @param useMemberId
	 *            セットする useMemberId
	 */
	public void setUseMemberId(String useMemberId) {
		this.useMemberId = useMemberId;
	}

	/**
	 * @return useMemberName
	 */
	public String getUseMemberName() {
		if (TextUtils.isEmpty(useMemberName)) {
			return NO_DATA;
		}
		return useMemberName;
	}

	/**
	 * @param useMemberName
	 *            セットする useMemberName
	 */
	public void setUseMemberName(String useMemberName) {
		this.useMemberName = useMemberName;
	}

	/**
	 * @return returnDate
	 */
	public String getReturnDate() {
		if (TextUtils.isEmpty(returnDate)) {
			return NO_DATA;
		}
		return returnDate;
	}

	/**
	 * @return returnDate
	 */
	public String getReturnDateFormat(String format) {
		if (TextUtils.isEmpty(returnDate)) {
			return NO_DATA;
		}
		if (returnDate.contentEquals("-") ){
    		return NO_DATA;
		}
		return getDateFormat(returnDate, format);
	}

	/**
	 * @param returnDate
	 *            セットする returnDate
	 */
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
		setAlertType();
	}

	/**
	 * @return checkResult
	 */
	public String getCheckResult() {
		if (TextUtils.isEmpty(checkResult)) {
			return "0";
		}
		return checkResult;
	}

	/**
	 * @return checkResult
	 */
	public Integer getCheckResultInt() {
		if (TextUtils.isEmpty(checkResult)) {
			return 0;
		}
		return Integer.parseInt(checkResult);
	}

	/**
	 * 確認結果の名称を返す
	 *
	 * @return getCheckResultName
	 */
	public String getCheckResultName() {
		// 確認結果の文字列　OK(1),NG(2),未(0,null)
		if (TextUtils.isEmpty(checkResult) || checkResult.equals("0")) {
			return "未";
		} else if (checkResult.equals("1")) {
			return "OK";
		} else {
			return "NG";
		}
	}

	/**
	 * @param checkResult
	 *            セットする checkResult
	 */
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	/**
	 * @return checkMemo
	 */
	public String getCheckMemo() {
		if (TextUtils.isEmpty(checkMemo)) {
			return NO_DATA;
		}
		return checkMemo;
	}

	/**
	 * @param checkMemo
	 *            セットする checkMemo
	 */
	public void setCheckMemo(String checkMemo) {
		this.checkMemo = checkMemo;
	}

	/**
	 * @return checkDate
	 */
	public String getCheckDate() {
		if (TextUtils.isEmpty(checkDate)) {
			return NO_DATA;
		}
		return checkDate;
	}

	/**
	 * @return returnDate
	 */
	public String getCheckDateFormat(String format) {
		if (TextUtils.isEmpty(returnDate)) {
			return NO_DATA;
		}
		return getDateFormat(checkDate, format);
	}

	/**
	 * @param checkDate
	 *            セットする checkDate
	 */
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}

	/**
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            セットする latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            セットする longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return self
	 */
	public AssetInfo getSelf() {
		return self;
	}

	public static final int EXPIRATION = 9; // 返却期限切れ
	public static final int EXPIRATION_SOON = 1; // 返却期限近し
	public static final int NORMAL = 0;		// 正常
	private int mAlertType;

	private void setAlertType(){

		int alertType = NORMAL;

		if (TextUtils.isEmpty(returnDate)) {
			mAlertType = alertType;
			return;
		}
		if (returnDate.contentEquals("-") ){
    		mAlertType = alertType;
    		return;
		}
//    	Date date = _string2date(returnDate);
//    	if (date == null){
//    		mAlertType = alertType;
//    		return;
//    	}

		long time = parseTime(returnDate + TIME_DIFF);
		long dateDiff = toDiffDate(time);

		if (dateDiff > 0) {
			alertType = EXPIRATION;		//返却期限 経過
		} else if (dateDiff > LIMIT_DAY && dateDiff <= 0){
			alertType = EXPIRATION_SOON;	//返却期限 近し

		}
		mAlertType = alertType;

	}

	public int getAlertType(){

		return mAlertType;

	}

	public String getAlertMessage(){

		String alertMessage = "";

		switch	(getAlertType()){
		case EXPIRATION:		//返却期限 経過
			alertMessage = "利用期限が過ぎています！";
			break;
		case EXPIRATION_SOON:  //返却期限 近し
			alertMessage = "利用期限が迫っています！";
			break;
		default:
			alertMessage = "";
			break;
		}
		return alertMessage;

	}


	public int getAlertImageID(){

		int alertImageID = 0;

		switch	(getAlertType()){
		case EXPIRATION:		//返却期限 経過
			alertImageID = android.R.drawable.ic_dialog_alert;
			break;
		case EXPIRATION_SOON:  //返却期限 近し
			alertImageID = android.R.drawable.ic_dialog_info;
			break;
		default:
			alertImageID = 0;
			break;
		}
		return alertImageID;

	}

	public int getAlertColor(){

		int alertImageID = 0;

		switch	(getAlertType()){
		case EXPIRATION:		//返却期限 経過
			alertImageID = R.color.dialog_detaile_alert;
			break;
		case EXPIRATION_SOON:  //返却期限 近し
			alertImageID =R.color.dialog_detaile_info;
			break;
		default:
			alertImageID = 0;
			break;
		}
		return alertImageID;

	}
	private static Time sTime = new Time();

	public static long parseTime(String time) {
		sTime.parse3339(time);
		return sTime.toMillis(false);
	}

	public String getDateFormat(String strDatetime, String format) {
		String formatDate = "";
		long time = parseTime(strDatetime);

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		formatDate = sdf.format(time);
		return formatDate;
	}

    //String文字列型をDate日付型へ変換
    private static long _string2date(String value) {

    	return parseTime(value);
    }

    //現在の日時と比較して、日にちの差分を求める
    private static long toDiffDate(long  time) {

        //現在の日時
        Calendar nowcal = Calendar.getInstance();
        int factor = 1;
        //比較対象日時
//        Calendar dateCal = Calendar.getInstance();
//        dateCal.setTime(date);

        //long型の差分（ミリ秒）
        long diffTime = nowcal.getTimeInMillis() - time;

        if(diffTime < 0){
        	factor = -1;
        	diffTime = diffTime * factor;
        }

        //秒
        long second = diffTime/1000;
        if (second < 60) {
            return 0;
        }

        //分
        long minute = second/60;
        if (minute < 60) {
            return 0;
        }

        //時
        long hour = minute/60;
        if (hour < 24) {
            return 0;
        }

        //日
        long day = hour/24;

        return day * factor;

//        //30日以上の場合
//        //月＋1
//        dateCal.add(Calendar.MONDAY, 1);
//        if (dateCal.after(nowcal)) {
//            return day + "日";   //一ヶ月以内
//        }
//
//        dateCal.setTime(date);  //日付のリセット
//        dateCal.add(Calendar.MONDAY, 12);    //12ヶ月
//        if (dateCal.after(nowcal)) {//一年（12ヶ月）以内
//            for (int i=11; i>=1; i--) {
//                dateCal.setTime(date);  //日付のリセット
//                dateCal.add(Calendar.MONDAY, i);
//                if (dateCal.before(nowcal)) {
//                    return i + "月";   //iヶ月　前
//                }
//            }
//        }
//
//        return "1年";    //1年前

    }
}
