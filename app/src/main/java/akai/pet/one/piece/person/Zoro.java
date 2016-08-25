package akai.pet.one.piece.person;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import akai.floatView.op.luffy.R;

public class Zoro extends Person{
    /**图片,动作未超过3个*/
    private Bitmap[] bmpZoro = new Bitmap[3];
    /**标识常量*/
    private final int FLAG_STAND = 0, FLAG_SLEEP = 1,
            FLAG_DOWN = 2, FLAG_EAT = 3, FLAG_SIT = 4,
            FLAG_UP = 5, FLAG_WALK = 6, FLAG_WALK2 = 7;
    /**上下行走的步数*/
    private final int UP_DOWN_STEPS = 20;

    public Zoro(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        setFocusable(true);
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Context.MODE_PRIVATE);
        res = context.getResources();
        init(sp);
        randomChange();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas, paint);
    }
    /***************************继承start***********************************/
    /**
     * 初始化数据处理
     */
    @Override
    public void init(SharedPreferences sp){
//一般数据
        //获取界面大小
        measureScreen();
        //人物大小（注意，可能手机上面获得的显示大小跟实际上的不一样）
        bmpW = decodeResource(res, R.drawable.zoro_eat_1).getWidth();
        bmpH = decodeResource(res, R.drawable.zoro_eat_1).getHeight();
        personSize = Float.parseFloat(sp.getString("person_size", "1"));
        drawTime = sp.getBoolean("draw_time", false);
        //长按设置功能
        onPerson = sp.getBoolean("long_down", false)?0:-1;
//存储数据
        setActionGroup(sp);

//初始化的xy
        x = screenW/2 - bmpW/2;
        y = screenH/2 - bmpH/2;
    }

    /**
     * 随机动画产生
     */
    @Override
    public void randomChange(){
        if(actionFlag != FLAG_UP){
            int count = actionGroup.size();
            int i = (int)( Math.random()*count );
            onActionChange(actionGroup.get(i));
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
        if(key.indexOf("zoro_action_") != -1){
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
        float disW = (1-personSize)*bmpW/2;
        float disH = (1-personSize)*bmpH/2;
        matrix = new Matrix();
        matrix.postScale(personSize*leftOrRight, personSize, bmpW/2, bmpH/2);
        matrix.postTranslate(-disW, -disH);
        switch (actionFlag){
            case FLAG_SLEEP:
                actionSleep(canvas, paint);
                break;
            case FLAG_DOWN:
                actionDown(canvas, paint);
                break;
            case FLAG_EAT:
                actionEat(canvas, paint);
                break;
            case FLAG_SIT:
                actionSit(canvas, paint);
                break;
            case FLAG_STAND:
                actionStand(canvas, paint);
                break;
            case FLAG_UP:
                actionUp(canvas, paint);
                break;
            case FLAG_WALK:
                actionWalk(canvas, paint);
                break;
            case FLAG_WALK2:
                actionWalk2(canvas, paint);
                break;
        }
        if(drawTimeNow)
            actionTime(canvas, paint);
    }
    /*********************动作,action*******************************************/
    /**
     * 动作：睡觉,sleep
     */
    private void actionSleep(Canvas canvas, Paint paint){
        if(frameFlag/4%2 == 1)
            canvasDraw(canvas, bmpZoro[1], matrix, paint);
        else if(frameFlag/8%2 == 0)
            canvasDraw(canvas, bmpZoro[0], matrix, paint);
        else if(frameFlag/8%2 == 1)
            canvasDraw(canvas, bmpZoro[2], matrix, paint);
        frameFlag ++;
    }
    /**
     * 动作：下降,down
     */
    private void actionDown(Canvas canvas, Paint paint){
        if(frameFlag <= 3){
            canvasDraw(canvas, bmpZoro[0], matrix, paint);
            int disY = (int) (screenH - bmpH/2 - bmpH*personSize/2);
            if(y < disY)
                y += speed * 3;
        }else if(frameFlag <= 5){
            int disY = (int) (screenH - bmpH/2 - bmpH*personSize/2);
            if(y < disY)
                y += speed * 3;
            canvasDraw(canvas, bmpZoro[1], matrix, paint);
        }else if(frameFlag <= 6){
            canvasDraw(canvas, bmpZoro[2], matrix, paint);
        }
        else{
            int disY = (int) (screenH - bmpH/2 - bmpH*personSize/2);
            if(y > disY)
                y = disY;
            canvasDraw(canvas, bmpZoro[2], matrix, paint);
            randomChange();
        }
        frameFlag ++;
    }
    /**
     * 动作：吃肉,eat
     */
    private void actionEat(Canvas canvas, Paint paint){
        canvasDraw(canvas, bmpZoro[frameFlag%3], matrix, paint);
        frameFlag ++;
    }
    /**
     * 动作：坐着,sit
     */
    private void actionSit(Canvas canvas, Paint paint){
        canvasDraw(canvas, bmpZoro[0], matrix, paint);
    }
    /**
     * 动作：站立,stand
     */
    private void actionStand(Canvas canvas, Paint paint){
        canvasDraw(canvas, bmpZoro[0], matrix, paint);
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
        canvasDraw(canvas, bmpZoro[frameFlag*leftOrRight], matrix, paint);
        //记录前一个点
        touchPreX = touchX;
    }
    /**
     * 动作：行走,walk
     */
    private void actionWalk(Canvas canvas, Paint paint){
        if(x <= 0 && leftOrRight == 1
                ||  x+bmpW*personSize >= screenW && leftOrRight == -1)//碰壁换向
            leftOrRight *= -1;
        x -= speed*leftOrRight;//移动
        //采用了循环方法，walk1 -> walk2 -> walk3 -> walk2
        if(frameFlag%2 == 1)
            canvasDraw(canvas, bmpZoro[1], matrix, paint);
        else if(frameFlag/2%2 == 0)
            canvasDraw(canvas, bmpZoro[0], matrix, paint);
        else
            canvasDraw(canvas, bmpZoro[2], matrix, paint);

        frameFlag ++;
    }
    /**
     * 动作：行走,walk2,具有上下行走
     */
    private void actionWalk2(Canvas canvas, Paint paint){
        if(y <= 0 && upOrDown == 1
                ||  y+bmpH*personSize >= screenH && upOrDown == -1)//碰壁换向
            upOrDown *= -1;
        y -= speed*upOrDown;//移动
        //水平行走
        actionWalk(canvas, paint);
        if(frameFlag >= UP_DOWN_STEPS)
            randomChange();
    }
    /**
     * 动画报时功能
     */
    private void actionTime(Canvas canvas, Paint paint){
        paint.setTextSize(30f * personSize);
        paint.setTextAlign(Align.CENTER);
        paint.setShadowLayer(2, 2, 2, Color.BLACK);
        if(drawTimeFlag-- > 0){
            if(touchX <= screenW/3){//左边绘制日期
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                canvas.drawText(dateFormat.format(new Date()), bmpW*personSize/2, (bmpH-2)*personSize, paint);
            }
            else if(touchX >= screenW*2/3){//绘制时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                canvas.drawText(dateFormat.format(new Date()), bmpW*personSize/2, (bmpH-2)*personSize, paint);
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
        actionGroup = new ArrayList<Integer>();
        if(sp.getBoolean("zoro_action_sleep", false))
            actionGroup.add(FLAG_SLEEP);
        if(sp.getBoolean("zoro_action_eat", false))
            actionGroup.add(FLAG_EAT);
        if(sp.getBoolean("zoro_action_sit", false))
            actionGroup.add(FLAG_SIT);
        if(sp.getBoolean("zoro_action_stand", false))
            actionGroup.add(FLAG_STAND);
        if(sp.getBoolean("zoro_action_walk", false)){
            actionGroup.add(FLAG_WALK);
            actionGroup.add(FLAG_WALK2);
        }
        if(actionGroup.size() == 0){
            actionGroup.add(FLAG_WALK);
        }
        randomChange();
    }

    /**
     * 动作变换事件(flag的变化)
     */
    private void onActionChange(int actionFlag){
        this.actionFlag = actionFlag;
        frameFlag = 0;//初始化
        switch (actionFlag){
            case FLAG_DOWN:
                if(x < 0) x = 0;
                if(x > screenW-bmpW*personSize) x = screenW-bmpW*personSize;
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_down_2);
                bmpZoro[1] = decodeResource(res, R.drawable.zoro_down_3);
                bmpZoro[2] = decodeResource(res, R.drawable.zoro_stand);
                break;
            case FLAG_EAT:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_eat_1);
                bmpZoro[1] = decodeResource(res, R.drawable.zoro_eat_2);
                bmpZoro[2] = decodeResource(res, R.drawable.zoro_eat_3);
                break;
            case FLAG_SIT:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_sit);
                break;
            case FLAG_STAND:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_stand);
                break;
            case FLAG_UP:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_stand);
                bmpZoro[1] = decodeResource(res, R.drawable.zoro_up_1);
                bmpZoro[2] = decodeResource(res, R.drawable.zoro_up_2);
                break;
            case FLAG_WALK2:
                upOrDown = Math.random()< 0.5 ? 1:-1;
            case FLAG_WALK:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_walk_1);
                bmpZoro[1] = decodeResource(res, R.drawable.zoro_walk_2);
                bmpZoro[2] = decodeResource(res, R.drawable.zoro_walk_3);
                break;
            case FLAG_SLEEP:
                bmpZoro[0] = decodeResource(res, R.drawable.zoro_sleep_1);
                bmpZoro[1] = decodeResource(res, R.drawable.zoro_sleep_2);
                bmpZoro[2] = decodeResource(res, R.drawable.zoro_sleep_3);
                break;
        }
    }
}
