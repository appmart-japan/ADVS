ADVS
====

Appmart Download Verification Service : アプリコピー防止機能API仕様書


---


## 概要

ADVSはapkファイルのコピー防止機能APIです。
ADVSを実装していただくと不正にapkファイルがコピーされて他の端末で実行されても起動しない仕組みとなっております。

--

## 実装

AIDLファイルを実装していただいて、アプリ起動時に呼ばれているactivityにコード数行を追加していただくだけで
apkファイルのコピーを防止することができます。

#### manifestファイルを更新

>  必要なパーミッションを追加

```xml
 <uses-permission android:name="jp.app_mart.permissions.APPMART_ADVS" />
```

>  aidlファイル追加 ( jp/app_mart/service/AppmartADVSInterface.aidl )

```java
package jp.app_mart.service;

import android.os.Bundle;

interface AppmartADVSInterface {    

    //提供元を確認
    int verify(String packageName );
}
```
必ず同じパッケージ名・インタフェース名にしてください。

#### ヘルパークラスを追加

>  ヘルパークラスが用意されておりますので、そのままご利用ください。

```java
package com.example.advstestproject;

import com.example.advstestproject.MainActivity.ResultServiceInterface;

import jp.app_mart.service.AppmartADVSInterface;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class ADVSHelper {
	
	// debug状態
	private boolean isDebug = true;	
	// context
	public Context mContext ;
	// Appmartパケージ名
	public static final String APP_PACKAGE = "jp.app_mart";
	// Appmartサービスパス
	public static final String APP_PATH = "jp.app_mart.service.AppmartADVSService";
	// Appmartサービス
	protected AppmartADVSInterface service;
	// callback
	ResultServiceInterface callback;
	
	// CONSTRUCTOR 
	public ADVSHelper(Context context, ResultServiceInterface callback){
		this.mContext=context;
		this.callback=callback;
	}
	
	public void verifyInstallSource(){
		
		// Appmartサービスに接続するためのIntentオブジェクトを生成
		Intent i = new Intent();
		i.setClassName(APP_PACKAGE, APP_PATH);
		
		if (mContext.getPackageManager().queryIntentServices(i, 0).isEmpty()) {
			debugMess("Appmartがインストールされてないようです。");
			callback.isValid(false);
			return;
		}
						
		// サービス接続
        ServiceConnection mConnection = new ServiceConnection() {
            // 接続時実行
            public void onServiceConnected(ComponentName name, IBinder boundService) {
                service = AppmartADVSInterface.Stub.asInterface((IBinder) boundService);
                debugMess("Appmartに接続しました。");
                verifyAppliIntegrity();
            }
            // 切断時実行
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
	
	// インストール元を確認 
	protected void verifyAppliIntegrity(){
		try {
			if(service.verify(mContext.getPackageName()) == 1 ){
				debugMess("Appmartからインストールされました。");
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
	
	// debug用
	private void debugMess(String mess) {
		if (isDebug) {
			Log.d("DEBUG", mess);
			Toast.makeText(mContext, mess, Toast.LENGTH_SHORT).show();
		}
	}


}

```

#### activityを更新

>  launcherとなるactivityにコードを追加

```java
package com.example.advstestproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*
         * @ADVSHelper クラスファイルを追加して、
         * {@link ADVSHelper#verifyInstallSource() } メッソードを呼び出してください 
         */
        ADVSHelper helper = new ADVSHelper(this.getApplicationContext(), new ResultServiceInterface() {			
			@Override
			public void isValid(boolean result) {				
				if (!result)
			Toast.makeText(getApplicationContext(),"Appmartからインストールされたアプリではありません",Toast.LENGTH_LONG).show();
				finish();
			}
		});        
       helper.verifyInstallSource();
           
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
        
    public interface ResultServiceInterface{
    	public void isValid(boolean result);
    }
    
}
```

 * ヘルパークラスをインスタンス化します
 * verifyInstallSourceメッソドを呼び出します


>  Helper constructorのパラメータ

| パラメータ名     | 説明           |
| ------------- |:-------------:| ------: |
| Context   |  activityのcontext    |
| ResultServiceInterface  |  callbackクラス    |
