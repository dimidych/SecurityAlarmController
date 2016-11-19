package com.security.alarm.controller;

/*import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;*/
import android.content.Context;
/*import android.content.Intent;
import android.content.IntentFilter;*/
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
//import android.widget.Toast;
import android.widget.Toast;

public class SmsSenderCls {

	private final String LOG_TAG="security.alarm.controller - SmsSenderCls";
	private Context m_Ctx=null;
	private String m_PreviousPassword="";
	
	/** Not default constructor */
	public SmsSenderCls(Context ctx){
		m_Ctx=ctx;
	}

	/** Not default constructor */
	public SmsSenderCls(Context ctx, String previousPassword){
		m_Ctx=ctx;
		m_PreviousPassword=previousPassword;
	}
	
	/** Sends SMS message to main block of alarm */
	public void sendMessageToAlarm(String message, boolean isProgrammingPwd){
		try{
			Cursor alarmPhoneCursor=DbWorkerCls.getDbWorkerAsSingleton(m_Ctx).m_CurrentDb.rawQuery(
					"select alarm_block_phone, opr_password, prg_password from SETTINGS_TBL", new String[]{});
			
			if(alarmPhoneCursor==null||!alarmPhoneCursor.moveToFirst())
				throw new Exception("Не задан телефон главного блока");
			
			try{
				do{
					String alarmBlockPhone=alarmPhoneCursor.getString(0).trim();
					String operativePwd=alarmPhoneCursor.getString(1).trim();
					String programmingPwd=alarmPhoneCursor.getString(2).trim();
					
					if(alarmBlockPhone.equals(""))
						throw new Exception("Необходимо задать телефон главного блока");
					
					if(isProgrammingPwd&&programmingPwd.equals(""))
						throw new Exception("Необходимо задать программный пароль");
					
					if(!isProgrammingPwd&&operativePwd.equals(""))
						throw new Exception("Необходимо задать оперативный пароль");
					
					sendMessage(alarmBlockPhone, (m_PreviousPassword.trim().equals("")?
							(isProgrammingPwd ? programmingPwd:operativePwd):m_PreviousPassword)+message);
				}
				while(alarmPhoneCursor.moveToNext());
			}
			catch(Exception ex){
				String strErr="Ошибка отправки SMS - "+ex.getMessage();
				Log.d(LOG_TAG,strErr);
			}
			finally{
				alarmPhoneCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка отправки SMS - "+ex.getMessage();
			Toast.makeText(m_Ctx, strErr, Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,strErr);
		}
	}
	
	/** Sends SMS message */
	public void sendMessage(String phoneNum, String message){
		try{
			if(phoneNum.trim().equals("")||message.trim().equals(""))
				return;
			
			/*String SENT = "SMS_SENT";
		    String DELIVERED = "SMS_DELIVERED";
		    PendingIntent sentPI = PendingIntent.getBroadcast(m_Ctx, 0, new Intent(SENT), 0);
		    PendingIntent deliveredPI = PendingIntent.getBroadcast(m_Ctx, 0, new Intent(DELIVERED), 0);
		    
		    m_Ctx.registerReceiver(new BroadcastReceiver(){
		        @Override
		        public void onReceive(Context arg0, Intent arg1) {
		            switch (getResultCode())
		            {
		                case Activity.RESULT_OK:
		                    Toast.makeText(m_Ctx, "Сообщение успешно отправлено", Toast.LENGTH_LONG).show();
		                    break;
		                    
		                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
		                    Toast.makeText(m_Ctx, "Сообщение не доставлено", Toast.LENGTH_LONG).show();
		                    break;
		                    
		                case SmsManager.RESULT_ERROR_NO_SERVICE:
		                    Toast.makeText(m_Ctx, "Вне зоны обслуживания", Toast.LENGTH_LONG).show();
		                    break;
		                    
		                case SmsManager.RESULT_ERROR_NULL_PDU:
		                    Toast.makeText(m_Ctx, "Невозможно отправить сообщение", Toast.LENGTH_LONG).show();
		                    break;
		                    
		                case SmsManager.RESULT_ERROR_RADIO_OFF:
		                    Toast.makeText(m_Ctx, "Включите радиомодуль", Toast.LENGTH_LONG).show();
		                    break;
		            }
		        }
		    }, new IntentFilter(SENT));
		    
		    m_Ctx.registerReceiver(new BroadcastReceiver(){
		        @Override
		        public void onReceive(Context arg0, Intent arg1) {
		            switch (getResultCode())
		            {
		                case Activity.RESULT_OK:
		                    Toast.makeText(m_Ctx, "Сообщение доставлено", Toast.LENGTH_LONG).show();
		                    break;
		                    
		                case Activity.RESULT_CANCELED:
		                    Toast.makeText(m_Ctx, "Сообщение не доставлено",Toast.LENGTH_LONG).show();
		                    break;                        
		            }
		        }
		    }, new IntentFilter(DELIVERED));*/

			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNum.trim(), null, message.trim(), null/*sentPI*/, null/*deliveredPI*/);
		}
		catch(Exception ex){
			String strErr="Ошибка отправки SMS0 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(m_Ctx, strErr, Toast.LENGTH_LONG).show();
		}
	}
}
