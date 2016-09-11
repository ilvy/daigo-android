package com.jxf.daigo.customplugins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by gogo on 2016/8/29.
 */
public class ScreenShot extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("shot")) {
            String message = args.getString(0);
            this.shot(message, callbackContext);
            return true;
        }
        return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private void shot(String message, CallbackContext callbackContext){
        try {
            this.savePic(takeScreenShotBmp());
            callbackContext.success(1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            callbackContext.error("FileNotFoundException");
        }catch (IOException e){
            e.printStackTrace();
            callbackContext.error("IOException");
        }
    }
    private Bitmap takeScreenShotBmp() {
        // View是你需要截图的View
        Activity activity = this.cordova.getActivity();
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
//        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//        int height = activity.getWindowManager().getDefaultDisplay()
//                .getHeight();
        int width = view.getWidth();
        int height = view.getHeight();
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
    private Bitmap captureWebView(WebView webView){
        Activity activity = this.cordova.getActivity();
//        WebView webView = activity.getWindow().getDecorView();
        Picture snapShot = webView.capturePicture();

        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }
    private void savePic(Bitmap b) throws FileNotFoundException,IOException{
        FileOutputStream fos = null;
        String savePath = this.getSDCardPath()+"/daigo/images";
        System.out.println(savePath);
        long timestamp = new Date().getTime();
        String filePath = savePath+"/"+timestamp+".jpg";
            File path = new File(savePath);
            File file = new File(filePath);
            if(!path.exists()){
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(cordova.getActivity().getApplicationContext(), "截屏文件已保存至"+savePath+"下", Toast.LENGTH_LONG).show();
            }
    }
    /**
     * 获取SDCard的目录路径功能
     * @return
     */
    private String getSDCardPath(){
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
}