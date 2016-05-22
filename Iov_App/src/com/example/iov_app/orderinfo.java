package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zxing.encoding.EncodingHandler;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class orderinfo extends Activity implements OnClickListener{
	
	@SuppressWarnings("deprecation")
	HttpClient client = new DefaultHttpClient();
	public static final String serverUrl = "http://10.80.182.140:8080/Iov/orderinfo";
	       String order_number;
	       String user;
	       String ousername ;
	  	 String otimeString ;
	  	 String ostationString  ;
	  	 String ogastypeString  ;
	  	 String ototalprice ;
	  	 String oaddress ;
	  	String ogasnum ;
	  	String content;
	        TextView usertext;
			TextView timeView;
			TextView gas_station;
			TextView address;
			TextView gas_type;
			TextView gas_num;
			TextView total_price;
			Button backButton;
			Button deleteButton;
			ImageView qrcodeImageView;   
		    int position;
			int delete;

   @SuppressLint("NewApi")
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	   
	super.onCreate(savedInstanceState);
	setContentView(R.layout.order_info);
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
			detectDiskWrites().detectNetwork().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
			detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
			findviewbyid();
			init();
		Intent	 rintent = getIntent();
		order_number = 	rintent.getStringExtra("order_num");
		user = rintent.getStringExtra("name");
		position = rintent.getIntExtra("position", -1);
		
		
		displayorderinfo();
		zings();
		
}
   void findviewbyid(){
		usertext =  (TextView) findViewById(R.id.order_name);
		timeView = (TextView) findViewById(R.id.time_show);
		gas_station =  (TextView) findViewById(R.id.ed_gas_station);
		address =   (TextView) findViewById(R.id.ed_address);
		gas_type = (TextView) findViewById(R.id.ed_gas_type);
		gas_num = (TextView) findViewById(R.id.ed_gas_num);
		total_price = (TextView) findViewById(R.id.ed_total_price);
		backButton = (Button) findViewById(R.id.back);
		deleteButton = (Button) findViewById(R.id.delete_order);
		qrcodeImageView = (ImageView) findViewById(R.id.showzxing);
		
	}


	void init(){
		backButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);  
	}
   
	
void zings(){
	try {
		 ousername = usertext.getText().toString().trim();
		  otimeString  = timeView.getText().toString().trim();
		  ostationString  = gas_station.getText().toString().trim();
		  oaddress  = address.getText().toString().trim();
		 ototalprice = total_price.getText().toString().trim();
		 ogasnum = gas_num.getText().toString().trim();
		 content = otimeString+"|"+ostationString+"|"+ogastypeString+"|"+ogasnum+"|"+order_number+"|"+ousername+"|"+oaddress+"|"+ototalprice;
			Bitmap qrcodeBitmap = EncodingHandler.createQRCode(content, 400);
			qrcodeImageView.setImageBitmap(qrcodeBitmap);
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
@SuppressWarnings("deprecation")
private int dodeleteorder(String ordernum){
	 int ret = 0;
	
	HttpPost httpPost = new HttpPost(serverUrl);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("operation", "delete"));
	params.add(new BasicNameValuePair("dorder", ordernum));
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
			 ret =  json.getInt("delete_code");
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
private void displayorderinfo(){
	
   
	
	HttpPost httpPost = new HttpPost(serverUrl);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("orderinfo", order_number));
	params.add(new BasicNameValuePair("operation", "display"));
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
			usertext.setText(user);
			timeView.setText(json.getString("order_time"));
			gas_station.setText(json.getString("gas_station"));
			address.setText(json.getString("address"));
			gas_type.setText(json.getString("gas_type"));
			gas_num.setText(json.getString("gas_num"));
			total_price.setText(json.getString("total_price"));
			
			
		 
		}	
	} catch (UnsupportedEncodingException e) {
		Log.d("lu", "UnsupportedEncodingException");
		e.printStackTrace();
	} catch (ClientProtocolException e) {
		Log.d("lu", "ClientProtocolException");
		e.printStackTrace();
	} catch (IOException e) {
		Log.d("lu", "IOException");
	
		Toast.makeText(orderinfo.this, "ÍøÂçÁ¬½ÓÒì³£", Toast.LENGTH_LONG).show();
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
	case R.id.back:
		finish();
		break;
	case R.id.delete_order:
	 delete =	dodeleteorder(order_number);
	if (delete==1) {
      Toast.makeText(orderinfo.this, "É¾³ý³É¹¦!", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent();
		intent.putExtra("delete", delete);
		intent.putExtra("theposition", position);
		setResult(RESULT_OK,intent);
		finish();
	}else {
		Toast.makeText(orderinfo.this, "É¾³ýÊ§°Ü,¼ì²éÍøÂç", Toast.LENGTH_SHORT).show();
	}
	default:
		break;
	}
	
	
	
}
   

}
