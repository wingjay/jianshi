package com.wingjay.jianshi.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.wingjay.jianshi.ui.theme.BackgroundColorHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ASUS on 2016/9/4.
 */
public class ScreenshotHelper {
  private static final int THUMB_SIZE = 150;

  public static void screenshotShare(Context context, View view, IWXAPI api, boolean shareToCircle) throws Exception{
    View temp = view;
    temp.setBackgroundResource(BackgroundColorHelper.getBackgroundColorResFromPrefs(context));
    Bitmap jianshiBitmap = screenshot(temp);
    WXImageObject imgObj = new WXImageObject(jianshiBitmap);
    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = imgObj;

    Bitmap thumbBmp = Bitmap.createScaledBitmap(jianshiBitmap, THUMB_SIZE, THUMB_SIZE, true);
    jianshiBitmap.recycle();
    msg.thumbData = bmpToByteArray(thumbBmp, true);

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = buildTransaction("img");
    req.message = msg;
    req.scene = shareToCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
    api.sendReq(req);

    temp.setBackgroundResource(0);
    temp = null;
  }

  private static void screenshotAndSave(View view,String path) throws Exception{
    Bitmap bitmap = screenshot(view);
    saveBitmap(bitmap,path);
  }

  private static void saveBitmap(Bitmap bitmap,String path) throws Exception{
    FileOutputStream os = null;
    try{
      os = new FileOutputStream(path);
      if(os != null){
        bitmap.compress(Bitmap.CompressFormat.PNG,100,os);
        os.flush();
        os.close();
      }
    }catch(IOException e){
      e.printStackTrace();
      throw e;
    }
  }

  private static Bitmap screenshot(View view)throws Exception{
    view.clearFocus();
    view.setPressed(true);
    boolean willNotCache = view.willNotCacheDrawing();
    view.setWillNotCacheDrawing(false);
    int color = view.getDrawingCacheBackgroundColor();
    view.setDrawingCacheBackgroundColor(0);
    if(color != 0){
      view.destroyDrawingCache();
    }
    view.buildDrawingCache();
    Bitmap cacheBitmap = view.getDrawingCache();
    if(cacheBitmap == null){
      throw new Exception("Bitmap can not be screenshoted");
    }
    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
    view.destroyDrawingCache();
    view.setWillNotCacheDrawing(willNotCache);
    view.setDrawingCacheBackgroundColor(color);
    return bitmap;
  }

  private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
    if (needRecycle) {
      bmp.recycle();
    }

    byte[] result = output.toByteArray();
    try {
      output.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  private static String buildTransaction(final String type) {
    return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
  }
}
