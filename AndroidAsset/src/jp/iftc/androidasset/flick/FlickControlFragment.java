package jp.iftc.androidasset.flick;

import java.util.ArrayList;
import java.util.List;

import jp.iftc.androidasset.AssetDetailDialogFragment;
import jp.iftc.androidasset.AssetListAdapter;
import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetInfo;
import jp.iftc.androidasset.db.AssetInfoDAO;
import jp.iftc.androidasset.flick.FlickUtil.FlickLogic;
import jp.iftc.androidasset.map.AssetMapDialogFragment;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class FlickControlFragment extends Fragment {
	@SuppressWarnings("unused")
	private static final String TAG = FlickControlFragment.class.getSimpleName();
	private final FlickControlFragment self = this;

	private ViewFlipper viewFlipper;

	private int mPageTotal; // 総ページ数
	private int mRowCouont; // 1ページのデータ数
	private static int mActivePage; // 表示中のページ数

	private List<ListView> mListView; // 1ページ分のListView * ページ数
	private List<AssetListAdapter> mAssetListAdapter; // 1ページ分のAdapter * ページ数
	private AssetInfoDAO mDao;
//	private ArrayList<AssetInfo> mListAssetInfo; // 表示対象の全データ
	private ArrayList<ArrayList<AssetInfo>> mListAdapterData;

	private FlickUtil mFlickUtil;
	private OnPageChangeListener mListener;

	private int mListCount;


	private FragmentManager mFragmentManager;
	public AssetMapDialogFragment mMapDialog;

	public boolean mLoadListDataTaskState = false;

	private static boolean mMapDipState = false;		// 保存された地図表示状態
	private static int mSelectedPosition = -1;

	public static FlickControlFragment newInstance(int row) {
		FlickControlFragment f = new FlickControlFragment();

		// パラメータを格納する
		Bundle args = new Bundle();

		args.putInt("row", row);

		f.setArguments(args);

		return f;
	}

    public interface OnPageChangeListener {
        public void onPageChange(int activePage);
    }

    // 上位のActivityがコールバックを実装しているか確認する
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPageChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPageChangeListener");
        }
    }

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(mMapDialog != null){
		    mMapDialog = AssetMapDialogFragment.newInstance(AssetMapDialogFragment.STATIC_LOCATION,0,0);
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

	    // 初期起動時のみViewを作成する。
	    if (savedInstanceState != null){
	        return null;
	    }

		View view = inflater.inflate(R.layout.flick_controller, null, false);
		viewFlipper = (ViewFlipper) view.findViewById(R.id.ViewFlipper);

		// DB接続
		mDao = new AssetInfoDAO(getActivity());

		// データ読み込み
		mRowCouont = getArguments().getInt("row");
		mAssetListAdapter = new ArrayList<AssetListAdapter>();
		mListView = new ArrayList<ListView>();
		mListAdapterData = new ArrayList<ArrayList<AssetInfo>>();
		mListCount = 0;
		mActivePage = 0;

		setMapDipState(false);

		return view;
	}


	/**
	 * 一覧データの読み込み
	 * @param columns：絞り込み条件　カラム名
	 * @param narrows：絞込み条件
	 */
	public void loadData(String[] columns, String[] narrows, boolean reset) {

		if(reset){
			mSelectedPosition = -1;
			mActivePage = 0;

		}

		//一覧表示タスクをスタート
		new LoadListDataTask().execute(columns, narrows);


	}


	/**
	 * ListViewのFooterを生成
	 * @return	：作成したFooterのView
	 */
	private View createListFooter(int page){
		View convertView;
		LayoutInflater layoutinf = null;
		layoutinf = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutinf.inflate(R.layout.listfooter_layout, null);

		Button footer_btnfirst = (Button) convertView.findViewById(R.id.btnfirst);
		footer_btnfirst.setOnClickListener(onFooterClickListener);

		Button footer_btnlast = (Button) convertView.findViewById(R.id.btnlast);
		footer_btnlast.setOnClickListener(onFooterClickListener);

		Button footer_btnnext = (Button) convertView.findViewById(R.id.btnnext);
		footer_btnnext.setOnClickListener(onFooterClickListener);

		Button footer_btnbefore = (Button) convertView.findViewById(R.id.btnbefore);
		footer_btnbefore.setOnClickListener(onFooterClickListener);

		//1ページ目だったら、前・最前は表示しない
		if(page == 0){
			footer_btnfirst.setVisibility(View.INVISIBLE);
			footer_btnbefore.setVisibility(View.INVISIBLE);
		}

		//最終ページ目だったら、次・最後は表示しない
		if (page + 1 == mPageTotal) {
			footer_btnlast.setVisibility(View.INVISIBLE);
			footer_btnnext.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	/**
	 * リストの全件数を返す
	 * @return：リスト件数
	 */
	public int getListCount() {

		return mListCount;
	}

	/**
	 * 表示中のページ番号を返す
	 * @return：表示ページ番号
	 */
	public int getActivePage() {
		return mActivePage;
	}

	/**
	 * 全ページ数を返す
	 * @return
	 */
	public int getPageTotal() {
		return mPageTotal;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
		String title = getString(R.string.listcontext_title);

		// ページ移動と一緒にこのイベントが発生したとき、
		// 配列の範囲外を参照してしまうエラーを防ぐ
		if(mListAdapterData.get(mActivePage - 1).size() < adapterInfo.position){
			return;
		}

        //Footerをクリックした場合何もしない
        if(adapterInfo.position >= mListAdapterData.get((mActivePage - 1)).size()){
            return;
        }

        //選択行を保存
		setSelectedPosition(adapterInfo.position);

		if(mListAdapterData.size() > 0){
				title += mListAdapterData.get(mActivePage - 1).get(adapterInfo.position).getAssetNumber();
		}

		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.menu_listcontext, menu);

		menu.setHeaderTitle(title);

		double lat = 0;
		double lon = 0;

		lat = mListAdapterData.get(mActivePage - 1).get(adapterInfo.position).getLatitude();
		lon = mListAdapterData.get(mActivePage - 1).get(adapterInfo.position).getLongitude();

		if(lat == 0 && lon == 0){
			MenuItem menu_map = (MenuItem) menu.findItem(R.id.disp_map);
			menu_map.setVisible(false);
		}

		mListView.get(mActivePage - 1).setItemChecked(adapterInfo.position, true);

	}


	/* (非 Javadoc)
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 * コンテキストメニュークリック時のリスナ
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item	.getMenuInfo();

		// ページ移動と一緒にこのイベントが発生したとき、
		// 配列の範囲外を参照してしまうエラーを防ぐ
		if(mListAdapterData.get(mActivePage - 1).size() < info.position){
		    Toast.makeText(getActivity().getBaseContext(),"キャンセルしました。", Toast.LENGTH_LONG ).show();
			return false;
		}

		//選択行を保存
		setSelectedPosition(info.position);

		//地図表示
		if(item.getItemId() == R.id.disp_map){
			showMapDialog(info.position);
			// 選択されているアイテムをハイライト
			mListView.get(mActivePage - 1).setItemChecked(info.position, true);

			return true;
		}


		AssetInfo assetInfo = new AssetInfo();
		int insertFlg = 0;

		// DBの更新
		assetInfo.setId(mListAdapterData.get((mActivePage - 1)).get(info.position).getId());
		assetInfo.setCheckResult(Character.toString(item.getNumericShortcut()));
		mDao.saveCheckResult(assetInfo, insertFlg);

		// adapterの更新
		mListAdapterData.get(mActivePage - 1).get(info.position).setCheckResult(
				assetInfo.getCheckResult());

		// 選択されているアイテムをハイライト
		mListView.get(mActivePage - 1).setItemChecked(info.position, true);


		mAssetListAdapter.get(mActivePage - 1).notifyDataSetChanged();


		Log.d("FlickSample", "onContextItemSelected : " + Integer.toString(info.position));

		return true;
	}

	public void showMapDialog(int position){

		double lat = 0;
		double lon = 0;

		lat = mListAdapterData.get(mActivePage - 1).get(position).getLatitude();
		lon = mListAdapterData.get(mActivePage - 1).get(position).getLongitude();

		if(lat == 0 && lon == 0){
		    Toast.makeText(getActivity().getBaseContext(),"	位置情報が未登録です。", Toast.LENGTH_LONG ).show();
			return;
		}
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

		if(mMapDialog == null){
			mMapDialog = AssetMapDialogFragment.newInstance(AssetMapDialogFragment.STATIC_LOCATION,0,0);
		}

//		mMapDialog.setLocation(lat, lon);
		mMapDialog.setLocation(lat, lon, mListAdapterData.get(mActivePage - 1).get(position).getId());
		mMapDialog.show(getFragmentManager(), "dialog");

		setMapDipState(false);

	}

	/**
	 * 詳細ダイアログの表示
	 * @param _id　：　表示する資産情報のID
	 */
	private void showDetailDialog(Long _id) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("DetailDialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		DialogFragment df = AssetDetailDialogFragment.newInstance(_id);
		df.show(ft, "DetailDialog");
		setMapDipState(false);
	}

    /**
	 * リストのクリックリスナ
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		// @Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int arg2, long arg3) {
			Log.d("FlickSample", "onItemClick : " + view.toString() + " arg2:" + Integer.toString(arg2));

			//Footerをクリックした場合何もしない
			if(arg2 >= mListAdapterData.get((mActivePage - 1)).size()){
				return;
			}
			// 選択されているアイテムをハイライト
			mListView.get(mActivePage - 1).setItemChecked(arg2, true);

			// ダイアログ表示
			showDetailDialog(mListAdapterData.get((mActivePage - 1)).get(arg2).getId());

			//選択行を保存
			setSelectedPosition(arg2);

		}
	};

	/**
	 * Footerのボタンのクリックイベントリスナー
	 */
	private OnClickListener onFooterClickListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d("FlickSample", "onFooterClickListener : " + v.toString());
			switch( v.getId() ){
			case R.id.btnfirst:		//最初
				mFlickUtil.goFirst();
				break;
			case R.id.btnlast:		//最後
				mFlickUtil.goLast();
				break;
			case R.id.btnnext:		//次
				mFlickUtil.goRightToLeft();
				break;
			case R.id.btnbefore:	//前
				mFlickUtil.goLeftToRight();
				break;

			default:
				break;
			}

		};

	};

