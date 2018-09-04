package com.mohang.instadownload.instadownloader;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ClipBoardService extends Service {

    public static final String TAG= "ClipboardManager";
    public ClipBoardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    ClipboardManager.OnPrimaryClipChangedListener onPrimaryClipChangedListener=new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {

            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence pasteData = "";
            ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText();
            if(pasteData!=null && pasteData.toString().contains("https://www.instagram.com/p/")){

                Intent intent=new Intent(ClipBoardService.this, DetailActivity.class);

                String[] arr=pasteData.toString().split("\\?");
                String endpoint=arr[0].replace("https://www.instagram.com/p/","").replace("/","");
                Log.d(TAG, "onPrimaryClipChanged: "+endpoint);
                intent.putExtra(DetailActivity.SHORT_CODE,endpoint);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(onPrimaryClipChangedListener);





        return START_STICKY;
    }
}
