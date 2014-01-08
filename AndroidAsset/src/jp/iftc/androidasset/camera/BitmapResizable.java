/*
 * Copyright © 2012 Infotec Inc. All Rights Reserved.
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

package jp.iftc.androidasset.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * @author 0a6055
 *
 */
public class BitmapResizable {
	@SuppressWarnings("unused")
	private static final String TAG = BitmapResizable.class.getSimpleName();
	private final BitmapResizable self = this;

	private BitmapDrawable bitmapDrawAbel;
	private Bitmap bitmap;
	private Rect rect;
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BitmapResizable(Bitmap bitmap) {
		this.bitmap = bitmap;
		this.bitmapDrawAbel = new BitmapDrawable(this.bitmap);
	}

	/**
	 * サイズ変換
	 *
	 * @param resizeWidth
	 *            リサイズ後の幅
	 * @param resizeHeight
	 *            リサイズ後の高さ
	 */
	public Bitmap resize(float resizeWidth, float resizeHeight) {
		float resizeScaleWidth;
		float resizeScaleHeight;
		float resizeScale;


		//長いほうの辺にあわせて、同じ比率でリサイズする
		if(resizeWidth >= resizeHeight){
			resizeScale = resizeWidth / this.bitmap.getWidth();
		} else {
			resizeScale = resizeHeight / this.bitmap.getHeight();
		}


		Matrix matrix = new Matrix();
		resizeScaleWidth = resizeScale;
		resizeScaleHeight = resizeScale;


		matrix.postScale(resizeScaleWidth, resizeScaleHeight);
		Bitmap resizeBitmap = Bitmap.createBitmap(this.bitmap, 0, 0,
				this.bitmap.getWidth(), this.bitmap.getHeight(), matrix, true);

		this.bitmapDrawAbel = new BitmapDrawable(resizeBitmap);

		return resizeBitmap;
	}

	public void draw(int x, int y, Canvas canvas) {
		this.rect = new Rect(x, y, x + this.bitmapDrawAbel.getIntrinsicWidth(),
				this.y + this.bitmapDrawAbel.getIntrinsicHeight());
		this.bitmapDrawAbel.setBounds(this.rect);
		this.bitmapDrawAbel.draw(canvas);
	}

	public void draw(Canvas canvas) {
		this.rect = new Rect(this.x, this.y, this.x
				+ this.bitmapDrawAbel.getIntrinsicWidth(), this.y
				+ this.bitmapDrawAbel.getIntrinsicHeight());
		this.bitmapDrawAbel.setBounds(this.rect);
		this.bitmapDrawAbel.draw(canvas);
	}

}
