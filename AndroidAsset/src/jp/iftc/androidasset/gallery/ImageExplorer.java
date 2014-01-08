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

import java.util.List;

import jp.iftc.androidasset.BaseActivity;
import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetImage;
import jp.iftc.androidasset.db.AssetImageDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author 0a6055
 *
 */
public class ImageExplorer extends BaseActivity {
	@SuppressWarnings("unused")
	private static final String TAG = ImageExplorer.class.getSimpleName();
	private final ImageExplorer self = this;

	private ImageView selectedImageView;
	private Gallery imageMapGallery;
	private BitmapAdapter mapAdapter;
	private Button btnRename;
	private ImageButton btnTrash;
	private Button btnClose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_gallery);

		// 画像ギャラリーから選択された画像を表示するイメージビュー
		selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
		// 画像ギャラリー
		imageMapGallery = (Gallery) findViewById(R.id.mapImageGallery);

		// ギャラリーの画像リストアダプター作成
		mapAdapter = new BitmapAdapter(this);
		imageMapGallery.setAdapter(mapAdapter);
		imageMapGallery
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> adapterView,
							View parent, int position, long id) {
						// 選択された画像をイメージビューに表示
						Bitmap selectedBitmap = (Bitmap) mapAdapter
								.getItem(position);
						selectedImageView.setImageBitmap(selectedBitmap);
					}

				});

		// アプリで保存した画像を画像リストアダプターにロードする
		loadImage(mapAdapter);

		// 画像ロードによりデータが変更されたことを通知する
		// ※これをしないとギャラリーが表示されない
		mapAdapter.notifyDataSetChanged();

		btnRename = (Button) findViewById(R.id.btnRename);
		btnRename.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// リネーム
			}
		});

		btnTrash = (ImageButton) findViewById(R.id.btnTrash);
		btnTrash.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 選択されたファイル削除
			}
		});

		btnClose = (Button) findViewById(R.id.btnMapClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder closeConfirm = new AlertDialog.Builder(
						ImageExplorer.this);
				closeConfirm
						.setTitle("");
				closeConfirm
						.setMessage("");
				closeConfirm.setNegativeButton("とじる",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 閉じる
								finish();
							}
						});
				closeConfirm.setPositiveButton("とじない",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 何もしない
							}
						});
				closeConfirm.show();
			}
		});
	}

	// -- DBから保存された画像をアダプターにロード --
	private void loadImage(BitmapAdapter bitmapAdapter) {
			List<AssetImage> imageList = loadImageData();

			if (imageList.size() > 0) {
				for (int i = 0; i < imageList.size(); i++) {
					// 画像をロードする
					Bitmap bitmap = imageList.get(i).getAssetImageBitmap();
					// イメージリストアダプターに追加
					mapAdapter.addBitmap(bitmap);
				}
			} else {
				// ファイルリストが存在しない場合はトースト
				Toast.makeText(this, "イメージデータがありません。",
						Toast.LENGTH_SHORT).show();
			}

	}

	private List<AssetImage> loadImageData(){
		AssetImageDAO dao = new AssetImageDAO(this);
		Bitmap bitmap = null;
		List<AssetImage> img = dao.loadAllAssetImage();

		return img;

	}
}
