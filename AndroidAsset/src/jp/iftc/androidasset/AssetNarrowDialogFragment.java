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

import java.util.ArrayList;

import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * @author 0A7044
 *
 */
public class AssetNarrowDialogFragment  extends DialogFragment {
    @SuppressWarnings("unused")
    private static final String TAG = AssetNarrowDialogFragment.class.getSimpleName();
    private final AssetNarrowDialogFragment self = this;

    private Spinner mSpinner1;
    private Spinner mSpinner2;
    private Spinner mSpinner3;

    OnNarrowSelectedListener mListener;


    //インスタンス生成時に呼ばれる
    public static AssetNarrowDialogFragment newInstance(int index) {
        AssetNarrowDialogFragment fragment=new AssetNarrowDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("index",index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface OnNarrowSelectedListener {
        public void onNarrowSelected(String[] narrowData);
    }


    // 上位のActivityがコールバックを実装しているか？
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNarrowSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.narrow_layout, null);
        setSpinner( view );

        //絞込みボタンを押下
        Button btn = (Button) view.findViewById(R.id.nerrow_btn);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String NarrowData[] = new String[3];

                NarrowData[0] = mSpinner1.getSelectedItem().toString();
                NarrowData[1] = mSpinner2.getSelectedItem().toString();
                NarrowData[2] = String.valueOf(mSpinner3.getSelectedItemPosition() -1 );

                if ( NarrowData[2].equals( "-1" ) ) NarrowData[2]="－－－";

                //Activityへデータをコールバック
                mListener.onNarrowSelected(NarrowData);
            }
        });

        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.narrow_title);
        return dialog;
    }

    protected void setSpinner(View myview ){
        mSpinner1 = ( Spinner )myview.findViewById(R.id.Spinner_01 );
        mSpinner2 = ( Spinner )myview.findViewById(R.id.Spinner_02 );
        mSpinner3 = ( Spinner )myview.findViewById(R.id.Spinner_03 );

        setSpinnerList(getSpinnerList(AssetInfo.COLUMN_MANAGEMENT_GROUP), mSpinner1);
        setSpinnerList(getSpinnerList(AssetInfo.COLUMN_INSTALLATION_LOCATION), mSpinner2);

        ArrayList<String> sp3 = new ArrayList<String>();
        sp3.add(getString(R.string.detailetitle_alert_check_unconfirmed));
        sp3.add(getString(R.string.detailetitle_btnOK));
        sp3.add(getString(R.string.detailetitle_btnNG));

        setSpinnerList( sp3, mSpinner3);

    }

    protected ArrayList<String> getSpinnerList(String column ){
        //データアクセスクラスの宣言
        AssetInfoDAO dao = new AssetInfoDAO( getActivity() );
        ArrayList<String> astnarrow = new ArrayList<String>();

        astnarrow = dao.getNarrowColumnList(column);

        return astnarrow;
    }

    protected void setSpinnerList(ArrayList<String> listdata, Spinner setsp ){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_layout);

        adapter.add("－－－");
        adapter.addAll(listdata);
        setsp.setAdapter(adapter);
    }

}
