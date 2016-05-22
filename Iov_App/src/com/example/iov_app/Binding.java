package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
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

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat.OnItemClickListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Binding extends Activity implements OnClickListener {
	public static final String serverUrl = "http://10.80.182.140:8080/Iov/binding";
	
	//10.163.158.24
	HttpClient client = new DefaultHttpClient();
	ArrayList<String> listdata;
	ArrayAdapter<String> maAdapter;
    String user;
    String	 plateString;
    int delete;
	private static final int CHOOSE_PIC = 0;
	private static final int PHOTO_PIC = 1;
	private static final int updatelist = 2;
		
	
	private String  imgPath = null;
	private String[] imforStrings;
	AlertDialog alertDialog;
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
	ListView listView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
				detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		 listdata = new ArrayList<String>();
		 maAdapter	= new ArrayAdapter<String>(Binding.this, android.R.layout.simple_list_item_1,listdata);
		setContentView(R.layout.binding);
		setupViews();
		ResultIn();
		displayplate(user);
		dolist();
	}
	
	private void setupViews() {
		listView = (ListView) findViewById(R.id.listv);
		findViewById(R.id.scanner).setOnClickListener(this);
		findViewById(R.id.pick).setOnClickListener(this);
		
	        listView.setAdapter(maAdapter);
	        Intent	intent =  getIntent();
	        	
			 user = intent.getStringExtra("name");
			
			
	       // setContentView(listView);
	}

