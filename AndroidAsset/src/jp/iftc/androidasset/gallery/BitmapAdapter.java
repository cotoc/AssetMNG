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

package jp.iftc.androidasset.gallery;

import java.util.ArrayList;
import java.util.List;

import jp.iftc.androidasset.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * @author 0a6055
 *
 */
public class BitmapAdapter extends BaseAdapter {
	@SuppressWarnings("unused")
	private static final String TAG = BitmapAdapter.class.getSimpleName();
	private final BitmapAdapter self = this;

	private Context context;
	// ロードされた画像ファイルを保持するリスト
	private List<Bitmap> imageItems;
	private int galleryItemBackground;

	/**
	 * Constractor
	 *
	 * @param context
	 *            このアダプターを使用するビューが属するコンテキスト
	 */
	public BitmapAdapter(Context context) {
		this.context = context;
		// res/values/attrs.xml
		// <declare-styleable name="myGallery">
		// <attr name="android:galleryItemBackground"/>
		// </declare-styleable>
		//
		// obtainStyledAttributes(int[] attrs)
		// Return a StyledAttributes holding the values
		// defined by Theme which are listed in attrs.
		TypedArray typedArray = context
				.obtainStyledAttributes(R.styleable.myGallery);

		// typedArray.getResourceId(int index, int defValue)
		// Retrieve the resource identifier for the attribute at index.
		galleryItemBackground = typedArray.getResourceId(
				R.styleable.myGallery_android_galleryItemBackground, 0);

		// Give back a previously retrieved StyledAttributes, for later re-use.
		// 後の再使用のために以前に検索されたStyledAttributesを返す。
		typedArray.recycle();

		// Create ImageList
		imageItems = new ArrayList<Bitmap>();
	}

	// メインクラスでロードした画像イメージを追加します
	public void addBitmap(Bitmap image) {
		imageItems.add(image);
	}

	// 不要になった画像イメージをリストから削除します
	public void deleteBitmap(int index) {
		imageItems.remove(index);
	}

	// クリーンアップ
	public void clear() {
		imageItems.clear();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		// TODO 自動生成されたメソッド・スタブ
		return imageItems.size();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {

		return imageItems.get(position);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		// TODO 自動生成されたメソッド・スタブ
		return position;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	public View getView(int position, View view, ViewGroup parent) {
		// ギャラリーに画像を表示するためのイメージビュー作成
		ImageView imageView = new ImageView(context);

		// 表示する画像
		Bitmap bitmap = (Bitmap) getItem(position);
		imageView.setImageBitmap(bitmap);

		// イメージビューでの表示(フィット[リサイズして]中央揃え)
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new Gallery.LayoutParams(200, 240));

		// イメージビューの背景
		imageView.setBackgroundResource(galleryItemBackground);
		return imageView;
	}

}
