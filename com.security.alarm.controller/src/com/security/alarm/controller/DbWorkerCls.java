package com.security.alarm.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

class DbWorkerCls extends SQLiteOpenHelper {

	public static String m_DbName="SecurityAlarmDb";
	public static int m_DbVersion=1;
	private static Context m_Ctx=null;
	public SQLiteDatabase m_CurrentDb=null;
	private final String LOG_TAG="security.alarm.controller - DbWorkerCls";
	private static DbWorkerCls m_DbWrkInst=null;
	
	protected DbWorkerCls(Context context, String dbName,int currentVersion) {
		super(context, dbName, null, currentVersion);
		
		try{
			m_DbName=dbName;
			m_DbVersion=currentVersion;
			m_Ctx=context;
			m_CurrentDb=this.getWritableDatabase();
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка инициализации DbWorkerCls - "+ex.getMessage());
		}
	}

	public static DbWorkerCls getDbWorkerAsSingleton(Context context){
		m_Ctx=context;
		
		if(m_DbWrkInst==null)
			m_DbWrkInst = new DbWorkerCls(context,m_DbName,m_DbVersion);
		
		return m_DbWrkInst;
	}
	
	/** Closes all opened connections */
	public void destroyInstance(){
		try{
			this.close();
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"DbWorkerCls destroyInstance error - "+ex.getMessage());
		}
	}
	
	/** Finalizes object resources */
	@Override
	protected void finalize(){
		destroyInstance();
	}
	
	/** Creates new schema */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try{
			Toast.makeText(m_Ctx, "Попытка создать БД security.alarm.controller", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка создать БД security.alarm.controller");
			////////////////////////////////// Table creation //////////////////////////////
			db.beginTransactionNonExclusive();
		
			try{
				//PHONE_TBL
				db.execSQL("create table PHONE_TBL (phone_id byte primary key, phone_number text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу PHONE_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу PHONE_TBL");
				
				//SETTINGS_TBL
				db.execSQL("create table SETTINGS_TBL (alarm_block_phone text, opr_password text, prg_password text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу SETTINGS_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу SETTINGS_TBL");

				//TIMER_TBL
				db.execSQL("create table TIMER_TBL (arm_time text, disarm_time text, power_switch_time text);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу TIMER_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу TIMER_TBL");
				
				//SIREN_TBL
				db.execSQL("create table SIREN_TBL (hello_siren byte, siren_time byte, siren_volume byte);");
				Toast.makeText(m_Ctx, "Попытка создать таблицу SIREN_TBL", Toast.LENGTH_LONG).show();
				Log.d(LOG_TAG,"Попытка создать таблицу SIREN_TBL");
				
				db.setTransactionSuccessful();
			}
			catch(Exception ex){}
			finally{
				db.endTransaction();
			}
			
			////////////////////////////////Values set //////////////////////////////////////
			m_CurrentDb=db;
			
			//SETTINGS_TBL
			ArrayList<HashMap<String,String>> paramTypeValueCollection=new ArrayList<HashMap<String,String>>();
			paramTypeValueCollection.add(new HashMap<String,String>(){{put("opr_password","0000");put("prg_password","8888");}});
			makePackageInsert("SETTINGS_TBL",paramTypeValueCollection );
			paramTypeValueCollection.clear();
			Toast.makeText(m_Ctx, "Попытка внесения начальных данных в таблицу SETTINGS_TBL", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"Попытка внесения начальных данных в таблицу SETTINGS_TBL");
			
		}
		catch(Exception ex){
			Log.d(LOG_TAG,ex.getMessage());
			Toast.makeText(m_Ctx, "Ошибка при создании БД - "+ex.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
	}

	/** Makes insert into table*/
	public boolean makePackageInsert(String strTableName,ArrayList<HashMap<String,String>> paramTypeValueCollection ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			if(paramTypeValueCollection==null||paramTypeValueCollection.isEmpty())
				throw new Exception("Не заданы параметры для вставки");
			
			ContentValues cv = new ContentValues();
			
			for(int i=0;i<paramTypeValueCollection.size();i++){
				try{
					HashMap<String,String> paramAttr=paramTypeValueCollection.get(i);
				
					if(paramAttr==null||paramAttr.isEmpty())
						continue;
				
					Object[] keyArr=paramAttr.keySet().toArray();
				
					for(int j=0;j<keyArr.length;j++){
						String key=(String)(keyArr[j]);
						cv.put(key,paramAttr.get(key));
					}
				
					m_CurrentDb.insert(strTableName, null, cv);
				}
				catch(Exception ex){
					Log.d(LOG_TAG,ex.getMessage());
				}
			}
			
			result = true;
		}
		catch(Exception ex){
			String strErr="Ошибка в методе MakePackageInsert - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Makes update into table*/
	public boolean makeUpdate(String strTableName,ContentValues paramTypeValueCollection,
			String whereClause, String[] whereValues){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			if(paramTypeValueCollection==null||paramTypeValueCollection.size()<1)
				throw new Exception("Не заданы параметры для изменения");
			
			result=(m_CurrentDb.update(strTableName,paramTypeValueCollection,whereClause,whereValues)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе MakeUpdate - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

	/** Makes delete from table*/
	public boolean makeDelete(String strTableName, String whereClause, String[] whereValues){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			result= (m_CurrentDb.delete(strTableName,whereClause,whereValues)>0);
		}
		catch(Exception ex){
			String strErr="Ошибка в методе makeDelete - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}
	
	/** Upgrades schema */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			
		}
		catch(Exception ex){
			Log.d(LOG_TAG,"Ошибка при пересоздании БД - "+ex.getMessage());
			return;
		}
	}

	/** Drops current db */
	public boolean dropDatabase(){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует!");
			
			m_CurrentDb.beginTransactionNonExclusive();
			m_CurrentDb.execSQL("delete from PHONE_TBL");
			m_CurrentDb.execSQL("delete from SETTINGS_TBL");
			m_CurrentDb.execSQL("delete from TIMER_TBL");
			m_CurrentDb.execSQL("delete from SIREN_TBL");
			m_CurrentDb.setTransactionSuccessful();
			result=true;
			Toast.makeText(m_Ctx, "БД eBook удалена", Toast.LENGTH_LONG).show();
			Log.d(LOG_TAG,"БД eBook удалена");
		}
		catch(Exception ex){
			String strErr="Ошибка в методе dropDatabase - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		finally{
			if(m_CurrentDb!=null)
				m_CurrentDb.endTransaction();
		}
		
		return result;
	}

	/** Checks whether record exists in table*/
	public boolean checkRecordExistance(String strTableName,String strCondition, String[] conditionArgs ){
		boolean result=false;
		
		try{
			if(m_CurrentDb==null)
				throw new Exception("БД не существует");
			
			if(strTableName.trim().equalsIgnoreCase(""))
				throw new Exception("Таблица не указана");
			
			Cursor reader = m_CurrentDb.query(strTableName, new String[]{"count(1) as cnt"}, strCondition, conditionArgs, null, null, null);
			
			if (reader != null) {
			      if (reader.moveToFirst())
			    		  result=(reader.getInt(0)>0);

			      reader.close();
			}
		}
		catch(Exception ex){
			String strErr="Ошибка в методе CheckRecordExistance - "+ex.getMessage();
			Log.d(LOG_TAG,strErr);
			return false;
		}
		
		return result;
	}

}
