package akai.pet.one.piece.person;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import akai.floatView.op.luffy.R;
import akai.pet.one.piece.store.DataByFile;
import akai.pet.one.piece.store.KLog2File;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.widget.Toast;

public class DownloadPerson extends Person{

    public final static String ACTION_KEY_NAME = "action_name";
    public final static String ACTION_KEY_FUN = "action_function";

    private final static int FLAG_UP = -1;
    private final static int FLAG_DOWN = -2;

    private Bitmap[] bmpImage = new Bitmap[3];

    private String mPersonName;

    /**
     * FLAG_DOWN, FLAG_UP, 0, 1,2...
     */
    private int mActionFun;

    private List<HashMap<String, Object>> mActionList;

    public DownloadPerson(Context context, String name) {
        super(context);
        mPersonName = name;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        setFocusable(true);
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Context.MODE_PRIVATE);
        res = context.getResources();
        init(sp);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try{
            draw(canvas, paint);
        }catch(Exception e){
            showErrorAndClose(e);
        }
    }
    /***************************继承start***********************************/
    /**
     * 初始化数据处理
     */
    @Override
    public void init(SharedPreferences sp){
        //获取界面大小
        measureScreen();
        //人物大小（注意，可能手机上面获得的显示大小跟实际上的不一样）
        Bitmap bitmap = DataByFile.getPersonIcon(mPersonName);
        bmpW = bitmap.getWidth();
        bmpH = bitmap.getHeight();
        speed = bmpW/10;
        bitmap.recycle();

        personSize = Float.parseFloat(sp.getString("person_size", "1"));
        drawTime = sp.getBoolean("draw_time", false);
        //长按设置功能
        onPerson = sp.getBoolean("long_down", false)?0:-1;
//存储数据
        setActionGroup(sp);

//初始化的xy
        x = bmpW/2;
        y = bmpH/2;
        onActionChange(FLAG_DOWN);
    }

    /**
     * 随机动画产生
     */
    @Override
    public void randomChange(){
        if(actionFlag != FLAG_UP){
            int count = mActionList.size();
            int i = (int)( Math.random()*count );
            if(actionFlag != i)//don't action the same
            {
                onActionChange(i);
            }
        }
    }
    /**
     * 触屏事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getRawX();
        touchY = event.getRawY();
        int bmpW = (int) (this.bmpW*personSize), bmpH = (int) (this.bmpH*personSize);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDownTime = System.currentTimeMillis();
                if( !(onPerson == -1) )onPerson = 1;
                onActionChange(FLAG_UP);
                touchPreX = touchX;
                titleBarH = touchY - event.getY() - y;
                break;
            case MotionEvent.ACTION_MOVE:
                if( !(onPerson == -1) )onPerson = 0;
                //触摸点的显示作用
                x = touchX - bmpW/2;
                y = touchY - bmpH/10 - titleBarH;
                break;
            case MotionEvent.ACTION_UP:
                if( !(onPerson == -1) )onPerson = 0;
                onActionChange(FLAG_DOWN);
                titleBarH = 0;
                //报时的绘制点
                if(drawTime){
                    drawTimeNow = true;
                    drawTimeFlag = 10;
                }
                break;
        }
        return true;
    }
    /**
     * 存储数据改变监听器
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp,
                                          String key) {
        if(key.indexOf(mPersonName + "_action_") != -1){
            setActionGroup(sp);
        }
        else if("person_size".equals(key)){
            personSize = Float.parseFloat(sp.getString("person_size", "1"));
        }
        else if("draw_time".equals(key)){
            drawTime = sp.getBoolean("draw_time", false);
        }
        else if("long_down".equals(key)){
            onPerson = sp.getBoolean("long_down", false)?0:-1;
        }
    }
    /***************************继承end***********************************/
    /**
     * 绘图
     */
    private void draw(Canvas canvas, Paint paint){
        //公用的设置
        float dis = (1-personSize)*bmpW/2;
        matrix = new Matrix();
        matrix.postScale(personSize*leftOrRight, personSize, bmpW/2, bmpH/2);
        matrix.postTranslate(-dis, -dis);
        switch(mActionFun){
            case FLAG_DOWN:
                actionDown(canvas, paint);
                break;
            case FLAG_UP:
                actionUp(canvas, paint);
                break;
//		case 0:
//			action0(canvas, paint);
//			break;
//		case 1:
//			action1(canvas, paint);
//			break;
            default:
            {
                try {
                    Method method = this.getClass().getDeclaredMethod("action"+mActionFun, Canvas.class, Paint.class);
                    method.invoke(this, canvas, paint);
                } catch (Exception e) {
                    showErrorAndClose(e);
                }
            }
        }
        if(drawTimeNow)
            actionTime(canvas, paint);
    }
    /*********************动作,action*******************************************/
    /**
     * 动作：下降,down
     */
    private void actionDown(Canvas canvas, Paint paint){
        if(frameFlag <= 1){
            canvas.drawBitmap(bmpImage[0], matrix, paint);
            int disY = (int) (screenH - bmpH/2 - bmpH*personSize/2);
            if(y < disY)
                y += speed*personSize * 3;
        }else if(frameFlag <= 3){
            canvas.drawBitmap(bmpImage[1], matrix, paint);
        }else{
            int disY = (int) (screenH - bmpH/2 - bmpH*personSize/2);
            if(y > disY)
                y = disY;
            canvas.drawBitmap(bmpImage[1], matrix, paint);
            randomChange();
        }
        frameFlag ++;
    }

    /**
     * 动作：悬空,up
     */
    private void actionUp(Canvas canvas, Paint paint){
        //动作判断
        if(touchX - touchPreX > 0){//向右移动
            if(frameFlag < 1){
                frameFlag = 1;
                leftOrRight = 1;
            }else if(frameFlag == 1){
                frameFlag = 2;
            }
        }else if(touchX - touchPreX < 0){//向左移动
            if(frameFlag > -1){
                frameFlag = -1;
                leftOrRight = -1;
            }else if(frameFlag == -1){
                frameFlag = -2;
            }
        }else{//未移动(若移动过，则需要摆回来)
            if(frameFlag != 0){
                frameFlag -= leftOrRight;
            }
        }
        //动作绘制
        canvas.drawBitmap(bmpImage[frameFlag*leftOrRight], matrix, paint);
        //记录前一个点
        touchPreX = touchX;
    }

    /**
     * three frame, walk around
     * sanji_walk
     * @param canvas
     * @param pain
     */
    private void action0(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1
                ||  x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;

        if(upOrDown != 0)
        {
            if(y <= 0 && upOrDown == 1
                    ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
                upOrDown *= -1;
            y -= speed*upOrDown;//移动
        }

        x -= speed*leftOrRight;//移动
        //采用了循环方法，stand -> walk1 -> stand -> walk2
        if(frameFlag%2 == 1)
            canvas.drawBitmap(bmpImage[0], matrix, paint);
        else if(frameFlag/2%2 == 0)
            canvas.drawBitmap(bmpImage[1], matrix, paint);
        else
            canvas.drawBitmap(bmpImage[2], matrix, paint);

        frameFlag ++;
    }

    /**
     * only one frame, and no move
     * sanji_squat
     * @param canvas
     * @param pain
     */
    private void action1(Canvas canvas, Paint paint){
        canvas.drawBitmap(bmpImage[0], matrix, paint);
    }

    /**
     * three frame, no move
     * sanji_smoke
     * @param canvas
     * @param pain
     */
    private void action2(Canvas canvas, Paint paint){
        if(frameFlag < 10){
            canvas.drawBitmap(bmpImage[0], matrix, paint);
        }else if(frameFlag < 15){
            canvas.drawBitmap(bmpImage[1], matrix, paint);
        }else if(frameFlag < 20){
            canvas.drawBitmap(bmpImage[2], matrix, paint);
        }else{
            frameFlag = 0;
            action2(canvas, paint);
            return;
        }
        frameFlag ++;
    }

    /**
     * three frame, no move, sometimes stop, sometimes shake
     * sanji_sit
     * @param canvas
     * @param pain
     */
    private void action3(Canvas canvas, Paint paint){
        boolean isChange = Math.random()*100 / 95 > 1;
        if(frameFlag < 100){
            canvas.drawBitmap(bmpImage[0], matrix, paint);
            if(isChange)
                frameFlag = 100;
        }else{
            canvas.drawBitmap(bmpImage[frameFlag/4%2 + 1], matrix, paint);
            if(isChange)
                frameFlag = 0;
        }
        frameFlag ++;
    }

    /**
     * two frame, moving
     * ace_crawl
     * @param canvas
     * @param paint
     */
    private void action4(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1
                ||  x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;


        canvas.drawBitmap(bmpImage[frameFlag/6%2], matrix, paint);
        frameFlag ++;
        if(frameFlag % 6 == 5  &&  frameFlag/6%2 == 1){
            x -= speed*leftOrRight;//移动
            if(upOrDown != 0)
            {
                if(y <= 0 && upOrDown == 1
                        ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
                    upOrDown *= -1;
                y -= speed*upOrDown;//移动
            }
        }
    }

    /**
     * two frame, walk around
     * sanji_love
     * @param canvas
     * @param pain
     */
    private void action5(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1 || x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;
        x -= speed*personSize*leftOrRight;//移动

        if(upOrDown != 0)
        {
            if(y <= 0 && upOrDown == 1
                    ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
                upOrDown *= -1;
            y -= speed*upOrDown;//移动
        }

        canvas.drawBitmap(bmpImage[frameFlag%2], matrix, paint);
        frameFlag ++;
    }

    /**
     * two frame, no moving
     * joker_dance
     * @param canvas
     * @param pain
     */
    private void action6(Canvas canvas, Paint paint){
        if(frameFlag < 21){
            frameFlag ++;
        }
        else{
            if(Math.random() * 100 > 90)
                frameFlag = 0;
        }
        canvas.drawBitmap(bmpImage[frameFlag/3%2], matrix, paint);
    }

    /**
     * one frame, fly around
     * joker_fly
     * @param canvas
     * @param pain
     */
    private void action7(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1 || x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;
        x -= speed*personSize*leftOrRight;//移动

        if(upOrDown != 0)
        {
            if(y <= 0 && upOrDown == 1
                    ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
                upOrDown *= -1;
            y -= speed*upOrDown;//移动
        }

        canvas.drawBitmap(bmpImage[0], matrix, paint);
    }

    /**
     * three frame, walk around one by one
     * @param canvas
     * @param pain
     */
    private void action8(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1
                ||  x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;

        if(upOrDown != 0)
        {
            if(y <= 0 && upOrDown == 1
                    ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
                upOrDown *= -1;
            y -= speed*upOrDown;//移动
        }

        x -= speed*leftOrRight;//移动
        //采用了循环方法，stand -> walk1 -> stand -> walk2
        canvas.drawBitmap(bmpImage[frameFlag/3%3], matrix, paint);

        frameFlag ++;
    }

    /**
     * two frame, walk around one by one
     * @param canvas
     * @param pain
     */
    private void action9(Canvas canvas, Paint paint){
        canvas.drawBitmap(bmpImage[frameFlag/3%2], matrix, paint);
        frameFlag ++;
    }

    /**
     * 动画报时功能
     */
    private void actionTime(Canvas canvas, Paint paint){
        paint.setTextSize(40f * personSize);
        paint.setTextAlign(Align.CENTER);
        paint.setShadowLayer(2, 2, 2, Color.BLACK);
        if(drawTimeFlag-- > 0){
            if(touchX <= screenW/3){//左边绘制日期
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                canvas.drawText(dateFormat.format(new Date()), bmpH*personSize/2, (bmpH-2)*personSize, paint);
            }
            else if(touchX >= screenW*2/3){//绘制时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                canvas.drawText(dateFormat.format(new Date()), bmpH*personSize/2, (bmpH-2)*personSize, paint);
            }
        }else{
            drawTimeNow = false;
        }
        paint.setShadowLayer(0, 0, 0, 0);//移出阴影的设置
    }

    /*********************不同动作绘制*******************************************/
    /**
     * 设置动画数组，达到随机作用
     */
    private void setActionGroup(SharedPreferences sp){
        mActionList = DataByFile.getActionList(mPersonName);
        List<Object> removeData = new ArrayList<Object>();
        for(HashMap<String, Object> map : mActionList){
            String name = (String) map.get(ACTION_KEY_NAME);
            if(!sp.getBoolean(mPersonName + "_action_" + name, false)){
//				mActionList.remove(map);
                removeData.add(map);
            }
        }
        mActionList.removeAll(removeData);
    }

    /**
     * 动作变换事件(flag的变化)
     */
    private void onActionChange(int actionFlag){
        for(Bitmap b : bmpImage){
            if(b != null)
                b.recycle();
            b = null;
        }
        this.actionFlag = actionFlag;
        frameFlag = 0;//初始化
        upOrDown = (int) (Math.random()*3 - 1);
        switch (actionFlag){
            case FLAG_UP:
                mActionFun = FLAG_UP;
                bmpImage[0] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_up_1");
                bmpImage[1] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_up_2");
                bmpImage[2] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_up_3");
                break;
            case FLAG_DOWN:
                mActionFun = FLAG_DOWN;
                bmpImage[0] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_down_1");
                bmpImage[1] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_down_2");
                bmpImage[2] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_down_3");
                break;
            default:
            {
                HashMap<String, Object> map = mActionList.get(actionFlag);
                String name = (String) map.get(ACTION_KEY_NAME);
                for(int i = 0; i < 3; i++){
                    bmpImage[i] = DataByFile.getBitmapByPath(mPersonName + "/" + mPersonName + "_" + name + "_" + (i+1));
                }
                mActionFun = (Integer) map.get(ACTION_KEY_FUN);
            }
            break;
        }
    }

    /**
     * 资源错误导致报错，关闭提示并关闭服务
     */
    private void showErrorAndClose(Exception e){
        KLog2File.saveLog2File(e);
        e.printStackTrace();
        Toast.makeText(getContext(), R.string.str_app_res_error, Toast.LENGTH_LONG).show();
        SharedPreferences sp = getContext().getSharedPreferences(getContext().getString(R.string.sp_name), Activity.MODE_PRIVATE);
        sp.edit().putBoolean("person_visible", false).commit();
    }
}
