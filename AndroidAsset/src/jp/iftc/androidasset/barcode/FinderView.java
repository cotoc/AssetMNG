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

package jp.iftc.androidasset.barcode;

import jp.iftc.androidasset.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * ファインダーの枠を描画するView
 */
public final class FinderView extends View {

	private static final String TAG = FinderView.class.getSimpleName();

	// レーザーの透明度をループする配列
	private static final int[] LASER_ALPHA = { 0, 64, 128, 192, 255, 192, 128,
			64 };
	private static final int ANIMATION_DELAY = 50;

	private final Paint mPaint;
	private final Rect mBox;
	private Rect mFrame;// フレーム枠
	private final int mMaskColor;// 透過背景の色
	private final int mFrameColor;// フレームの色
	private final int mLaserColor;// レーザーの色
	private int mLaserIndex;// レーザーの透明度配列の要素番号

	/**
	 * FinderViewコンストラクタ
	 * @param context
	 * @param attrs
	 */
	public FinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG, "new ViewfinderView");

		mPaint = new Paint();
		mBox = new Rect();

		Resources resources = getResources();
		mMaskColor = resources.getColor(R.color.finder_mask);
		resources.getColor(R.color.result_mask);
		mFrameColor = resources.getColor(R.color.finder_frame);
		mLaserColor = resources.getColor(R.color.finder_laser);
		mLaserIndex = 0;
	}

	/**
	 * Previewから取得したフレームの大きさを設定する
	 *
	 * @param frame
	 */
	void setFramingRect(Rect frame) {
		mFrame = frame;
	}

	/*
	 * (非 Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {

		if (mFrame == null) {
			return;
		}

//		Log.v(TAG, "onDraw");

		Rect frame = mFrame;
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// ファインダーの外側を半透明で埋める
		mPaint.setColor(mMaskColor);
		mBox.set(0, 0, width, frame.top);
		canvas.drawRect(mBox, mPaint);
		mBox.set(0, frame.top, frame.left, frame.bottom + 1);
		canvas.drawRect(mBox, mPaint);
		mBox.set(frame.right + 1, frame.top, width, frame.bottom + 1);
		canvas.drawRect(mBox, mPaint);
		mBox.set(0, frame.bottom + 1, width, height);
		canvas.drawRect(mBox, mPaint);

		// 2pxのフレームを描画
		mPaint.setColor(mFrameColor);
		mBox.set(frame.left, frame.top, frame.right + 1, frame.top + 2);
		canvas.drawRect(mBox, mPaint);
		mBox.set(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1);
		canvas.drawRect(mBox, mPaint);
		mBox.set(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1);
		canvas.drawRect(mBox, mPaint);
		mBox.set(frame.left, frame.bottom - 1, frame.right + 1,
				frame.bottom + 1);
		canvas.drawRect(mBox, mPaint);

		// 赤色レーザーを描画
		mPaint.setColor(mLaserColor);
		mPaint.setAlpha(LASER_ALPHA[mLaserIndex]);
		mLaserIndex = (mLaserIndex + 1) % LASER_ALPHA.length;// 要素番号をループさせる
		int middle = frame.height() / 2 + frame.top;
		int left = frame.left + 2;
		int right = frame.right - 2;
		mBox.set(left, middle - 1, right, middle + 2);
		canvas.drawRect(mBox, mPaint);

		// レーザーの部分だけANIMATION_DELAY間隔でinvalidateする　
		postInvalidateDelayed(ANIMATION_DELAY, mBox.left, mBox.top, mBox.right,
				mBox.bottom);
	}
}
