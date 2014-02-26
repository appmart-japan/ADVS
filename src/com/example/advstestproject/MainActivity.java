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
				if (!result){
					Toast.makeText(getApplicationContext(), "appmartからインストールされたアプリではありません",Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "appmartからインストールされました",Toast.LENGTH_LONG).show();
				}
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
