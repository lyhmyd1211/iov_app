package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class Car_Service extends Service{
	public static final String serverUrl = "http://10.80.182.140:8080/Iov/carinfo";
	private MyBinder mBinder = new MyBinder();  
	public String  plate ;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		plate = intent.getStringExtra("which_car");
		Log.d("lu", "plate = " + plate);
		return mBinder;
	}

@Override
public void onCreate() {
	// TODO Auto-generated method stub
	super.onCreate();
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
			detectDiskWrites().detectNetwork().penaltyLog().build());
	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
			detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
	
}
@SuppressLint("NewApi")
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
	new Thread(new Runnable() {
		
		@Override
		public void run() {
		Log.d("LongRunningService", "executed at " + new Date().toString());
		mBinder.update();
		}
		}).start();
	AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
	int anHour = 3 * 1000; 
	long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
	Intent i = new Intent(Car_Service.this, AlarmReceiver.class);
	PendingIntent pi = PendingIntent.getBroadcast(Car_Service.this, 0, i, 0);
	manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

	
	return super.onStartCommand(intent, flags, startId);
}

	class MyBinder extends Binder {  
		public String   gasline,kilometers,engine,trans,lighting;
		
		
      
		public void update() {  
            // 执行具体的下载任务 
        
    			HttpClient client = new DefaultHttpClient();
    			HttpPost httpPost = new HttpPost(serverUrl);
    			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
    			params.add(new BasicNameValuePair("plate", plate));
    			HttpResponse httpResponse = null;
    			try {
    				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    				httpResponse = client.execute(httpPost);
    				if(httpResponse.getStatusLine().getStatusCode() == 200) {
    					Log.d("lu", "network OK!");
    					HttpEntity entity = httpResponse.getEntity();
    					String entityString = EntityUtils.toString(entity,HTTP.UTF_8);
    					String jsonString = entityString.substring(entityString.indexOf("{"));
    					Log.d("lu", "entity = " + jsonString);
    					JSONObject json = new JSONObject(jsonString);
    					// ret =  json.getInt("result_code");
    					  gasline = json.getString("gasline");
    					  kilometers = json.getString("kilometers");
    					  engine = json.getString("engine_performance");
    					  trans = json.getString("transmission_performance");
    					  lighting = json.getString("auto_lighting");
    					Log.d("lu", "gasline = " + gasline);
    					Log.d("lu", "kilometers = " + kilometers);
    					Log.d("lu", "engine = " + engine);
    					Log.d("lu", "trans = " + trans);
    					Log.d("lu", "lighting = " + lighting);
    					
    				}
    				
    				
    			} catch (UnsupportedEncodingException e) {
    				Log.d("lu", "UnsupportedEncodingException");
    				e.printStackTrace();
    			} catch (ClientProtocolException e) {
    				Log.d("lu", "ClientProtocolException");
    				e.printStackTrace();
    			} catch (IOException e) {
    				Log.d("lu", "IOException");
    				
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
            	
     			
            }
		
    			
	}		
	
    
}
