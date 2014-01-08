/*
   Copyright 2009 adamrocker

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

// ResultDialog.java
package jp.iftc.androidasset.barcode;

import jp.iftc.androidasset.AssetDetailDialogFragment;
import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import jp.iftc.androidasset.map.GPSLocation;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class BarcodeResultDialogFragment extends DialogFragment implements OnClickListener {

	private static final String TAG = BarcodeResultDialogFragment.class
			.getSimpleName();
	private final BarcodeResultDialogFragment self = this;

	//閉じるイベントリスナー
	private DialogInterface.OnDismissListener mOnDismissListener;

	private ImageView mResultImg;
	private EditText mComment;

	AlertDialog.Builder builder;

	private Button mCancelBtn;

	private Button mAccessBtn;

	private Button mNgBtn;

	/**
	 * Create a new instance of as an argument.
	 */
	static BarcodeResultDialogFragment newInstance(Bitmap img, String assetId,
			int chkResult) {
		BarcodeResultDialogFragment f = new BarcodeResultDialogFragment();

		// パラメータを格納する
		Bundle args = new Bundle();
		args.putParcelable("img", img);
		args.putString("id", assetId);
		args.putInt("chk", chkResult);
		f.setArguments(args);

		return f;
	}

	public BarcodeResultDialogFragment() {
		super();

	}

//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
////		return super.onCreateDialog(savedInstanceState);
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.barcode_resultdialog, null, false);
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//		mResultImg = (ImageView) view.findViewById(R.id.result_img);
//		mComment = (EditText) view.findViewById(R.id.editComment);
////		mCancelBtn = (Button) view.findViewById(R.id.cancel_btn);
////		mCancelBtn.setOnClickListener(this);
////		mAccessBtn = (Button) view.findViewById(R.id.ok_btn);
////		mAccessBtn.setOnClickListener(this);
////		mNgBtn = (Button) view.findViewById(R.id.ng_btn);
////		mNgBtn.setOnClickListener(this);
//
//
////        builder.setTitle("タイトル");
//
//		builder.setView(view);
//
//        //表示データをセット
//		setViewData(builder);
//
//		Dialog dialog = builder.create();
//		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        return dialog;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.barcode_resultdialog, null, false);



		mResultImg = (ImageView) view.findViewById(R.id.result_img);
//		mFormatTextView = (TextView) view.findViewById(R.id.format_txt);
//		mNumTextView = (TextView) view.findViewById(R.id.num_txt);
//		mMessageTextView = (TextView) view.findViewById(R.id.message_txt);
		mComment = (EditText) view.findViewById(R.id.editComment);
		mCancelBtn = (Button) view.findViewById(R.id.barcode_cancel_btn);
		mCancelBtn.setOnClickListener(this);
		mAccessBtn = (Button) view.findViewById(R.id.barcode_ok_btn);
		mAccessBtn.setOnClickListener(this);
		mNgBtn = (Button) view.findViewById(R.id.barcode_ng_btn);
		mNgBtn.setOnClickListener(this);

		//表示データをセット
		setViewData();

		getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return view;
	}

