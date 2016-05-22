package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class bindinginfo extends Activity implements OnClickListener{
	public static final String serverUrl = "http://10.80.182.140:8080/Iov/bindinginfo";	
	String plate;
	HttpClient client = new DefaultHttpClient();
	TextView brandtv;
	ImageView logoView;
	TextView modeltv;
	TextView plateNumtv;
	TextView engineNumtv;
	TextView leveltv;
	TextView kilometerstv;
	TextView gaslinetv;
	TextView engineperformtv;
	TextView transperformtv;
	TextView lightingtv;
	Button deletebtn;
	Button cancel;
    
	int theposition;
	int delete = 0;
	
	
	
   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.bindinginfo);
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
			detectDiskWrites().detectNetwork().penaltyLog().build());
	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
			detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
	findviewbyid();
	Intent intent = getIntent();
	plate = intent.getStringExtra("plate");
	theposition = intent.getIntExtra("position", -1);
	
	displayinfo(plate);
	
}
   
   
void findviewbyid(){
	brandtv = (TextView) findViewById(R.id.brand_perform);
	logoView = (ImageView) findViewById(R.id.logo_perform);
	modeltv = (TextView) findViewById(R.id.model_perform);
	plateNumtv = (TextView)findViewById(R.id.plate_num_perform);
	engineNumtv = (TextView) findViewById(R.id.engine_num_perform);
	leveltv = (TextView) findViewById(R.id.level_perform);
	kilometerstv = (TextView) findViewById(R.id.kilometers_perform);
	gaslinetv  = (TextView) findViewById(R.id.gasline_perform);
	engineperformtv = (TextView) findViewById(R.id.engine_per_perform);
	transperformtv = (TextView) findViewById(R.id.transmission_per_perform);
	lightingtv = (TextView) findViewById(R.id.lighting_perform);
	deletebtn = (Button) findViewById(R.id.deletebinding);
	cancel = (Button) findViewById(R.id.infocanel);
	deletebtn.setOnClickListener(this);
	cancel.setOnClickListener(this);
}
   
   private int dodelete(String plateNum){
		 int ret = 0;
		
		HttpPost httpPost = new HttpPost(serverUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("operation", "delete"));
		params.add(new BasicNameValuePair("dplate", plateNum));
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
				 ret =  json.getInt("result_code");
				Log.d("lu", "ret = " + ret);
				return ret;
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
		  return ret;
	}
   
   @SuppressWarnings("deprecation")
private void  displayinfo(String plate) {
	
		HttpPost httpPost = new HttpPost(serverUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("operation", "display"));
		params.add(new BasicNameValuePair("plate_Num", plate));
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
	           
	            brandtv.setText(json.getString("brand"));
				logoView.setImageResource(Integer.parseInt(json.getString("logo").replaceAll("^0[x|X]", ""),16));
				modeltv.setText(json.getString("model"));
				plateNumtv.setText(plate);
				engineNumtv.setText(json.getString("engine_num"));
				leveltv.setText(json.getString("auto_level"));
				kilometerstv.setText(json.getString("kilometers"));
				gaslinetv.setText(json.getString("gas_line"));
				engineperformtv.setText(json.getString("engine_perform"));
				transperformtv.setText(json.getString("trans_perform"));
				lightingtv.setText(json.getString("lighting"));
				
			 
			}	
		} catch (UnsupportedEncodingException e) {
			Log.d("lu", "UnsupportedEncodingException");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.d("lu", "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("lu", "IOException");
		
			Toast.makeText(bindinginfo.this, "网络连接异常", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}


@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.deletebinding:
		delete =
		dodelete(plate);
		if (delete==2) {
			Toast.makeText(bindinginfo.this, "解绑成功!", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("theposition", theposition);
			intent.putExtra("delete", delete);
			setResult(RESULT_OK,intent);
			finish();
		}else {
			Toast.makeText(bindinginfo.this, "解绑失败,检查网络!", Toast.LENGTH_SHORT).show();
		}
		break;
	case R.id.infocanel:
		finish();

	default:
		break;
	}
}

}
