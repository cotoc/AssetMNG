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

// Decoder.java
package jp.iftc.androidasset.barcode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

// 画像byte配列からバーコードの値を解析する
final public class Decoder {

	private static final String TAG = Decoder.class.getSimpleName();

	private final MultiFormatReader mMultiFormatReader;
	private Bitmap mResultBitmap;

	Decoder() {
		Log.i(TAG, "new Decoder");
		mMultiFormatReader = new MultiFormatReader();
	}

	// 解析したバーコード画像を取得する nullが返るかもしれないことに注意
	Bitmap getResultBitmap0() {
		return mResultBitmap;
	}

	/**
	 * 引数で渡した画像データからバーコードを解析する
	 *
	 * @param data
	 *            画像データ
	 * @param width
	 *            画像の幅
	 * @param height
	 *            画像の高さ
	 * @param frame
	 *            解析する領域
	 * @return
	 */
	Result decode(byte[] data, int width, int height, Rect frame) {
		Log.i(TAG, "decode");

		if (data == null) {
			return null;
		}

		Result rawResult = null;
		// YUVMonochromeBitmapSource source =
		// new YUVMonochromeBitmapSource(data,width, height, frame);
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
				width, height, frame.left, frame.top, frame.width(),
				frame.height(), false);
		if (source != null) {
			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
					source));
			try {
				// rawResult = mMultiFormatReader.decodeWithState(source);
				// mResultBitmap = source.renderToBitmap();
				rawResult = mMultiFormatReader.decodeWithState(binaryBitmap);
				mResultBitmap = source.renderCroppedGreyscaleBitmap();
				// } catch (ReaderException e) {
				// Ignore
			} catch (NotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return rawResult;
	}
}