//	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
//		// @Override
//		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//				int arg2, long arg3) {
//			Log.d("FlickSample", "onItemLongClick : " + arg1.toString());
//			return false;
//		}
//	};

	/**
	 * 一覧データ抽出⇒表示　タスククラス
	 * @author 0a6055
	 *
	 */
	class LoadListDataTask extends AsyncTask<String[], Integer, Integer> {

		// 処理中ダイアログ
		private ProgressDialog mProgressDialog = null;

		/**
		 * コンストラクタ
		 */
		public LoadListDataTask() {

		}

		@Override
		protected void onPreExecute() {

			// バックグラウンドの処理前にUIスレッドでダイアログ表示
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle("Please wait");
			mProgressDialog.setMessage("表示データを読み込んでいます・・・");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}

		/* (非 Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(String[]... params) {

			if( mLoadListDataTaskState ){
				return -1;
			}

			mLoadListDataTaskState = true;

			String[] columns = params[0];
			String[] narrows = params[1];

			mListCount = mDao.getNarrowListCount(columns, narrows);

			mPageTotal = (mListCount / mRowCouont);
			if(mListCount % mRowCouont > 0){
				mPageTotal ++;
			}

			if(mAssetListAdapter != null){
				mAssetListAdapter.clear();
			}
			if(mListAdapterData != null){
				mListAdapterData.clear();
			}

			for (int i = 0; i < mPageTotal; i++) {

				ArrayList<AssetInfo> subList = (ArrayList<AssetInfo>) mDao
						.getPageDataList(columns, narrows, i, mRowCouont);
				AssetListAdapter adapter = new AssetListAdapter(getActivity(),
						subList);

				mListAdapterData.add(subList);
				mAssetListAdapter.add(adapter);

			}

			return mListCount;
		}

		@Override
		protected void onPostExecute(Integer listCount) {

			mLoadListDataTaskState = false;

			if(listCount < 0){
				// 処理中ダイアログをクローズ
				mProgressDialog.dismiss();
				return;
			}

			//一覧を表示
			setPage();

			// 処理中ダイアログをクローズ
			mProgressDialog.dismiss();

		}

		/**
		 * 一覧表示
		 */
		private void setPage() {

			if (viewFlipper == null) {
				return;
			}

			// すべてのViewを削除
			viewFlipper.removeAllViews();
			if( mListView != null ){
				mListView.clear();
			}

			int page = 0;

			for (page = 0; mPageTotal > page; page++) {

				mListView.add(new ListView(getActivity()));


				if ( mPageTotal > 1 ) {
				    // ListFooterの登録
    				View convertView = createListFooter(page);

    				convertView.setTag("ListFooter");
    				mListView.get(page).addFooterView(convertView);
				}

				mListView.get(page).setAdapter(mAssetListAdapter.get(page));

				mListView.get(page).setOnItemClickListener(onItemClickListener);
				// ListViewの準備
				mListView.get(page).setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				mListView.get(page).setItemsCanFocus(false);

				registerForContextMenu(mListView.get(page));

				viewFlipper.addView(mListView.get(page), page);
			}

			mPageTotal = page;

			mFlickUtil = new FlickUtil(getActivity(), viewFlipper, new FlickLogic() {
				// @Override
				public void setDataLogic() {
					View v = viewFlipper.getCurrentView();

					mSelectedPosition = -1;
					mListener.onPageChange(mActivePage);
					Log.d("FlickSample",
							"SetDataLogic.setDataLogic : " + v.toString());
				}

				// @Override
				public void rightToLeftLogic() {
					View v = viewFlipper.getCurrentView();

					if(mActivePage == mPageTotal){

						mActivePage = 1;
					} else {

						mActivePage++;
					}

					Log.d("FlickSample",
							"SetDataLogic.rightToLeftLogic now page : "
									+ Integer.toString(mActivePage));
				}

				// @Override
				public void leftToRightLogic() {
					View v = viewFlipper.getCurrentView();

					if(mActivePage == 1){
						mActivePage = mPageTotal;
					} else {
						mActivePage--;
					}

					Log.d("FlickSample",
							"SetDataLogic.leftToRightLogic now page : "
									+ Integer.toString(mActivePage));
				}
		        public void dispTopLogic(){
		        	mActivePage = 1;

		        };

		        public void dispLastLogic(){
		        	mActivePage = mPageTotal;

		        };
			});

			if(mActivePage == 0)	mActivePage = 1;

			//地図情報表示
			if(mMapDipState && mSelectedPosition >= 0) {
				showMapDialog(mSelectedPosition);
			}

			//地図情報表示
			if( mSelectedPosition >= 0) {
				// 選択されているアイテムをハイライト
				mListView.get(mActivePage - 1).setItemChecked(mSelectedPosition, true);

				mListView.get(mActivePage - 1).setSelection(mSelectedPosition - 3);

			}

			mFlickUtil.setDispPage(mActivePage);
			mListener.onPageChange(mActivePage);

		}
	}

	/* (非 Javadoc)
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		//		mMapDialog.dismiss();
		mMapDialog = null;
	}

	/**
	 * MapDialogを消す
	 */
	public void closeDialog(){
		if(mMapDialog != null){
			setMapDipState(mMapDialog.isVisible());
			mMapDialog.dismiss();

		}
	}

	public void setActivePage(int page){
		mActivePage = page;

	}

	public boolean getMapDipState(){

		return mMapDipState;
	}

	public int getSelectedPosition(){
		return mSelectedPosition;

	}

	public void setMapDipState(boolean state){
		mMapDipState = state;
	}

	public void setSelectedPosition(int position){
		mSelectedPosition = position;
	}

	public void setSelectedPosition(){
		if(mListView.size() > 0)
			mSelectedPosition = mListView.get(mActivePage - 1).getCheckedItemPosition();
		else
			mSelectedPosition = -1;
	}
}
