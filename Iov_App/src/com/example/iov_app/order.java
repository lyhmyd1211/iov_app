package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.baidu.platform.comapi.map.s;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.juhe.petrolstation.adapter.PriceListAdapter;
import com.juhe.petrolstation.bean.Petrol;
import com.juhe.petrolstation.bean.Station;
import com.zxing.encoding.EncodingHandler;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class order extends Activity implements OnClickListener{
	public static final String serverUrl = "http://10.80.182.140:8080/Iov/orderin";
	
	TextView usertext;
	TextView timeView;
	ImageButton timebtn;
	TextView gas_station;
	TextView address;
	Spinner gas_type;
	EditText gas_num;
	EditText total_price;
	Button okButton;
	Button cancelButton;
	ImageView qrcodeImageView;
	Station s;
	
	private String initEndDateTime = "2016-1-1_00:00";
	String infoString = null;
	String []splitString = new String[3]; 
	 String ousername ;
	 String otimeString ;
	 String ostationString  ;
	 String ogastypeString  ;
	 String ototalprice ;
	 String oaddress ;
	 String ordernum ;
	String ogasnum ;
	//String gasnum;
	String content;
	double sum = 0.0;
	double num = 0.0;
	double price =0.0;
	

	

	
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.order);
	findviewbyid();
	init();
	SetText();
}


void findviewbyid(){
	usertext =  (TextView) findViewById(R.id.order_name);
	timeView = (TextView) findViewById(R.id.time_show);
	timebtn = (ImageButton) findViewById(R.id.time_btn);
	gas_station =  (TextView) findViewById(R.id.ed_gas_station);
	address =   (TextView) findViewById(R.id.ed_address);
	gas_type = (Spinner) findViewById(R.id.ed_gas_type);
	gas_num = (EditText) findViewById(R.id.ed_gas_num);
	total_price = (EditText) findViewById(R.id.ed_total_price);
	okButton = (Button) findViewById(R.id.btn_order_ok);
	cancelButton = (Button) findViewById(R.id.btn_order_cancel);
	qrcodeImageView = (ImageView) findViewById(R.id.showzxing);
	
}


void init(){
	timebtn.setOnClickListener(this);
	okButton.setOnClickListener(this);
	cancelButton.setOnClickListener(this);
	gas_num.addTextChangedListener(mtextwatcher);
	total_price.addTextChangedListener(priceWatcher);
	
}

@SuppressLint("SimpleDateFormat")
private void SetText() {
	
	// TODO Auto-generated method stub
	Intent intent = new Intent(this,orderinfo.class);
	
     usertext.setText(getIntent().getStringExtra("username"));
     SimpleDateFormat    formatter1    =   new    SimpleDateFormat ("yyyy-MM-dd_HH:mm");    
     Date    curDate    =   new    Date(System.currentTimeMillis());  
          
     timeView.setText(formatter1.format(curDate));
     initEndDateTime =formatter1.format(curDate);
     gas_station.setText(getIntent().getStringExtra("station_name")+"-"+getIntent().getStringExtra("station_brand"));
     address.setText(getIntent().getStringExtra("Addr"));
     s =getIntent().getParcelableExtra("station");
     final PriceListAdapter mAdapter = new PriceListAdapter(order.this, s.getGastPriceList());
     gas_type.setAdapter(mAdapter);
     gas_type.setOnItemSelectedListener(new OnItemSelectedListener() {
     
    	 
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
		infoString = gas_type.getSelectedItem().toString();
			splitString  = infoString.split(" "); 
			
			ogastypeString  =splitString[0];
		  price = Double.parseDouble(splitString[1]);
		  
		  
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	});
 
}
TextWatcher mtextwatcher = new TextWatcher() {
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		total_price.removeTextChangedListener(priceWatcher);
		sum = price * Double.parseDouble(s.toString());
		DecimalFormat formater = new DecimalFormat();  
	    //保留几位小数  
	       formater.setMaximumFractionDigits(2);  
		total_price.setText((formater.format((sum))));
		dozing();
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		total_price.addTextChangedListener(priceWatcher);
	}
};


TextWatcher priceWatcher = new TextWatcher() {
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		gas_num.removeTextChangedListener(mtextwatcher);
		num = Double.parseDouble(s.toString())/price;
		DecimalFormat formater = new DecimalFormat();  
	    //保留几位小数  
	       formater.setMaximumFractionDigits(2);  
		 gas_num.setText(formater.format((num)));
		 dozing();
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		gas_num.addTextChangedListener(mtextwatcher);
	}
};



void dozing(){
	 ousername = usertext.getText().toString().trim();
	  otimeString  = timeView.getText().toString().trim();
	  ostationString  = gas_station.getText().toString().trim();
	  oaddress  = address.getText().toString().trim();
	 ordernum = usertext.getText().toString().trim()+"::"+timeView.getText().toString().trim();
	 ototalprice = total_price.getText().toString().trim();
	 ogasnum = gas_num.getText().toString().trim();
	try {
		
		 content = otimeString+"|"+ostationString+"|"+ogastypeString+"|"+ogasnum+"|"+ordernum+"|"+ousername+"|"+oaddress+"|"+ototalprice;
			Bitmap qrcodeBitmap = EncodingHandler.createQRCode(content, 400);
			qrcodeImageView.setImageBitmap(qrcodeBitmap);
		
		//生成二维码图片，第一个参数是二维码的内容，第二个参数是正方形图片的边长，单位是像素
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

int orderin(String ousername, String otime ,String ostation ,String ogastype, String ototalprice ,String oaddress ,String ordernum,String ogasnum){
   int ret= 0;
	HttpPost httpPost = new HttpPost(serverUrl);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("username", ousername));
	params.add(new BasicNameValuePair("time", otime));
	params.add(new BasicNameValuePair("station", ostation));
	params.add(new BasicNameValuePair("gas_type", ogastype));
	params.add(new BasicNameValuePair("total_price", ototalprice));
	params.add(new BasicNameValuePair("address", oaddress));
	params.add(new BasicNameValuePair("order_num", ordernum));
	params.add(new BasicNameValuePair("gas_num", ogasnum));
	HttpClient client = new DefaultHttpClient();
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
			 ret =  json.getInt("order_code");
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

@SuppressLint("SimpleDateFormat")
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.btn_order_ok:
		
		  
		int i = orderin(ousername, otimeString, ostationString, ogastypeString, ototalprice, oaddress, ordernum, ogasnum) ;
		if (i==1) {
			Toast.makeText(order.this, "订单提交成功", Toast.LENGTH_SHORT).show();
            finish();
			
		}else {
			Toast.makeText(order.this, "订单提交失败", Toast.LENGTH_SHORT).show();
		}
		break;
	case R.id.time_btn:
		DateTimePicker dateTimePicKDialog = new DateTimePicker(
				order.this, initEndDateTime);
		dateTimePicKDialog.dateTimePicKDialog(timeView);
		break;
	case R.id.btn_order_cancel:
		finish();
			 break;
			
		 
	default:
		break;
		
	}
}
}
