package com.security.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SirenTabActivity extends Activity {

	private SeekBar sbrSirenTime;
	private SeekBar sbrSirenVolume;
	private Switch swcSirenCaution;
	private TextView lblSirenTimeVal;
	private TextView lblSirenVolumeVal;
	
	private final String LOG_TAG="security.alarm.controller - SirenTabActivity";
	private DbWorkerCls m_DbWrkInst;
	
	/** Occurs on current activity creation */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_siren_tab);
		
		try{
			sbrSirenTime=(SeekBar)findViewById(R.id.sbrSirenTime);
			sbrSirenVolume=(SeekBar)findViewById(R.id.sbrSirenVolume);
			swcSirenCaution=(Switch)findViewById(R.id.swcSirenCaution);
			lblSirenTimeVal=(TextView)findViewById(R.id.lblSirenTimeVal);
			lblSirenVolumeVal=(TextView)findViewById(R.id.lblSirenVolumeVal);

			sbrSirenTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					lblSirenTimeVal.setText(""+progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
			
			sbrSirenVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					lblSirenVolumeVal.setText(""+progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
		}
		catch(Exception ex){
			String strErr="Сбой инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Occurs on activity starting */
	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			m_DbWrkInst = DbWorkerCls.getDbWorkerAsSingleton(this);
			Cursor sirenCursor=m_DbWrkInst.m_CurrentDb.rawQuery("select hello_siren, siren_time, siren_volume from SIREN_TBL",new String[]{});
			
			if(sirenCursor==null||!sirenCursor.moveToFirst())
				return;
			
			try{
				do{
					short sirenCaution=sirenCursor.getShort(0);
					swcSirenCaution.setChecked(sirenCaution==1);
					short sirenTime=sirenCursor.getShort(1);
					sbrSirenTime.setProgress(sirenTime);
					lblSirenTimeVal.setText(""+sirenTime);
					short sirenVolume=sirenCursor.getShort(2);
					sbrSirenVolume.setProgress(sirenVolume);
					lblSirenVolumeVal.setText(""+sirenVolume);
				}
				while(sirenCursor.moveToNext());
			}
			catch(Exception ex){
				String strErr="Сбой инициализации0 - "+ex.getMessage();
				Log.e(LOG_TAG,strErr);
			}
			finally{
				sirenCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="Сбой инициализации1 - "+ex.getMessage();
			Log.e(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Saves settings */
	public void btnSave_Click(View vw){
		try{
			int sirenCaution=swcSirenCaution.isChecked()?1:0;
			int sirenTime=sbrSirenTime.getProgress();
			int sirenVolume=sbrSirenVolume.getProgress();
			
			if(m_DbWrkInst.checkRecordExistance("SIREN_TBL", "", new String[]{})){
				ContentValues paramTypeValueCollection=new ContentValues();
				paramTypeValueCollection.put("hello_siren",sirenCaution);
				paramTypeValueCollection.put("siren_time",sirenTime);
				paramTypeValueCollection.put("siren_volume",sirenVolume);
				
				if(!m_DbWrkInst.makeUpdate("SIREN_TBL", paramTypeValueCollection,"", new String[]{}))
					throw new Exception("Обновление не удалось");
			}
			else{
				ArrayList<HashMap<String,String>> valueCollection=new ArrayList<HashMap<String,String>>();
				HashMap<String,String> innerValues=new HashMap<String,String>();
				innerValues.put("hello_siren",""+sirenCaution);
				innerValues.put("siren_time",""+sirenTime);
				innerValues.put("siren_volume",""+sirenVolume);
				valueCollection.add(innerValues);
				
				if(!m_DbWrkInst.makePackageInsert("SIREN_TBL", valueCollection))
					throw new Exception("Добавление не удалось");
			}
			
			SmsSenderCls smsSndrInst=new SmsSenderCls(this);
			smsSndrInst.sendMessageToAlarm("#0"+sirenCaution+""+sirenTime+""+sirenVolume+"#", true);
		}
		catch(Exception ex){
			String strErr="Сбой сохранения настроек - "+ex.getMessage();
			Log.e(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
}
