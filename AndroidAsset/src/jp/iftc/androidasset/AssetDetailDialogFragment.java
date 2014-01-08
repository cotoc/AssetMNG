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
package jp.iftc.androidasset;

import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 0A7044
 *
 */
public class AssetDetailDialogFragment  extends DialogFragment implements OnClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = AssetDetailDialogFragment.class.getSimpleName();
    private final AssetDetailDialogFragment self = this;

    //インスタンス生成時に呼ばれる
    public static AssetDetailDialogFragment newInstance(Long index) {
        AssetDetailDialogFragment fragment=new AssetDetailDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putLong("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.assetdetailscreen_layout, null);
        findViews( view );

        //消去用クリックリスナー
        view.setOnClickListener(self);

        return view;
    }

    //自分自身を消去
    public void onClick(View arg0) {
        self.dismiss();
    }

    protected void findViews(View myview ){
        //データアクセスクラスの宣言
        AssetInfoDAO dao = new AssetInfoDAO( getActivity() );
        Long _index = getArguments().getLong("index",0);
        AssetInfo astinf = dao.loadAssetInfo( _index );

        TextView textasset_number = ( TextView )myview.findViewById(R.id.textView_asset_number );
        TextView textasset_type = ( TextView )myview.findViewById(R.id.textView_asset_type );
        TextView textavailability_status = ( TextView )myview.findViewById(R.id.textView_status );
        TextView textcontract_expiration_date = ( TextView )myview.findViewById(R.id.textView_contract_expiration_date );
        TextView textid = ( TextView )myview.findViewById(R.id.textView_id );
        TextView textinstallation_location = ( TextView )myview.findViewById(R.id.textView_installation_location );
        TextView textmanagement_group = ( TextView )myview.findViewById(R.id.textView_management_group );
        TextView textmanagement_member_id = ( TextView )myview.findViewById(R.id.textView_management_member_id );
        TextView textmanagement_member_name = ( TextView )myview.findViewById(R.id.textView_management_member_name );
        TextView textobtain_type = ( TextView )myview.findViewById(R.id.textView_obtain_type );
        TextView textuse_deadline = ( TextView )myview.findViewById(R.id.textView_use_deadline );
        TextView textuse_member_id = ( TextView )myview.findViewById(R.id.textView_use_member_id );
        TextView textuse_member_name = ( TextView )myview.findViewById(R.id.textView_use_member_name );

        textasset_number.setText( String.valueOf( astinf.getAssetNumber() )  );
        textasset_type.setText( String.valueOf( astinf.getAssetType() ) );
//      textavailability_status.setText( String.valueOf( astinf.get.getId() ) );
//        textavailability_status.setText( "利用期限が迫っています！" );
        textavailability_status.setText( astinf.getAlertMessage() );
        textcontract_expiration_date.setText( String.valueOf( astinf.getLeaseDeadlineFormat("yyyy/MM/dd")));
        textid.setText( String.valueOf( astinf.getId() ) );
        textinstallation_location.setText( String.valueOf( astinf.getInstallationLocation() ) );
        textmanagement_group.setText( String.valueOf( astinf.getManagementGroup() ) );
        textmanagement_member_id.setText( String.valueOf( astinf.getManagementMemberId() ) );
        textmanagement_member_name.setText( String.valueOf( astinf.getManagementMemberName() ) );
        textobtain_type.setText( astinf.getObtainType() );
        textuse_deadline.setText( astinf.getReturnDateFormat("yyyy/MM/dd"));
        textuse_member_id.setText( String.valueOf( astinf.getUseMemberId() ) );
        textuse_member_name.setText( String.valueOf( astinf.getUseMemberName() ) );

        if(astinf.getObtainType().equals(AssetInfo.OBTAIN_TYPE_BUY)){
        	textcontract_expiration_date.setVisibility(View.INVISIBLE );
        	myview.findViewById(R.id.titleView_contract_expiration_date ).setVisibility(View.INVISIBLE);
        }

        ImageView Image_availability_status = ( ImageView )myview.findViewById(R.id.Image_status );
        if(astinf.getAlertType() != AssetInfo.NORMAL){
        	Image_availability_status.setImageResource(astinf.getAlertImageID());
        	textavailability_status.setTextColor(getResources().getColor(astinf.getAlertColor()));

        }

    }


}
