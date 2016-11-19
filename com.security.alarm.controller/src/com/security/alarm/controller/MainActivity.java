package com.security.alarm.controller;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends TabActivity {

	private final String LOG_TAG="security.alarm.controller - MainActivity";
	
	/** Occurs on current activity loading */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.setTitle(R.string.title_activity_main);
		
		try{
			TabHost tabHost = getTabHost();
	        TabHost.TabSpec tabSpec;
	        
	        /** Main tab */
	        tabSpec = tabHost.newTabSpec("Main");
	        tabSpec.setIndicator("Основное", getResources().getDrawable(R.drawable.house));
	        Intent mainTabInt=new Intent(this, MainTabActivity.class);
	        tabSpec.setContent(mainTabInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Settings tab */
	        tabSpec = tabHost.newTabSpec("Settings");
	        tabSpec.setIndicator("Настройки", getResources().getDrawable(R.drawable.wrench_orange));
	        Intent settingsTabInt=new Intent(this, SettingsTabActivity.class);
	        tabSpec.setContent(settingsTabInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Phone tab */
	        tabSpec = tabHost.newTabSpec("Phone");
	        tabSpec.setIndicator("Телефон", getResources().getDrawable(R.drawable.telephone));
	        Intent phoneTabInt=new Intent(this, PhoneTabActivity.class);
	        tabSpec.setContent(phoneTabInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Timer tab */
	        tabSpec = tabHost.newTabSpec("Timer");
	        tabSpec.setIndicator("Таймер", getResources().getDrawable(R.drawable.clock));
	        Intent timerTabInt=new Intent(this, TimerTabActivity.class);
	        tabSpec.setContent(timerTabInt);
	        tabHost.addTab(tabSpec);
	        
	        /** Siren tab */
	        tabSpec = tabHost.newTabSpec("Siren");
	        tabSpec.setIndicator("Сирена", getResources().getDrawable(R.drawable.funnel));
	        Intent sirenTabInt=new Intent(this, SirenTabActivity.class);
	        tabSpec.setContent(sirenTabInt);
	        tabHost.addTab(tabSpec);
	        
	        DbWorkerCls.getDbWorkerAsSingleton(this).checkRecordExistance("SETTINGS_TBL", "", new String[]{});
	    }
	    catch(Exception ex){
			String strErr="Ошибка при загрузке - "+ex.getMessage();
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,strErr);
			return;
		}
	}

	/** Occurs on setting menu creation */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/** Occurs on option menu item clicking*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()){
				case R.id.mnuSaveSettings:{
					XmlDbWorkerCls xmlDbInst=new XmlDbWorkerCls(this);
					xmlDbInst.writeXmlData();
				}
				break;
				
				case R.id.mnuLoadSettings:{
					XmlDbWorkerCls xmlDbInst=new XmlDbWorkerCls(this);
					xmlDbInst.addDataFromXml();
				}
				break;
			
				case R.id.mnuAbout:{
					AboutFragment dlgAbout=new AboutFragment();
					dlgAbout.show(this.getFragmentManager(), "О проге...");
				}
				break;
				
				case R.id.mnuExit:
					this.finish();
				break;
			}
		}
		catch(Exception ex){
			String strErr="Ошибка onOptionItemSelected - "+ex.getMessage();
	    	Log.d(LOG_TAG,strErr);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