void dolist(){
	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
		 plateString  =	parent.getItemAtPosition(position).toString();
		        Intent  intent = new Intent(Binding.this,bindinginfo.class);
		     //   Toast.makeText(Binding.this, plateString, Toast.LENGTH_SHORT).show();
		        intent.putExtra("plate", plateString);
		        intent.putExtra("position", position);
			startActivityForResult(intent, updatelist);
		}
	});


	}


	//解析二维码图片,返回结果封装在Result对象中
	private com.google.zxing.Result  parseQRcodeBitmap(String bitmapPath){
		//解析转换类型UTF-8
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		//获取到待解析的图片
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		//如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
		//并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
		options.inJustDecodeBounds = true;
		//此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath,options);
		//我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
		/**
			options.outHeight = 400;
			options.outWidth = 400;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		*/
		//以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
		options.inSampleSize = options.outHeight / 400;
		if(options.inSampleSize <= 0){
			options.inSampleSize = 1; //防止其值小于或等于0
		}
		/**
		 * 辅助节约内存设置
		 * 
		 * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
		 * options.inPurgeable = true; 
		 * options.inInputShareable = true; 
		 */
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, options); 
		//新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
		//将图片转换成二进制图片
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
		//初始化解析对象
		QRCodeReader reader = new QRCodeReader();
		//开始解析
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		imgPath = null;
	
		
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			  
			case updatelist:
				delete = data.getExtras().getInt("delete");
				 if (delete==2) {
						listdata.remove(data.getExtras().getInt("theposition"));
						maAdapter.notifyDataSetChanged();
					}
			
			case CHOOSE_PIC:
				String[] proj = new String[]{MediaStore.Images.Media.DATA};
				Cursor cursor = Binding.this.getContentResolver().query(data.getData(), proj, null, null, null);
				
				if(cursor.moveToFirst()){
					int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
					System.out.println(columnIndex);
					//获取到用户选择的二维码图片的绝对路径
					imgPath = cursor.getString(columnIndex);
				}
				cursor.close();
				
				//获取解析结果
				Result ret = parseQRcodeBitmap(imgPath);
				//Toast.makeText(Binding.this,"解析结果：" + ret.toString(), Toast.LENGTH_LONG).show();
				
				 imforStrings
				 = ret.toString().split("\\|");
				 try {
					
					 brandtv.setText(imforStrings[0]);
					 logoView.setImageResource(Integer.parseInt(imforStrings[1].replaceAll("^0[x|X]", ""),16));
					 modeltv.setText(imforStrings[2]);
					 plateNumtv.setText(imforStrings[3]);
					 engineNumtv.setText(imforStrings[4]);
					 leveltv.setText(imforStrings[5]);
					 kilometerstv.setText(imforStrings[6]);
					 gaslinetv.setText(imforStrings[7]);
					 engineperformtv.setText(imforStrings[8]);
					 transperformtv.setText(imforStrings[9]);
					 lightingtv.setText(imforStrings[10]);
						alertDialog.show();
						alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//写入数据库;
								int ret = 0;
								ret = dobinding(imforStrings[0], imforStrings[1], imforStrings[2], imforStrings[3],
										imforStrings[4], imforStrings[5], imforStrings[6], imforStrings[7], imforStrings[8], imforStrings[9], imforStrings[10],user);
								if(ret == 0)
								{
									Toast.makeText(Binding.this, "绑定失败,请重试!", Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(Binding.this, "绑定成功!", Toast.LENGTH_LONG).show();
									alertDialog.dismiss();
									listdata.add(imforStrings[3]);
									
									maAdapter.notifyDataSetChanged();
									 
								}
								
							}
						});
						alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
					
				} catch (NumberFormatException e) {
					// TODO: handle exception
					Toast.makeText(Binding.this, "数据源格式不正确或数据异常,请检查", Toast.LENGTH_LONG).show();
				}
			
				break;
			case PHOTO_PIC:
				String result = data.getExtras().getString("result");
			//	Toast.makeText(Binding.this,"解析结果：" + result, Toast.LENGTH_LONG).show();
				
				 imforStrings = result.split("\\|");
				 try {
					
					 brandtv.setText(imforStrings[0]);
					 logoView.setImageResource(Integer.parseInt(imforStrings[1].replaceAll("^0[x|X]", ""),16));
					 modeltv.setText(imforStrings[2]);
					 plateNumtv.setText(imforStrings[3]);
					 engineNumtv.setText(imforStrings[4]);
					 leveltv.setText(imforStrings[5]);
					 kilometerstv.setText(imforStrings[6]);
					 gaslinetv.setText(imforStrings[7]);
					 engineperformtv.setText(imforStrings[8]);
					 transperformtv.setText(imforStrings[9]);
					 lightingtv.setText(imforStrings[10]);
					 alertDialog.show();
						alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//写入数据库;
								int ret = 0;
								ret = dobinding(imforStrings[0], imforStrings[1], imforStrings[2], imforStrings[3],
										imforStrings[4], imforStrings[5], imforStrings[6], imforStrings[7], imforStrings[8], imforStrings[9], imforStrings[10],user);
								if(ret == 0)
								{
									Toast.makeText(Binding.this, "绑定失败,请重试!", Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(Binding.this, "绑定成功!", Toast.LENGTH_LONG).show();
									alertDialog.dismiss();
									listdata.add(imforStrings[3]);
									
									maAdapter.notifyDataSetChanged();
								}
								
							}
						});
						alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();
							}
						});
				} catch (NumberFormatException e) {
					// TODO: handle exception
					Toast.makeText(Binding.this, "数据源格式不正确或数据异常,请检查", Toast.LENGTH_LONG).show();
				}
					
					break;
			
			default:
				break;
			}
		}
		
	}

	
	

void  ResultIn(){
	LayoutInflater inflater = getLayoutInflater();
	View mlayout = inflater.inflate(R.layout.resultin,null);
	alertDialog = new AlertDialog.Builder(this).setNegativeButton("取消", null)
	.setPositiveButton("确定", null).create();
	brandtv = (TextView) mlayout.findViewById(R.id.brand_perform);
	logoView = (ImageView) mlayout.findViewById(R.id.logo_perform);
	modeltv = (TextView) mlayout.findViewById(R.id.model_perform);
	plateNumtv = (TextView) mlayout.findViewById(R.id.plate_num_perform);
	engineNumtv = (TextView) mlayout.findViewById(R.id.engine_num_perform);
	leveltv = (TextView) mlayout.findViewById(R.id.level_perform);
	kilometerstv = (TextView) mlayout.findViewById(R.id.kilometers_perform);
	gaslinetv  = (TextView) mlayout.findViewById(R.id.gasline_perform);
	engineperformtv = (TextView) mlayout.findViewById(R.id.engine_per_perform);
	transperformtv = (TextView) mlayout.findViewById(R.id.transmission_per_perform);
	lightingtv = (TextView) mlayout.findViewById(R.id.lighting_perform);
	alertDialog.setTitle("确认绑定车辆信息!");
	alertDialog.setView(mlayout);
	alertDialog.setCancelable(false);
	
}

