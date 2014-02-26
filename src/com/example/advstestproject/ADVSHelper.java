package com.example.advstestproject;

import jp.app_mart.service.AppmartADVSInterface;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.advstestproject.MainActivity.ResultServiceInterface;


public class ADVSHelper {

	//debug状態
	private boolean isDebug = true;	
	//context
	public Context mContext ;
	// appmartパケージ名
	public static final String APP_PACKAGE = "jp.app_mart";
	//Appmartサービスパス
	public static final String APP_PATH = "jp.app_mart.service.AppmartADVSService";
	//appmartサービス
	protected AppmartADVSInterface service;
	//callback
	public ResultServiceInterface callback;

	/* CONSTRUCTOR */
	public ADVSHelper(Context context, ResultServiceInterface callback){
		this.mContext=context;
		this.callback=callback;
	}

	public void verifyInstallSource(){

		// appmartサービスに接続するためのIntentオブジェクトを生成
		Intent i = new Intent();
		i.setClassName(APP_PACKAGE, APP_PATH);

		if (mContext.getPackageManager().queryIntentServices(i, 0).isEmpty()) {
			debugMess("appmartがインストールされてないようです。");
			callback.isValid(false);
			return;
		}

		//サービス接続
        ServiceConnection mConnection = new ServiceConnection() {
            //接続時実行
            public void onServiceConnected(ComponentName name, IBinder boundService) {
                service = AppmartADVSInterface.Stub.asInterface((IBinder) boundService);
                debugMess("Appmartに接続しました。");
                verifyAppliIntegrity();
            }
            //切断時実行
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }            
        };

		// bindServiceを利用し、サービスに接続
		try {			
			mContext.bindService(i, mConnection, Context.BIND_AUTO_CREATE);			
		} catch (Exception e) {
			e.printStackTrace();
			debugMess("Appmartとの接続は失敗しました。");
			callback.isValid(false);
			return;
		}

	}

	/* アプリ提供元確認 */
	protected void verifyAppliIntegrity(){	
		try {
			if(service.verify(mContext.getPackageName()) == 1 ){
				debugMess("appmartからインストールされました。");
				callback.isValid(true);
				return;
			}else{				
				debugMess("Appmartでダウンロードされておりません。");
				callback.isValid(false);
				return;
			}
		} catch (RemoteException e) {
			callback.isValid(false);
		}
	}

	/* debug用 */
	private void debugMess(String mess) {
		if (isDebug) {
			Log.d("DEBUG", mess);
		}
	}

}