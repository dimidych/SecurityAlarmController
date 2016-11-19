package com.security.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TimerTabActivity extends Activity {

	private Spinner cmbAlarmZone;
	private EditText txtArmTimer;
	private EditText txtDisarmTimer;
	private EditText txtSwitchTimer;
	
	private final String LOG_TAG="security.alarm.controller - TimerTabActivity";
	private DbWorkerCls m_DbWrkInst;
	
	/** Occurs on current activity creation */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer_tab);
		
		try{
			cmbAlarmZone=(Spinner)findViewById(R.id.cmbAlarmZone);
			txtArmTimer=(EditText)findViewById(R.id.txtArmTimer);
			txtDisarmTimer=(EditText)findViewById(R.id.txtDisarmTimer);
			txtSwitchTimer=(EditText)findViewById(R.id.txtSwitchTimer);
			fillAlarmZoneLst();
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
			Cursor timerCursor=m_DbWrkInst.m_CurrentDb.rawQuery("select arm_time, disarm_time, power_switch_time from TIMER_TBL",new String[]{});
			
			if(timerCursor==null||!timerCursor.moveToFirst())
				return;
			
			try{
				do{
					txtArmTimer.setText(timerCursor.getString(0));
					txtDisarmTimer.setText(timerCursor.getString(1));
					txtSwitchTimer.setText(timerCursor.getString(2));
				}
				while(timerCursor.moveToNext());
			}
			catch(Exception ex){}
			finally{
				timerCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="Сбой инициализации1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Fills alarm zone */
	private void fillAlarmZoneLst(){
		try{
			ArrayList<String> alarmZoneLst=new ArrayList<String>();
			
			for(int i=0;i<10;i++){
				if(i==0)
					alarmZoneLst.add("0 - выключено");
				else if(i>0&&i<6)
					alarmZoneLst.add(i+" - периодическая зона");
				else
					alarmZoneLst.add(i+" - временная зона");
			}
			
			ArrayAdapter<String> lstAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alarmZoneLst);
			cmbAlarmZone.setAdapter(lstAdapter);
			cmbAlarmZone.setSelection(0);
		}
		catch(Exception ex){
			String strErr="Сбой заполнения охранных зон - "+ex.getMessage();
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Saves settings */
	public void btnSave_Click(View vw){
		try{
			String armTime=txtArmTimer.getText().toString().trim();
			String disarmTime=txtDisarmTimer.getText().toString().trim();
			String switchTime=txtSwitchTimer.getText().toString().trim();

			if(m_DbWrkInst.checkRecordExistance("TIMER_TBL", "", new String[]{})){
				ContentValues paramTypeValueCollection=new ContentValues();
				paramTypeValueCollection.put("arm_time",armTime);
				paramTypeValueCollection.put("disarm_time",disarmTime);
				paramTypeValueCollection.put("power_switch_time",switchTime);
				
				if(!m_DbWrkInst.makeUpdate("TIMER_TBL", paramTypeValueCollection,"", new String[]{}))
					throw new Exception("Обновление не удалось");
			}
			else{
				ArrayList<HashMap<String,String>> valueCollection=new ArrayList<HashMap<String,String>>();
				HashMap<String,String> innerValues=new HashMap<String,String>();
				innerValues.put("arm_time",""+armTime);
				innerValues.put("disarm_time",disarmTime);
				innerValues.put("power_switch_time",switchTime);
				valueCollection.add(innerValues);
				
				if(!m_DbWrkInst.makePackageInsert("TIMER_TBL", valueCollection))
					throw new Exception("Добавление не удалось");
			}
			
			SmsSenderCls smsSndrInst=new SmsSenderCls(this);
			int currentHours=(new java.util.Date()).getHours();
			int currentMinutes=(new java.util.Date()).getMinutes();
			smsSndrInst.sendMessageToAlarm("#2"+(currentHours>9?""+currentHours:"0"+currentHours)+
					(currentMinutes>9?""+currentMinutes:"0"+currentMinutes)+"#", true);
			
			if(armTime.equals(""))
				smsSndrInst.sendMessageToAlarm("#300000#",true);
			else
				smsSndrInst.sendMessageToAlarm("#3"+armTime.replace(":", "").trim()+
						cmbAlarmZone.getSelectedItemPosition()+"#",true);
			
			if(disarmTime.equals(""))
				smsSndrInst.sendMessageToAlarm("#400000#",true);
			else
				smsSndrInst.sendMessageToAlarm("#4"+disarmTime.replace(":", "").trim()+
						cmbAlarmZone.getSelectedItemPosition()+"#",true);
			
			if(switchTime.equals(""))
				smsSndrInst.sendMessageToAlarm("#500000#",true);
			else
				smsSndrInst.sendMessageToAlarm("#5"+switchTime.replace(":", "").trim()+
						cmbAlarmZone.getSelectedItemPosition()+"#",true);
		}
		catch(Exception ex){
			String strErr="Сбой сохранения настроек - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
}
