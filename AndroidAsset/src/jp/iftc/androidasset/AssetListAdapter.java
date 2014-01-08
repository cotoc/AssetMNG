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

package jp.iftc.androidasset;

import java.util.ArrayList;

import jp.iftc.androidasset.db.AssetInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 0A7044
 *
 * 1レコード分のリストデータを保持するクラス
 */
public class AssetListAdapter extends BaseAdapter {
	/**
	 *
	 */
	@SuppressWarnings("unused")
	private static final String TAG = AssetListAdapter.class.getSimpleName();
	private final AssetListAdapter self = this;

	private Integer[] mImageid = {
	    R.drawable.list_nocheck,
        R.drawable.list_ok,
        R.drawable.list_ng
	};

    private LayoutInflater mLayoutInfo = null;
    private ArrayList<AssetInfo> mAssetListInfo = null;
    private ViewHolder mHolder;

    public AssetListAdapter(Context context, ArrayList<AssetInfo> _mAssetListInfo) {

        //LayoutInflaterを取得
        mLayoutInfo = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mAssetListInfo = _mAssetListInfo;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //assetlist.xmlから1行分のレイアウトを生成
            convertView = mLayoutInfo.inflate(R.layout.assetlist, parent, false);

            mHolder = new ViewHolder();

            //各idの項目に値をセット
            mHolder.list_view = (ImageView) convertView.findViewById(R.id.imageView_list);
            mHolder.list_id = (TextView) convertView.findViewById(R.id.textView_list_id);
            mHolder.list_asset_number    = (TextView) convertView.findViewById(R.id.textView_list_asset_number);
            mHolder.list_management_group  = (TextView) convertView.findViewById(R.id.textView_list_management_group);
            mHolder.list_asset_type = (TextView) convertView.findViewById(R.id.textView_list_asset_type);
            mHolder.list_installation_location  = (TextView) convertView.findViewById(R.id.textView_list_installation_location);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.list_view.setImageResource(mImageid[Integer.valueOf(mAssetListInfo.get(position).getCheckResult())]);
        mHolder.list_id.setText(String.valueOf(mAssetListInfo.get(position).getId()));
        mHolder.list_asset_number.setText(mAssetListInfo.get(position).getAssetNumber());
        mHolder.list_management_group.setText(mAssetListInfo.get(position).getManagementGroup());
        mHolder.list_asset_type.setText(mAssetListInfo.get(position).getAssetType());
        mHolder.list_installation_location.setText(mAssetListInfo.get(position).getInstallationLocation());

        return convertView;
    }

    public int getCount() {
        return mAssetListInfo.size();
    }

    public Object getItem(int position) {
        return mAssetListInfo.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView list_view;
        TextView list_id;
        TextView list_asset_number;
        TextView list_management_group;
        TextView list_asset_type;
        TextView list_installation_location;
    }

}


