package cc.alonebo.lanc.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.alonebo.lanc.MyApplication;
import cc.alonebo.lanc.Constants;


/**
 * Created by aloneBo on 17-3-21.
 */

public class Utils {
    public static Context getContext() {
        return MyApplication.getContext();
    }


    public static long getCurrentTime() {
       return new Date().getTime();
    }



    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }


    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            r.run();
        } else {
            getHandler().post(r);
        }
    }

    public static boolean isRunOnUIThread() {
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }

        return false;
    }

    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
    public static String getExtSDCardDownloadPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    public static long getInnerSDCardUsableSpec() {
        File file = new File(getInnerSDCardPath());
        return file.getUsableSpace();
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    public static boolean isShowing = false;
    public static void showToast(final Context context, final String msg) {
        if (isShowing) return;
        isShowing = true;
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
            }
        });
        isShowing = false;
    }

    public static String getFileName(String name) {
        int index = name.lastIndexOf("/");
        return name.substring(index+1);
    }


    public static String getAppCacheDir() {
        return getContext().getCacheDir().getAbsolutePath();
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static int getVMSize() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024/1024);
        return maxMemory;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static Bitmap decodeSampledBitmapFromFile(String picPath,
                                                     int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picPath, options);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     mdpi density=1
     hdpi density=1.5
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  //1.5
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     *
     * @param ident
     * @return
     */
    public static String getAvatorPath(String ident, long time) {
        return Utils.getContext().getFilesDir().getAbsolutePath()+"/"+ident+"_"+time+".png";
    }

    public static String getFilsDir() {
        return Utils.getContext().getFilesDir().getAbsolutePath()+"/";
    }


    public static String getMyAvatarAbsPath() {
        return Utils.getContext().getFilesDir().getAbsolutePath()+"/"+Constants.NAME_AVATAR;
    }
    public static String getAppFilesPath() {
        return Utils.getContext().getFilesDir().getAbsolutePath();
    }


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String getDeviceIdent() {
        return (String) SPUtils.get(getContext(), Constants.SP_DEVICE_ID, Build.SERIAL);
    }

    public static long getMyAvatarTime() {
        long result = (long) SPUtils.get(getContext(), Constants.SP_AVATAR_TIME, 0l);
        return result;
    }

    public static String getCurrentTime(long time) {
		/*
		 * 1秒=1000毫秒
			1分=60秒
			1小时=60分
			1天=24小时
			1天=24*60*60*1000=86400000毫秒
		 * */
//        long second = 1000;
        long minute = 60000;
        long hour = minute * 60;
        long day = hour * 24;
        long currentTime = System.currentTimeMillis();
        String result = "";
        if((currentTime - time) < day) {//小于一天 显示 22:15
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            result = dateFormat.format(new Date(time));
        } else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd"); //显示日期
            result = dateFormat.format(new Date(time));
        }
        return result;
    }

    public static String getString(int resId) {
        return getContext().getString(resId);
    }
    public static long getVersionCode() {
        PackageManager manager = getContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        String version = info.versionName;
        return info.versionCode;
    }


    public static void copy2clip(String content) {
        ClipboardManager cmb = (ClipboardManager) Utils.getContext() .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }

}
