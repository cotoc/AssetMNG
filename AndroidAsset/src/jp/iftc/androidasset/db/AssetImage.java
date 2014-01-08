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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.iftc.androidasset.R;
import jp.iftc.androidasset.camera.BitmapResizable;

import org.apache.http.impl.cookie.DateParseException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * @author 0a6055
 *
 *         1レコード分のデータを保持するクラス
 */
public class AssetImage implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = AssetImage.class.getSimpleName();
	private final AssetImage self = this;

	// TableName
	public static final String TABLE_NAME = "assets_image";

	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ASSET_ID = "asset_id";
	public static final String COLUMN_TYPE = "image_type";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_THUM = "thum";

	private Long id = null;
	private Long assetId = null;
	private int type = 0;
	private byte[] assetImage = null;
	private byte[] assetThum = null;

	public static final int TYPE_PICTURE = 1;	// 写真
	public static final int TYPE_MAP = 2;		// 地図
	public static final int TYPE_OTHER = 0;		// その他

	/**
	 * @return self
	 */
	public AssetImage getSelf() {
		return self;
	}


	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param id セットする id
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return assetNumber
	 */
	public Long getAssetId() {
		return assetId;
	}


	/**
	 * @param assetNumber セットする assetNumber
	 */
	public void setAssetId(Long assetNumber) {
		this.assetId = assetNumber;
	}


	/**
	 * @return assetImage
	 */
	public byte[] getAssetImage() {
		return assetImage;
	}


	/**
	 * @return assetImageをBitmapに変換
	 */
	public Bitmap getAssetImageBitmap() {
		return BitmapFactory.decodeByteArray(assetImage, 0, assetImage.length);
	}


	/**
	 * @param assetImage セットする assetImage
	 */
	public void setAssetImage(byte[] assetImage) {
		this.assetImage = assetImage;
	}


	/**
	 * @param bitmapAssetImage セットする assetImage
	 * 						Bitmapデータ
	 */
	public void setAssetImageBitmap(Bitmap bitmapAssetImage) {

		Bitmap resizeBitmap = null;
		//Bitmapデータをリサイズ
		BitmapResizable bitmapResizable = new BitmapResizable(bitmapAssetImage);;

		float resizeWidth = 500;
		float resizeHeight = 500;

		resizeBitmap = bitmapResizable.resize(resizeWidth, resizeHeight);

		//Bitmapをbyte[]に変換
		byte[] assetImage = bitmap2Byte(resizeBitmap);

		this.assetImage = assetImage;
	}


	/**
	 * jpeg形式のBitmapデータをbyte[]に変換する
	 * @param image
	 * @return
	 */
	public byte[] bitmap2Byte(Bitmap image){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, os);
		byte[] bin = os.toByteArray();

		return bin;

	}


	/**
	 * @return assetThum
	 */
	public byte[] getAssetThum() {
		return assetThum;
	}


	/**
	 * @return assetImageをBitmapに変換
	 */
	public Bitmap getAssetThumBitmap() {
		return BitmapFactory.decodeByteArray(assetThum, 0, assetThum.length);
	}


	/**
	 * @param assetThum セットする assetThum
	 */
	public void setAssetThum(byte[] assetThum) {
		this.assetThum = assetThum;
	}


	/**
	 * @return type
	 */
	public int getType() {
		return type;
	}


	/**
	 * @param type セットする type
	 */
	public void setType(int type) {
		this.type = type;
	}


}
