package com.security.alarm.controller;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsTabActivity extends Activity {

	private EditText txtOprPhone=null;
	private EditText txtOprPwd=null;
	private EditText txtPrgPwd=null;
	private EditText txtOldPrgPwd=null;
	
	private final String LOG_TAG="security.alarm.controller - SettingsTabActivity";
	private DbWorkerCls m_DbWrkInst;
	private final String m_DefaultProgrammingPassword="8888";
	private final String m_DefaultOperativePassword="0000";
	private String m_PreviousPwd="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_tab);
		
		try{
			txtOprPhone=(EditText)findViewById(R.id.txtOprPhone);
			txtOprPwd=(EditText)findViewById(R.id.txtOprPwd);
			txtPrgPwd=(EditText)findViewById(R.id.txtPrgPwd);
			txtOldPrgPwd=(EditText)findViewById(R.id.txtOldPrgPwd);
		}
		catch(Exception ex){
			String strErr="—бой загрузки главных настроек - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Occurs on activity starting */
	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			m_DbWrkInst = DbWorkerCls.getDbWorkerAsSingleton(this);
			Cursor mainSettingsCursor=m_DbWrkInst.m_CurrentDb.rawQuery(
					"select alarm_block_phone, opr_password, prg_password from SETTINGS_TBL", new String[]{});
			
			if(mainSettingsCursor==null||!mainSettingsCursor.moveToFirst())
				return;
			
			try{
				do{
					txtOprPhone.setText(mainSettingsCursor.getString(0));
					txtOprPwd.setText(mainSettingsCursor.getString(1));
					m_PreviousPwd=mainSettingsCursor.getString(2).trim();
					m_PreviousPwd=m_PreviousPwd.equals("")?m_DefaultProgrammingPassword:m_PreviousPwd;
					txtPrgPwd.setText(m_PreviousPwd);
					txtOldPrgPwd.setText(m_PreviousPwd);
				}
				while(mainSettingsCursor.moveToNext());
			}
			catch(Exception ex){}
			finally{
				mainSettingsCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="—бой инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Saves settings */
	public void btnSave_Click(View vw){
		try{
			String mainBlockPhone=txtOprPhone.getText().toString().trim();
			String prevPhone=txtOldPrgPwd.getText().toString().trim();
			m_PreviousPwd=(prevPhone.equals("")||prevPhone.length()!=4)?m_PreviousPwd:prevPhone;
			
			if(mainBlockPhone.equals(""))
				throw new Exception("ѕеред использованием необходимо указать телефон главного блока");

			String programmingPwd=txtPrgPwd.getText().toString().trim();
			
			if(programmingPwd.equals("")||programmingPwd.length()<4
					||programmingPwd.equals(m_DefaultProgrammingPassword)
					||programmingPwd.equals(m_DefaultOperativePassword))
				throw new Exception("ƒл€ выполнени€ обновлени€ надо, чтобы программный пароль не был пуст и не совпадал с паролем по-умолчанию");
			
			ContentValues paramTypeValueCollection=new ContentValues();
			paramTypeValueCollection.put("alarm_block_phone",""+mainBlockPhone);
			String operativePwd=txtOprPwd.getText().toString().trim();
			String smsCommandBuilder="*1";
			
			if(operativePwd.trim().equals("")||operativePwd.length()<4
					||operativePwd.trim().equals(programmingPwd.trim()))
				smsCommandBuilder+=m_DefaultOperativePassword;
			else{
				paramTypeValueCollection.put("opr_password",operativePwd);
				smsCommandBuilder+=operativePwd;
			}

			paramTypeValueCollection.put("prg_password",programmingPwd);
			smsCommandBuilder+=programmingPwd+"*";
			
			if(!m_DbWrkInst.makeUpdate("SETTINGS_TBL", paramTypeValueCollection,"",new String[]{}))
				throw new Exception("ќбновление не удалось");
			
			SmsSenderCls smsSndrInst=new SmsSenderCls(this, m_PreviousPwd);
			smsSndrInst.sendMessageToAlarm(smsCommandBuilder, true);
		}
		catch(Exception ex){
			String strErr="—бой сохранени€ настроек - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
}
