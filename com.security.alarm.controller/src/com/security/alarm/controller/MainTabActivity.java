package com.security.alarm.controller;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainTabActivity extends Activity {

	private static EditText txtStatus=null;
	private static final String LOG_TAG="security.alarm.controller - MainTabActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab);
		
		try{
			txtStatus=(EditText)findViewById(R.id.txtStatus);
		}
		catch(Exception ex){
			String strErr="—бой инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Sets message into text field */
	public static void setMessage(String message){
		try{
			String lastMessage=txtStatus.getText().toString();
			txtStatus.setText(lastMessage+message);
		}
		catch(Exception ex){
			String strErr="—бой получени€ сообщени€ - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Arms security alarm */
	public void btnArm_Click(View vw){
		try{
			SmsSenderCls smsSenderInst=new SmsSenderCls(this);
			smsSenderInst.sendMessageToAlarm("SF", true);
		}
		catch(Exception ex){
			String strErr="—бой постановки на охрану - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Arms security alarm in power safe mode */
	public void btnPowerSafeArm_Click(View vw){
		try{
			SmsSenderCls smsSenderInst=new SmsSenderCls(this);
			smsSenderInst.sendMessageToAlarm("BF", true);
		}
		catch(Exception ex){
			String strErr="—бой постановки на умную охрану - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Disarms security alarm */
	public void btnDisarm_Click(View vw){
		try{
			SmsSenderCls smsSenderInst=new SmsSenderCls(this);
			smsSenderInst.sendMessageToAlarm("CF", true);
		}
		catch(Exception ex){
			String strErr="—бой сн€ти€ с охраны - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Retrieves status from security alarm */
	public void btnStatus_Click(View vw){
		try{
			SmsSenderCls smsSenderInst=new SmsSenderCls(this);
			smsSenderInst.sendMessageToAlarm("STATUS", true);
		}
		catch(Exception ex){
			String strErr="—бой получени€ статуса - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

}
