package com.security.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

class SettingCls {
	private final String LOG_TAG="security.alarm.controller - XmlDbWorkerCls";
	private DbWorkerCls m_DbWrkObj;
	public String m_MainBlockPhone="";
	public String m_OperationalPwd="";
	public String m_ProgramPwd="";
	public ArrayList<PhoneCls> m_PhoneLst=new ArrayList<PhoneCls>();
	
	/** Default constructor */
	public SettingCls(){}
	
	/** Not default constructor */
	public SettingCls(Context ctx){
		m_DbWrkObj=DbWorkerCls.getDbWorkerAsSingleton(ctx);
	}
	
	/** Sets settings data into database */
	public boolean setSettingData(){
		boolean result=false;
		
		try{
			if(m_MainBlockPhone==null||m_MainBlockPhone.trim().equals(""))
				throw new Exception("Не задан телефон головного блока");
			
			if(m_DbWrkObj.checkRecordExistance("SETTINGS_TBL", "", new String[]{})
					||m_DbWrkObj.checkRecordExistance("PHONE_TBL", "", new String[]{})
					||m_DbWrkObj.checkRecordExistance("TIMER_TBL", "", new String[]{}))
				m_DbWrkObj.dropDatabase();
			
			ArrayList<HashMap<String,String>> paramsToInsert=new ArrayList<HashMap<String,String>>();
			HashMap<String,String> params=new HashMap<String,String>();
			params.put("alarm_block_phone",m_MainBlockPhone);
			params.put("opr_password",m_OperationalPwd);
			params.put("prg_password",m_ProgramPwd);
			paramsToInsert.add(params);
			byte counter=0;
			
			if(m_DbWrkObj.makePackageInsert("SETTINGS_TBL", paramsToInsert))
				counter++;
			
			if(m_PhoneLst!=null&&m_PhoneLst.size()>0){
				paramsToInsert=new ArrayList<HashMap<String,String>>();

				for(PhoneCls phoneInst:m_PhoneLst){
					params=new HashMap<String,String>();
					params.put("phone_id", ""+phoneInst.m_PhoneId);
					params.put("phone_number", ""+phoneInst.m_PhoneNumber);
					paramsToInsert.add(params);
				}
				
				if(m_DbWrkObj.makePackageInsert("PHONE_TBL", paramsToInsert))
					counter++;
			}
			
			result=(counter>0);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе setSettingData - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Retrieves setting data from database */
	public SettingCls getSettingData(){
		SettingCls result=new SettingCls();
		
		try{
			Cursor crsr=m_DbWrkObj.m_CurrentDb.rawQuery("select alarm_block_phone, opr_password,prg_password from SETTINGS_TBL", new String[]{});
			
			try{
				if(crsr==null||!crsr.moveToFirst())
					return null;
			
				do{
					result.m_MainBlockPhone=crsr.getString(0);
					result.m_OperationalPwd=crsr.getString(1);
					result.m_ProgramPwd=crsr.getString(2);
					result.m_PhoneLst=new ArrayList<PhoneCls>();
				}
				while(crsr.moveToNext());
			}
			catch(Exception ex){}
			finally{
				crsr.close();
			}
			
			crsr=m_DbWrkObj.m_CurrentDb.rawQuery("select phone_id, phone_number from PHONE_TBL", new String[]{});
				
			try{
				if(crsr==null||!crsr.moveToFirst())
					throw new Exception("");
				
				do{
					PhoneCls phoneInst=new PhoneCls(crsr.getShort(0),crsr.getString(1));
					result.m_PhoneLst.add(phoneInst);
				}
				while(crsr.moveToNext());
			}
			catch(Exception ex){}
			finally{
				crsr.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе getSettingData - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return null;
		}

		return result;
	}
}
