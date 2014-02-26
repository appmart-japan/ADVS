package com.example.advstestproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	//TAG
	private final String TAG = this.getClass().getSimpleName();
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*
         * @ADVSHelper �N���X�t�@�C����ǉ����āA
         * {@link ADVSHelper#verifyInstallSource() } ���b�\�[�h���Ăяo���Ă������� 
         */
        ADVSHelper helper = new ADVSHelper(this.getApplicationContext(), new ResultServiceInterface() {			
			@Override
			public void isValid(boolean result) {				
				if (!result)
				Toast.makeText(getApplicationContext(), "appmart����C���X�g�[�����ꂽ�A�v���ł͂���܂���",Toast.LENGTH_LONG).show();
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
