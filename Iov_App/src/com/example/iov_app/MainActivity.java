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











import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements OnClickListener{
	  public static final String serverUrl = "http://10.80.182.140:8080/Iov/login";  
	    //10.0.2.2
	    
	  //10.163.219.59
    private	   Button login ;
	private    Button register ;
	
	 private    EditText nameEdit ;  
	 private    EditText codeEdit ;  
	 private MediaPlayer mediaPlayer;
	 private SurfaceView surfaceview;
	 private AlertDialog alertDialog;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 Intent intent = new Intent(this,MusicService.class);
		 startService(intent);
		 bindService(intent, conn, Context.BIND_AUTO_CREATE);
	    findViewById();
        initView();
        logindialog();
 } 
	protected void findViewById() {
		// TODO Auto-generated method stub
		surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
		login = (Button)findViewById(R.id.btn_login);  
		register = (Button) findViewById(R.id.btn_register);
		

	}
	protected void initView() {
		// TODO Auto-generated method stub
		mediaPlayer = new MediaPlayer();
		surfaceview.getHolder().setKeepScreenOn(true);
		surfaceview.getHolder().addCallback(new SurfaceViewLis());
		login.setOnClickListener(this);
		register.setOnClickListener(this);
	}
	protected void logindialog() {
		LayoutInflater inflater = getLayoutInflater();
		View mylayout = inflater.inflate(R.layout.login,null);
		alertDialog = new AlertDialog.Builder(this).setNegativeButton("取消", null)
		.setPositiveButton("确定", null).create();
		
		nameEdit = (EditText)mylayout.findViewById(R.id.edit_name);  
		codeEdit = (EditText)mylayout.findViewById(R.id.edit_code);  
		alertDialog.setTitle("请登录");
		alertDialog.setView(mylayout);
	
	}
 @Override  
 public boolean onCreateOptionsMenu(Menu menu) {  
     // Inflate the menu; this adds items to the action bar if it is present.  
     getMenuInflater().inflate(R.menu.main, menu);  
     return true;  
 }  
 public class SubmitAsyncTask extends AsyncTask<String, Void, String>{  
     String info = "";  
     ProgressDialog pdDialog;
 public    SubmitAsyncTask(Context context){
	 pdDialog = new ProgressDialog(context,0);
	 pdDialog.setMessage("正在登录,稍安勿躁");
	 pdDialog.show();
 }
     @Override  
     protected String doInBackground(String... params) {  
         // TODO Auto-generated method stub  
         String url = params[0];  
         String reps = "";  
             reps = handleLogin(url);
         return reps;  
     }  

     protected void onPostExecute(String result) {  
         // TODO Auto-generated method stub  
    	 String username = nameEdit.getText().toString().trim();
        String res = result.trim();
        System.out.println(res);
         if(res.equals("0")){  
             info = "验证通过!";
             
            
             pdDialog.dismiss();
           
             	Intent intent = new Intent(MainActivity.this, UserInfo.class);
             	intent.putExtra("username", username);
         		startActivity(intent);		
				
         
         }else if(res.equals("1")){  
             info = "密码错误,注意区分大小写!";  
             pdDialog.dismiss();
         }else if(res.equals("2")){  
             info = "用户不存在,请确认后再试!";  
             pdDialog.dismiss();
         }else if(res.equals("-1")){  
             info = "网络连接错误!";  
             pdDialog.dismiss();
         }  
         
       
         Toast.makeText(MainActivity.this, info, Toast.LENGTH_LONG).show();
         super.onPostExecute(result);  
     }  
       


 }  

 private String handleLogin(String url) {
		String username = nameEdit.getText().toString().trim();
		String password = codeEdit.getText().toString().trim();
		
		
		return login(username, password, url);
	}
	private String login(final String username, final String password,final String url) {
		String str = "-1";
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>(); 
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password));
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
						int ret =  json.getInt("result_code");
						Log.d("lu", "ret = " + ret);
						 str = Integer.toString(ret).trim();
						return str;
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
					Log.d("lu", "IOException");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return str;
			}
	private class SurfaceViewLis implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			
				try {
					play(); 
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}

	}

	public void play() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException { 
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		AssetFileDescriptor fd = this.getAssets().openFd("start1.mp4");
		mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
				fd.getLength());
		mediaPlayer.setLooping(true);
		mediaPlayer.setDisplay(surfaceview.getHolder());
		// 通过异步的方式装载媒体资源
		mediaPlayer.prepareAsync();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				// 装载完毕回调
				mediaPlayer.start();
			}
		}); 
	}
	@Override
	  public void onClick(View v) {  
          // TODO Auto-generated method stub  
          switch (v.getId()) {
		case R.id.btn_login:
			alertDialog.show();
			
			alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new SubmitAsyncTask(MainActivity.this).execute(serverUrl);  
					
				}
			});
			break;

		case R.id.btn_register:
			Intent intent = new Intent(MainActivity.this,Register.class);
			startActivity(intent);
			break;
		}
         
      }
	 final ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
         
	   
	    };
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		nameEdit.setText("");
		codeEdit.setText("");
		 unbindService(conn);
		 mediaPlayer.stop();
		 mediaPlayer.release();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 unbindService(conn);
		 mediaPlayer.stop();
	}
}