private int dobinding(final String brand, final String logo,final String model, final String plateNum,final String engineNum,


		final String level,final String kilometers,final String gasline,final String engineperform,final String transperform,final String lighting,final String user) {
    int ret = 0;
	
	HttpPost httpPost = new HttpPost(serverUrl);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("brand", brand));
	params.add(new BasicNameValuePair("logo", logo));
	params.add(new BasicNameValuePair("model", model));
	params.add(new BasicNameValuePair("plateNum", plateNum));
	params.add(new BasicNameValuePair("engineNum", engineNum));
	params.add(new BasicNameValuePair("level", level));
	params.add(new BasicNameValuePair("kilometers", kilometers));
	params.add(new BasicNameValuePair("gasline", gasline));
	params.add(new BasicNameValuePair("engineperform", engineperform));
	params.add(new BasicNameValuePair("transperform", transperform));
	params.add(new BasicNameValuePair("lighting", lighting));
	params.add(new BasicNameValuePair("operation", "binding"));
	params.add(new BasicNameValuePair("user", user));
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
private void displayplate(String user){
	HttpPost httpPost = new HttpPost(serverUrl);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("operation", "display"));
	params.add(new BasicNameValuePair("user", user));
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
			JSONObject jsonObject = new JSONObject(jsonString);
			//jsonObject.getJSONArray(jsonString);
			JSONArray array =  jsonObject.optJSONArray("license_plate_num");
			if (array!=null) {
				
				for (int i = 0; i <array.length() ; i++) {
					
						
					listdata.add(array.getString(i));
					
			}
				HashSet set  =   new  HashSet();
			      List newList  =   new  ArrayList();
			   for  (Iterator iter  =  listdata.iterator(); iter.hasNext();)   {
			         Object element  =  iter.next();
			         if  (set.add(element))
			            newList.add(element);
			     } 
			   listdata.clear();
			   listdata.addAll(newList);
			   if (listdata.size()!=0) {				
					 maAdapter.notifyDataSetChanged();
				}
						
			}
		 
		}
		
		
	} catch (UnsupportedEncodingException e) {
		Log.d("lu", "UnsupportedEncodingException");
		e.printStackTrace();
	} catch (ClientProtocolException e) {
		Log.d("lu", "ClientProtocolException");
		e.printStackTrace();
	} catch (IOException e) {
		Log.d("lu", "IOException");
		//unbindService(connection);  
	//	timer.cancel(); 
	//	mHandler = null;
		Toast.makeText(Binding.this, "网络连接异常", Toast.LENGTH_LONG).show();
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

}



	@SuppressLint("InlinedApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.pick:
			//跳转到图片选择界面去选择一张二维码图片
			Intent intent1 = new Intent();

			intent1.setAction(Intent.ACTION_PICK);
			
			intent1.setType("image/*");
			
			Intent intent2 =  Intent.createChooser(intent1, "选择二维码图片");
			startActivityForResult(intent2, CHOOSE_PIC);
			break;
		case R.id.scanner:
			//跳转到拍照界面扫描二维码
			Intent intent3 = new Intent(Binding.this, com.zxing.activity.CaptureActivity.class);
			startActivityForResult(intent3, PHOTO_PIC);
			break;

		default:
			break;
		}

	}


	
}
