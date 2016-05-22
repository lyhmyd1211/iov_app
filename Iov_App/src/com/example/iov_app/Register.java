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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity{
public static final String serverUrl = "http://10.80.182.140:8080/Iov/register";
//10.0.2.2
//10.163.219.59
TextView conTextView;
EditText username ;
EditText password ;
EditText repassword ;
EditText realname;
CheckBox showBox;
CheckBox showReBox;
Button ensure ;
Button cancel ;
@SuppressLint("NewApi")
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.register);
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
			detectDiskWrites().detectNetwork().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
			detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
			conTextView= (TextView) findViewById(R.id.content1);
			username = (EditText) findViewById(R.id.regs_username);
			password = (EditText) findViewById(R.id.regs_password);
			repassword = (EditText) findViewById(R.id.regs_repassword);
			realname = (EditText) findViewById(R.id.regs_real_name);
			showBox = (CheckBox) findViewById(R.id.show_password);
			showReBox = (CheckBox) findViewById(R.id.show_repassword);
			ensure = (Button) findViewById(R.id.btn_ensure);
			cancel = (Button) findViewById(R.id.btn_cancel);
			showBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					 if(isChecked)
					 {
						 password.setInputType(0x90);
					 }else {
						password.setInputType(0x81);
					}
				}
			});
			showReBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					 if (isChecked) {
							repassword.setInputType(0x90);
						}else {
							repassword.setInputType(0x81);
						}
				}
			});
			
			ensure.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 String Username = username.getText().toString().trim();
	    			 String Password = password.getText().toString().trim();
	    			 String Repassword = repassword.getText().toString().trim();
	    			 String Realname = realname.getText().toString().trim();
	    			if(Username.equals(""))
	    			{
	    				Toast.makeText(Register.this, "请输入用户名!" ,Toast.LENGTH_LONG).show();
	    			}else
	    			 if(Password.equals(Repassword))
	    			 {
	    				int retr = doRegister(Username, Password, Repassword,Realname);
	    		if ( retr== 1) {
	    			Toast.makeText(Register.this, "注册成功,自动登录", Toast.LENGTH_LONG).show(); 	
	                Intent intent = new Intent(Register.this, UserInfo.class);
	                intent.putExtra("username", Username);
	         		startActivity(intent);		
				}else {
					conTextView.setText("用户名已存在,请更换!");
				}
	    			 }
	    			 else {
						Toast.makeText(Register.this, "两次密码不一致,请确认后再试", Toast.LENGTH_LONG).show();
					}
					
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
}
@SuppressWarnings("deprecation")
private int doRegister(final String username, final String password,final String repassword, final String realname) {
	        int ret = 0;
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(serverUrl);
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 
			params.add(new BasicNameValuePair("username", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("realname", realname));
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
@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	super.onDestroy();
	username.setText("");
	password.setText("");
	repassword.setText("");
	conTextView.setText("");
	}
}