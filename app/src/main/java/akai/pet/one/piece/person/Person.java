package akai.pet.one.piece.person;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class Person extends View{
    /**画笔*/
    protected Paint paint;
    /**界面大小，宽高*/
    protected int screenW, screenH;
    /**人物图片的宽高*/
    protected int bmpW, bmpH;
    /**当前系统标题栏的高度，如果全屏则为0*/
    protected float titleBarH;
    /**资源类*/
    protected Resources res;
    /**移动速度, 默认为5*/
    protected int speed = 5;
    /**当前坐标位置*/
    protected float x, y;
    /**触摸时的坐标*/
    protected float touchX, touchY;
    /**当move时，前一个touchX坐标（标识左右移动）*/
    protected float touchPreX;
    /**触摸状态(长按用),-1未开启功能；0开启未触摸；1开启且触摸*/
    protected int onPerson = -1;
    /**触摸down的时间*/
    protected long touchDownTime;
    /**动画大小比例*/
    protected float personSize = 1;
    /**公共，简化*/
    protected Matrix matrix;
    /**用户选择的动画数组，随机出现*/
    protected List<Integer> actionGroup;
    /***************标识,常量************************/
    /**左右判别，向左1，右-1。默认左*/
    protected int leftOrRight = 1;
    /**上下判别，向上1，下-1*/
    protected int upOrDown;
    /**是否绘制时间,是否能绘制*/
    protected boolean drawTime;
    /**当前是否绘制时间,前提是drawTime=true*/
    protected boolean drawTimeNow;
    /**绘制时间标识数*/
    protected int drawTimeFlag;
    /**动画标识*/
    protected int actionFlag;
    /**单个动画变化标识*/
    protected int frameFlag;

    public Person(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    /**** public 需继承的*******************************************************/
    public void init(SharedPreferences sp) {
    }
    /**
     * 随机动画产生
     */
    public void randomChange(){
    }
    /**
     * 触屏事件
     */
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    /**
     * 存储数据改变监听器
     */
    public void onSharedPreferenceChanged(SharedPreferences sp,
                                          String key) {
    }
/********************************************************************/
    /**测量屏幕高宽,由于会出现屏幕旋转问题，所以需要改变*/
    public void measureScreen(){
        screenH = res.getDisplayMetrics().heightPixels;
        screenW = res.getDisplayMetrics().widthPixels;
    }
    /** 获得坐标x */
    public float getX(){
        return x;
    }
    /** 获得坐标y */
    public float getY(){
        return y;
    }
    /** 获得当前图片的宽度 */
    public int getBmpW(){
        return (int) Math.ceil(bmpW*personSize);
    }
    /** 获得当前图片的高度 */
    public int getBmpH(){
        return (int) Math.ceil(bmpH*personSize);
    }
    public long getTouchDownTime() {
        return touchDownTime;
    }
    public void setOnPerson(int onPerson) {
        this.onPerson = onPerson;
    }
    public int getOnPerson() {
        return onPerson;
    }
    protected Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
    protected void canvasDraw(Canvas canvas, Bitmap bitmap, Matrix matrix, Paint paint){
        if(bitmap != null){
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }
}
