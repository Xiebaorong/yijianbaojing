package com.rndchina.demo.util;

/**
 * Created by Administrator on 2016/7/6.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

public class ImageUtil {
    /**
     *
     * @param src 原图片
     * @param watermark  要打的水印图片
     * @return Bitmap 打好水印的图片
     */
    private Bitmap createBitmap(Bitmap src,Bitmap watermark){
        if(src == null){
            return null;
        }
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int waterWidth = watermark.getWidth();
        int waterHeight = watermark.getHeight();
        //create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(srcWidth,srcHeight,Config.ARGB_8888);//创建一个新的和src长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0,null);//在0,0坐标开始画入src
	  /*Paint paint = new Paint();
	  paint.setColor(Color.RED);*/
        if(watermark != null){
            cv.drawBitmap(watermark, srcWidth-waterWidth,srcHeight-waterHeight, null);//在src的右下解画入水印图片
            //cv.drawText("HELLO",srcWidth-waterWidth,srcHeight-waterHeight, paint);//这是画入水印文字，在画文字时，需要指定paint
        }
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.restore();//存储
        return newb;
    }

    // 从资源中获取Bitmap
    public static Bitmap getBitmapFromResources(Context context, int resId) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                        : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap convertDrawable2BitmapSimple(Drawable drawable){
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        return bd;
    }

    public static Bitmap drawImageDropShadow(Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(1,
                BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(150);
        shadowPaint.setColor(Color.RED);
        shadowPaint.setMaskFilter(blurFilter);
        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowImage32 = shadowBitmap.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, offsetXY[0], offsetXY[1], null);
        return shadowImage32;
    }

    private Bitmap createBitMap(Bitmap src,Bitmap wmsrc){
        /**
         * 水印制作方法
         */
        String tag="xx";
        if(src==null){
            return null;
        }
        int w=src.getWidth();
        int h=src.getHeight();
        int wmw=wmsrc.getWidth();
        int wmh=wmsrc.getHeight();
        //create the new bitmap
        Bitmap newb=Bitmap.createBitmap(w,h,Config.ARGB_8888);//创建一个底图
        Canvas cv=new Canvas(newb);
        //将底图画进去
        cv.drawBitmap(src, 0, 0,null);//在0,0坐标开始画入src
        //讲水印画进去
        cv.drawBitmap(wmsrc, w-wmw+5, h-wmh+5, null);
        //保存图片
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newb;

    }

    /**
     * 加水印 也可以加文字
     * @param src
     * @param watermark
     * @param title
     * @return
     */
    public static Bitmap watermarkBitmap(Bitmap src, Drawable watermark,
                                         String title) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        //需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
        Bitmap newb= Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint paint=new Paint();
        //加入图片
        if (watermark != null) {
            watermark.setBounds(0, 0, w, h/4);
            watermark.setAlpha(0xaf);
            Bitmap bmp =convertDrawable2BitmapSimple(watermark);
//	            paint.setAlpha(80);
            cv.drawBitmap(bmp, 0 , h-h/4 , paint);// 在src的右下角画入水印
//	            cv.drawBitmap(watermark, 0, 0, paint);// 在src的左上角画入水印
        }else{
            System.out.println( "water mark failed");
        }
        //加入文字
        if(title!=null)
        {
            String familyName ="宋体";
            Typeface font = Typeface.create(familyName,Typeface.NORMAL);
            TextPaint textPaint=new TextPaint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(font);
            textPaint.setTextSize(22);
            //这里是自动换行的
//	            StaticLayout layout = new StaticLayout(title,textPaint,w,Alignment.ALIGN_OPPOSITE,1.0F,0.0F,true);
//	            layout.draw(cv);
            //文字就加左上角算了
            cv.drawText(title,10,h-5,textPaint);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
    }


}
