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
	
	//debug���
	private boolean isDebug = true;	
	//context
	public Context mContext ;
	// appmart�p�P�[�W��
	public static final String APP_PACKAGE = "jp.app_mart";
	//Appmart�T�[�r�X�p�X
	public static final String APP_PATH = "jp.app_mart.service.AppmartADVSService";
	//appmart�T�[�r�X
	protected AppmartADVSInterface service;
	//callback
	ResultServiceInterface callback;
	
	/* CONSTRUCTOR */
	public ADVSHelper(Context context, ResultServiceInterface callback){
		this.mContext=context;
		this.callback=callback;
	}

	
	public void verifyInstallSource(){
		
		// appmart�T�[�r�X�ɐڑ����邽�߂�Intent�I�u�W�F�N�g�𐶐�
		Intent i = new Intent();
		i.setClassName(APP_PACKAGE, APP_PATH);
		
		if (mContext.getPackageManager().queryIntentServices(i, 0).isEmpty()) {
			debugMess("appmart���C���X�g�[������ĂȂ��悤�ł��B");
			callback.isValid(false);
			return;
		}
						
		//�T�[�r�X�ڑ�
        ServiceConnection mConnection = new ServiceConnection() {
            //�ڑ������s
            public void onServiceConnected(ComponentName name, IBinder boundService) {
                service = AppmartADVSInterface.Stub.asInterface((IBinder) boundService);
                debugMess("Appmart�ɐڑ����܂����B");
                verifyAppliIntegrity();
            }
            //�ؒf�����s
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }            
        };

		// bindService�𗘗p���A�T�[�r�X�ɐڑ�
		try {			
			mContext.bindService(i, mConnection, Context.BIND_AUTO_CREATE);			
		} catch (Exception e) {
			e.printStackTrace();
			debugMess("Appmart�Ƃ̐ڑ��͎��s���܂����B");
			callback.isValid(false);
			return;
		}

	}
	
	
	protected void verifyAppliIntegrity(){	
		try {
			if(service.verify(mContext.getPackageName()) == 1 ){
				debugMess("appmart����C���X�g�[������܂����B");
				callback.isValid(true);
				return;
			}else{				
				debugMess("Appmart�Ń_�E�����[�h����Ă���܂���B");
				callback.isValid(false);
				return;
			}
		} catch (RemoteException e) {
			callback.isValid(false);
		}
	}
	
	
	/* debug�p */
	private void debugMess(String mess) {
		if (isDebug) {
			Log.d("DEBUG", mess);
			Toast.makeText(mContext, mess, Toast.LENGTH_SHORT).show();
		}
	}


}
