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

import jp.iftc.androidasset.BarcodeReaderActivity;
import jp.iftc.androidasset.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;

public class ResultDialog extends Dialog implements
		android.view.View.OnClickListener {

    private static final String TAG = ResultDialog.class.getSimpleName();

	// AmazonのURL
	private static final String AMAZON_BASE = "http://www.amazon.co.jp/dp/";

	private ImageView mResultImg;
	private TextView mNumTextView;
	private TextView mFormatTextView;
	private TextView mConvTextView;
	private Button mAccessBtn;
	private Button mCancelBtn;
	private Bitmap img;
	private BarcodeReaderActivity mBarcodReaderActivity;
	private String mIsbn10;

	public ResultDialog(Context context) {
		super(context);
		mBarcodReaderActivity = (BarcodeReaderActivity) context;

		setContentView(R.layout.result_dialog);

		mResultImg = (ImageView) findViewById(R.id.result_img);
		mFormatTextView = (TextView) findViewById(R.id.format_txt);
		mNumTextView = (TextView) findViewById(R.id.num_txt);
		mConvTextView = (TextView) findViewById(R.id.conv_txt);
		mCancelBtn = (Button) findViewById(R.id.cancel_btn);
		mCancelBtn.setOnClickListener(this);
		mAccessBtn = (Button) findViewById(R.id.access_btn);
		mAccessBtn.setOnClickListener(this);

		setTitle("Access web?");
		mResultImg.setImageBitmap(img);
	}

	void set(Bitmap img, Result result) {
		Log.i(TAG, "set");
		mResultImg.setImageBitmap(img);
		String format = result.getBarcodeFormat().toString();
		String num = result.getText();
		mIsbn10 = convISBN10(num);

		mFormatTextView.setText("FORMAT: " + format);
		mNumTextView.setText("NUM: " + num);
		mConvTextView.setText("ISBN-10: " + mIsbn10);
	}

	public void onClick(View v) {
		if (v == mAccessBtn) {
			accessAmazon(mIsbn10);
		} else if (v == mCancelBtn) {
			// オートフォーカスを開始することでバーコード解析も開始する
			mBarcodReaderActivity.requestAutoFocus();
			dismiss();
		}
	}

	// Amazonのウェブページを表示する
	private void accessAmazon(String asin) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(AMAZON_BASE + asin));
		mBarcodReaderActivity.startActivity(i);
	}

	/**
	 * ISBN13からISBN10へ変換する
	 *
	 * @param isbn13
	 * @return
	 */
	private String convISBN10(String isbn13) {
		Log.i(TAG, "convISBN10(isbn13:" + isbn13 + ")");
		if (isbn13.length() != 13) {
			return null;
		}

		int checkDigit = 0;
		String isbn10 = isbn13.substring(3, 12);
		char[] isbn10c = isbn10.toCharArray();
		for (int i = 0; i < isbn10c.length; ++i) {
			int val = Character.digit(isbn10c[i], 10);
			checkDigit += (val * (10 - i)) % 11;
		}
		checkDigit = 11 - checkDigit % 11;
		isbn10 += checkDigit == 10 ? "X" : Integer.toString(checkDigit);

		return isbn10;
	}

}
