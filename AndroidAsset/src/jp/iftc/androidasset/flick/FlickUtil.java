package jp.iftc.androidasset.flick;

import jp.iftc.androidasset.AssetsListScreenActivity;
import jp.iftc.androidasset.R;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class FlickUtil {
	private static final String TAG = FlickUtil.class.getSimpleName();

	private ViewFlipper viewFlipper;
    private GestureDetector gestureDetecotr;
    private Animation inFromLeft;
    private Animation outToRight;
    private Animation inFromRight;
    private Animation outToLeft;
    private FlickLogic setDataLogic;

    // ジェスチャーリスナー
    OnGestureListener gestureListener = new OnGestureListener() {
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = Math.abs(velocityX);
            float dy = Math.abs(velocityY);

            //ほぼ同じ行内(縦方向の移動)でFlickした場合はFlickイベントは何もしない。
            if(Math.abs( e1.getY() - e2.getY()) < 50){
            	return true;
            }

            if (dx > dy && dx > 150) {
                if (e1.getX() < e2.getX()) {
                	goLeftToRight();
                } else {
                	goRightToLeft();
                }
                setDataLogic.setDataLogic();
                Log.d(TAG, "OnGestureListener.onFling");
                return true;
            }

            return false;
        }

        public boolean onDown(MotionEvent e) {
            return false;
        }
    };

    // タッチ処理リスナー
    OnTouchListener touchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetecotr.onTouchEvent(event);
        }
    };

    /***
     * コンストラクタ
     * @param context
     * @param flipper
     * @param logic
     */
    public FlickUtil(Context context, ViewFlipper flipper, FlickLogic logic) {
        viewFlipper = flipper;
        gestureDetecotr = new GestureDetector(context, gestureListener);

        inFromLeft = AnimationUtils.loadAnimation(context, R.anim.in_from_left);
        outToRight = AnimationUtils.loadAnimation(context, R.anim.out_to_right);
        inFromRight = AnimationUtils.loadAnimation(context, R.anim.in_from_right);
        outToLeft = AnimationUtils.loadAnimation(context, R.anim.out_to_left);

        setOnTouchListener(context, flipper);
        setDataLogic(logic);
    }

    /***
     * ViewFlipperの子ViewにOnTouchListenerを設定する
     * @param context
     * @param flipper
     */
    private void setOnTouchListener(Context context, ViewFlipper flipper) {
        int child_count = viewFlipper.getChildCount();
        for (int i = 0; i < child_count; i++) {
            viewFlipper.getChildAt(i).setOnTouchListener(touchListener);
        }
    }

    /***
     * setDataLogicを設定する
     * @param logic
     */
    private void setDataLogic(FlickLogic logic) {
        setDataLogic = logic;
    }

    /***
     * フリックに関する処理に関するインタフェース
     *
     * @author nakaji
     *
     */
    public interface FlickLogic {
        public void setDataLogic();

        public void rightToLeftLogic();

        public void leftToRightLogic();

        public void dispTopLogic();

        public void dispLastLogic();
    }

    public void goRightToLeft(){

        viewFlipper.setInAnimation(inFromRight);
        viewFlipper.setOutAnimation(outToLeft);
        viewFlipper.showNext();
        setDataLogic.rightToLeftLogic();

        setDataLogic.setDataLogic();

        Log.d(TAG, "goRightToLeft");
    }

    public void goLeftToRight(){
        viewFlipper.setInAnimation(inFromLeft);
        viewFlipper.setOutAnimation(outToRight);
        viewFlipper.showPrevious();
        setDataLogic.leftToRightLogic();

        setDataLogic.setDataLogic();

        Log.d(TAG, "goLeftToRight");
    }

    public void goFirst(){
        viewFlipper.setInAnimation(inFromLeft);
        viewFlipper.setOutAnimation(outToRight);

        viewFlipper.setDisplayedChild(0);

    	setDataLogic.dispTopLogic();
        setDataLogic.setDataLogic();

        Log.d(TAG, "goFirst");
    }

    public void goLast(){
        viewFlipper.setInAnimation(inFromRight);
        viewFlipper.setOutAnimation(outToLeft);

    	viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);

    	setDataLogic.dispLastLogic();
        setDataLogic.setDataLogic();

        Log.d(TAG, "goLast");
    }

    public void setDispPage(int page){

        viewFlipper.setDisplayedChild(page - 1);

        Log.d(TAG, "setDispPage");
    }
}