//	/**
//	 * Viewに表示用のデータをセットする
//	 */
//	private void setViewData(AlertDialog.Builder builder) {
//		Log.i(TAG, "setViewData");
//
////		mMessageTextView.setText(getMessage());
//		mResultImg.setImageBitmap((Bitmap) getArguments().getParcelable("img"));
////		mNumTextView.setText("ID: " + getArguments().getString("id"));
//
//        builder.setPositiveButton(R.string.check_btnOK, this);
//        builder.setNeutralButton(R.string.check_btnNG, this);
//        builder.setNegativeButton(R.string.check_btnCancel, this);
//		builder.setTitle("資産ID：" + getArguments().getString("id") + " " + getMessage());
//		builder.setIcon(R.drawable.alert_dialog_icon);
//
//	}
	/**
	 * Viewに表示用のデータをセットする
	 */
	private void setViewData() {
		Log.i(TAG, "setViewData");

		mResultImg.setImageBitmap((Bitmap) getArguments().getParcelable("img"));

		getDialog().setTitle("資産ID：" + getArguments().getString("id") + " " + getMessage());


	}
	/**
	 * ダイアログに表示するメッセージを取得する
	 * @return
	 */
	private String getMessage(){

		String message = null;

		switch (getArguments().getInt("chk")) {
		case AssetInfo.NORECORD:
			message = getString(R.string.message_norecord);
			break;
		case AssetInfo.CHECK_OK:
			message = getString(R.string.message_check_ok);
			break;
		case AssetInfo.CHECK_NG:
			message = getString(R.string.message_check_ng);
			break;
		default:
			message = "";
			break;
		}

		return message;

	}

	public void onClick(View v) {

		int chkType = 0;
		String comment;

		if (v == mAccessBtn) { // OK登録
			// 結果登録
			chkType = 1;

		} else if (v == mNgBtn) { // NG登録
			chkType = 2;

		} else {		//キャンセル
			dismiss();	//ダイアログを閉じる
			return;
		}

		//入力したコメントを取得
		SpannableStringBuilder sb = (SpannableStringBuilder)mComment.getText();
		comment =  sb.toString();

		//確認結果登録
		updateCheckResult(chkType, comment);

		dismiss();	//ダイアログを閉じる

	}

	/**
	 * 確認結果登録
	 * @param chkType
	 * @param comment
	 */
	private void updateCheckResult(int chkType, String comment) {

		AssetInfo assetInfo = new AssetInfo();

		assetInfo.setId( Long.parseLong( getArguments().getString("id")));
		assetInfo.setCheckResult(Integer.toString(chkType));
		assetInfo.setCheckMemo(comment);

		//確認結果OK登録の場合現在位置をセット
		if(assetInfo.getCheckResultInt() == AssetInfo.CHECK_OK){
			setLocateion(assetInfo);
		}
		//未登録データの場合、InsertFlgを1に設定する
		int insertFlg = 0;
		if( getArguments().getInt("chk") == AssetInfo.NORECORD ){
			insertFlg = 1;
		}
		AssetInfoDAO dao = new AssetInfoDAO(this.getActivity());

		AssetInfo newAsset;

		newAsset = dao.saveCheckResult(assetInfo, insertFlg);

		Toast.makeText(this.getActivity(), "管理番号:[" + newAsset.getAssetNumber() + "]" + " ID [" + String.valueOf(newAsset.getId()) + "]" + '\n' + " 確認結果を登録しました。", Toast.LENGTH_LONG).show();

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.app.DialogFragment#onDismiss(android.content.DialogInterface)
	 */
	@Override
	public void onDismiss(DialogInterface dialog) {

		Log.d(TAG, "FragmentDialog Dismiss!");

		super.onDismiss(dialog);
		if (mOnDismissListener != null) {
			mOnDismissListener.onDismiss(dialog);
		}
	}

	/**
	 * リスナーのセット
	 *
	 * @param listener
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		mOnDismissListener = listener;
	}

//	/* (非 Javadoc)
//	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
//	 */
//	public void onClick(DialogInterface dialog, int which) {
//		int chkType = 0;
//		String comment;
//
//		if (which == DialogInterface.BUTTON1) { // OK登録
//			// 結果登録
//			chkType = 1;
//
//		} else if (which == DialogInterface.BUTTON3) { // NG登録
//			chkType = 2;
//
//		} else {		//キャンセル
//			dismiss();	//ダイアログを閉じる
//			return;
//		}
//
//		//入力したコメントを取得
//		SpannableStringBuilder sb = (SpannableStringBuilder)mComment.getText();
//		comment =  sb.toString();
//
//		//確認結果登録
//		updateCheckResult(chkType, comment);
//
//		dismiss();	//ダイアログを閉じる
//	}
	/**
	 * 現在の位置情報をセットする
	 * @param assetInfo
	 */
	private void setLocateion(AssetInfo assetInfo){

		//位置情報の取得
		GPSLocation gps = new GPSLocation(getActivity());
		double locationLat = 0;
		double locationLon = 0;
		if(gps.getmLocation() != null){
			locationLat = gps.getmLocationLat();
			locationLon = gps.getmLocationLon();
		}

		assetInfo.setLatitude(locationLat);
		assetInfo.setLongitude(locationLon);

	}

}
