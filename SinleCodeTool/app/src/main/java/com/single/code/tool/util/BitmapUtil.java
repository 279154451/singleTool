package com.single.code.tool.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * bitmap 、缩略图相关
 * Created by Administrator on 2017/11/6.
 */
public class BitmapUtil {


    /**
     * drawable转换成bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Log.d("token", "icon w=" + w + " ,h=" + h);
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     *缩放图片获得bitmap
     * @param sourceFile
     * @param width 最终缩放的宽度
     * @param higth 最终缩放的高度
     * @return
     */
    public static Bitmap ScaleBitmap(String sourceFile,int width,int higth) throws Exception{
        File file = new File(sourceFile);
        Bitmap scaleBimap = null;
        if(file.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds =true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            options.inJustDecodeBounds =false;
            int w= options.outWidth;
            int h= options.outHeight;
            int beWidth = w / width;
            int beHeight = h / higth;
            int be = 1;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if(be<=0){
                be =1;
            }
            options.inSampleSize =be;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            if(bitmap!=null&&(bitmap.getWidth()!=0)&&(bitmap.getWidth()!=0)){
                scaleBimap = Bitmap.createScaledBitmap(bitmap,width,higth,true);
            }
        }else {
            throw new Resources.NotFoundException("NotFound the image file");
        }
        return scaleBimap;
    }

    /**
     * 保存bitmap到文件
     * @param bitmap
     * @param newPath  生成文件的地址
     */
    public  static boolean newFileByBitmap(Bitmap bitmap,String newPath){
        boolean ret = false;
        try {
            File bitmapFile = new File(newPath);
            if(!bitmapFile.exists()){
                bitmapFile.createNewFile();
            }
            String fileName = bitmapFile.getName();
            Bitmap.CompressFormat e;
            if(!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")) {
                if(!fileName.endsWith(".png")) {
                    return ret;
                }

                e = Bitmap.CompressFormat.PNG;
            } else {
                e = Bitmap.CompressFormat.JPEG;
            }

            FileOutputStream fos = new FileOutputStream(bitmapFile);
            ret =bitmap.compress(e,100,fos);
            fos.close();;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return ret;
    }


    /**
     * 根据图片路径生成压缩后的Bitmap
     * @param context
     * @param filePath
     * @return
     */
    public static Bitmap getScaleBitmapByFilePath(Context context, String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

        int bmpWidth = opt.outWidth;
        int bmpHeght = opt.outHeight;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        opt.inSampleSize = 1;
        if (bmpWidth > bmpHeght) {
            if (bmpWidth > screenWidth)
                opt.inSampleSize = bmpWidth / screenWidth;
        } else {
            if (bmpHeght > screenHeight)
                opt.inSampleSize = bmpHeght / screenHeight;
        }
        opt.inJustDecodeBounds = false;

        bmp = BitmapFactory.decodeFile(filePath, opt);
        return bmp;
    }

    /* * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
             *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
             *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
             *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
             * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

}