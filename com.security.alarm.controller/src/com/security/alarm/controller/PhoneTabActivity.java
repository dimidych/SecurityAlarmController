package com.security.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneTabActivity extends Activity {

	private EditText txtTel1=null;
	private EditText txtTel2=null;
	private EditText txtTel3=null;
	private EditText txtTel4=null;
	private EditText txtTel5=null;
	private EditText txtTel6=null;
	private EditText txtSms7=null;
	private EditText txtSms8=null;
	private EditText txtSms9=null;

	private final String LOG_TAG="security.alarm.controller - PhoneTabActivity";
	private DbWorkerCls m_DbWrkInst;
	
	/** Occurs on activity creation */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_tab);
		
		try{
			txtTel1=(EditText)findViewById(R.id.txtTel1);
			txtTel2=(EditText)findViewById(R.id.txtTel2);
			txtTel3=(EditText)findViewById(R.id.txtTel3);
			txtTel4=(EditText)findViewById(R.id.txtTel4);
			txtTel5=(EditText)findViewById(R.id.txtTel5);
			txtTel6=(EditText)findViewById(R.id.txtTel6);
			txtSms7=(EditText)findViewById(R.id.txtSms7);
			txtSms8=(EditText)findViewById(R.id.txtSms8);
			txtSms9=(EditText)findViewById(R.id.txtSms9);
		}
		catch(Exception ex){
			String strErr="Сбой загрузки настроек телефонов - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
		}
	}

	/** Occurs on activity starting */
	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			m_DbWrkInst = DbWorkerCls.getDbWorkerAsSingleton(this);
			Cursor telNumCursor=m_DbWrkInst.m_CurrentDb.rawQuery("select phone_id, phone_number from PHONE_TBL", new String[]{});
			
			if(telNumCursor==null||!telNumCursor.moveToFirst())
				return;
			
			try{
				do{
					short phoneId=telNumCursor.getShort(0);
					String phoneNum=telNumCursor.getString(1);
					
					switch(phoneId){
						case 1:
							txtTel1.setText(phoneNum);
							break;
							
						case 2:
							txtTel2.setText(phoneNum);						
							break;
													
						case 3:
							txtTel3.setText(phoneNum);
							break;
							
						case 4:
							txtTel4.setText(phoneNum);
							break;
							
						case 5:
							txtTel5.setText(phoneNum);
							break;
							
						case 6:
							txtTel6.setText(phoneNum);
							break;
							
						case 7:
							txtSms7.setText(phoneNum);
							break;
							
						case 8:
							txtSms8.setText(phoneNum);
							break;
							
						case 9:
							txtSms9.setText(phoneNum);
							break;
					}
				}
				while(telNumCursor.moveToNext());
			}
			catch(Exception ex){}
			finally{
				telNumCursor.close();
			}
		}
		catch(Exception ex){
			String strErr="Сбой инициализации - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Makes phone number insert or update */
	private boolean makePhoneInsertion(short phoneId,String phoneNum){
		boolean result=false;
		
		try{
			if(phoneId<=0||phoneNum.trim().equalsIgnoreCase(""))
				throw new Exception("Неверные параметры");

			if(m_DbWrkInst.checkRecordExistance("PHONE_TBL", " phone_id=? ", new String[]{""+phoneId})){
				ContentValues paramTypeValueCollection=new ContentValues();
				paramTypeValueCollection.put("phone_id",""+phoneId);
				paramTypeValueCollection.put("phone_number",phoneNum);
				
				if(!m_DbWrkInst.makeUpdate("PHONE_TBL", paramTypeValueCollection," phone_id=? ", new String[]{""+phoneId}))
					throw new Exception("Обновление не удалось");
			}
			else{
				ArrayList<HashMap<String,String>> valueCollection=new ArrayList<HashMap<String,String>>();
				HashMap<String,String> innerValues=new HashMap<String,String>();
				innerValues.put("phone_id",""+phoneId);
				innerValues.put("phone_number",phoneNum);
				valueCollection.add(innerValues);
				
				if(!m_DbWrkInst.makePackageInsert("PHONE_TBL", valueCollection))
					throw new Exception("Добавление не удалось");
			}
				
			SmsSenderCls smsSndrInst=new SmsSenderCls(this);
			smsSndrInst.sendMessageToAlarm("#"+phoneId+phoneNum+"#",true);
			result=true;
		}
		catch(Exception ex){
			String strErr="Сбой обновления настроек - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return result;
	}
	
	/** Deletes phone */
	private boolean deletePhone(short phoneId){
		boolean result=false;
		
		try{
			if(phoneId<=0)
				throw new Exception("Неверные параметры");
			
			ContentValues paramTypeValueCollection=new ContentValues();
			paramTypeValueCollection.put("phone_id",""+phoneId);
			m_DbWrkInst.makeDelete("PHONE_TBL", " phone_id=? ",new String[]{""+phoneId});
			SmsSenderCls smsSndrInst=new SmsSenderCls(this);
			smsSndrInst.sendMessageToAlarm("#"+phoneId+"#",true);
			result=true;
		}
		catch(Exception ex){
			String strErr="Сбой удаления настроек - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return result;
	}
	
	/** Saves settings for tel1 */
	public void btnSave1_Click(View vw){
		try{
			if(!txtTel1.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)1,txtTel1.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #1");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel1 */
	public void btnDel1_Click(View vw){
		try{
			if(!txtTel1.getText().toString().trim().equals(""))
				if(!deletePhone((short)1))
					throw new Exception("Не удалось удалить телефон #1");
				else
					txtTel1.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона1 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel2 */
	public void btnSave2_Click(View vw){
		try{
			if(!txtTel2.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)2,txtTel2.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #2");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel2 */
	public void btnDel2_Click(View vw){
		try{
			if(!txtTel2.getText().toString().trim().equals(""))
				if(!deletePhone((short)2))
					throw new Exception("Не удалось удалить телефон #2");
				else
					txtTel2.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона2 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel3 */
	public void btnSave3_Click(View vw){
		try{
			if(!txtTel3.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)3,txtTel3.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #3");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона3 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel3 */
	public void btnDel3_Click(View vw){
		try{
			if(!txtTel3.getText().toString().trim().equals(""))
				if(!deletePhone((short)3))
					throw new Exception("Не удалось удалить телефон #3");
				else
					txtTel3.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона3 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel4 */
	public void btnSave4_Click(View vw){
		try{
			if(!txtTel4.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)4,txtTel4.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #4");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона4 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel4 */
	public void btnDel4_Click(View vw){
		try{
			if(!txtTel4.getText().toString().trim().equals(""))
				if(!deletePhone((short)4))
					throw new Exception("Не удалось удалить телефон #4");
				else
					txtTel4.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона4 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel5 */
	public void btnSave5_Click(View vw){
		try{
			if(!txtTel5.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)5,txtTel5.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #5");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона5 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel5 */
	public void btnDel5_Click(View vw){
		try{
			if(!txtTel5.getText().toString().trim().equals(""))
				if(!deletePhone((short)5))
					throw new Exception("Не удалось удалить телефон #5");
				else
					txtTel5.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона5 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel6 */
	public void btnSave6_Click(View vw){
		try{
			if(!txtTel6.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)6,txtTel6.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #6");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона6 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel6 */
	public void btnDel6_Click(View vw){
		try{
			if(!txtTel6.getText().toString().trim().equals(""))
				if(!deletePhone((short)6))
					throw new Exception("Не удалось удалить телефон #6");
				else
					txtTel6.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона6 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel7 */
	public void btnSave7_Click(View vw){
		try{
			if(!txtSms7.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)7,txtSms7.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #7");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона7 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel7 */
	public void btnDel7_Click(View vw){
		try{
			if(!txtSms7.getText().toString().trim().equals(""))
				if(!deletePhone((short)7))
					throw new Exception("Не удалось удалить телефон #7");
				else
					txtSms7.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона7 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel8 */
	public void btnSave8_Click(View vw){
		try{
			if(!txtSms8.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)8,txtSms8.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #8");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона8 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel8 */
	public void btnDel8_Click(View vw){
		try{
			if(!txtSms8.getText().toString().trim().equals(""))
				if(!deletePhone((short)8))
					throw new Exception("Не удалось удалить телефон #8");
				else
					txtSms8.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона8 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/** Saves settings for tel9 */
	public void btnSave9_Click(View vw){
		try{
			if(!txtSms9.getText().toString().trim().equals(""))
				if(!makePhoneInsertion((short)9,txtSms9.getText().toString().trim()))
					throw new Exception("Не удалось сохранить телефон #9");
		}
		catch(Exception ex){
			String strErr="Сбой сохранения телефона9 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Deletes settings for tel9 */
	public void btnDel9_Click(View vw){
		try{
			if(!txtSms9.getText().toString().trim().equals(""))
				if(!deletePhone((short)9))
					throw new Exception("Не удалось удалить телефон #9");
				else
					txtSms9.setText("");
		}
		catch(Exception ex){
			String strErr="Сбой удаления телефона9 - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			Toast.makeText(this, strErr, Toast.LENGTH_LONG).show();
			return;
		}
	}
	
}
