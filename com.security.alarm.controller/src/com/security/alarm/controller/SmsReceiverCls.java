package com.security.alarm.controller;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class SmsReceiverCls extends BroadcastReceiver {

	private final String LOG_TAG="security.alarm.controller - SmsReceiverCls";

	/** Retrieves main block phone number */
	private String getCentralBlockPhone(Context ctx){
		String result="";
		
		try{
			Cursor alarmPhoneCursor=DbWorkerCls.getDbWorkerAsSingleton(ctx).m_CurrentDb.rawQuery(
					"select alarm_block_phone from SETTINGS_TBL", new String[]{});
			
			if(alarmPhoneCursor==null||!alarmPhoneCursor.moveToFirst())
				throw new Exception("�� ����� ������� �������� �����");
			
			try{
				do{
					String alarmBlockPhone=alarmPhoneCursor.getString(0).trim();
					
					if(alarmBlockPhone.equals(""))
						throw new Exception("���������� ������ ������� �������� �����");
				}
				while(alarmPhoneCursor.moveToNext());
			}
			catch(Exception ex){
				String strErr="������ ��������� �������� ��������� ����� - "+ex.getMessage();
				Log.d(LOG_TAG,strErr);
			}
			finally{
				alarmPhoneCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="������ ��������� �������� ��������� ����� - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
		
		return result;
	}
	
	/** Handles sms receiving */
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	        String messageFrom="";
	        String messageBody=""; 
	        String mainBlockPhone=getCentralBlockPhone(context);
	        
	        if (bundle == null)
	        	return;
	        
	        Object[] pdus = (Object[]) bundle.get("pdus");
	        msgs = new SmsMessage[pdus.length];            
	            
	        for (int i=0; i<msgs.length; i++){
	        	msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	        	messageFrom = msgs[i].getOriginatingAddress(); 
	            
	            /*if(!messageFrom.trim().equalsIgnoreCase(mainBlockPhone.trim()))
	            	continue;*/
	            
	        	messageBody=msgs[i].getMessageBody().toString();
	            Log.d(LOG_TAG,"sms rcv_msg - "+messageBody);
	            String msg=""+(new Date())+" --- ������ �� : "+messageFrom+" --- \n"+ messageBody+"\n------------------\n";
	            MainTabActivity.setMessage(msg);
	        }  
		}
		catch(Exception ex){
			String strErr="������ ������ SMS - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

}
