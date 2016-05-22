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


	//������ά��ͼƬ,���ؽ����װ��Result������
	private com.google.zxing.Result  parseQRcodeBitmap(String bitmapPath){
		//����ת������UTF-8
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		//��ȡ����������ͼƬ
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		//������ǰ�inJustDecodeBounds��Ϊtrue����ôBitmapFactory.decodeFile(String path, Options opt)
		//��������ķ���һ��Bitmap���㣬������������Ŀ���ȡ��������
		options.inJustDecodeBounds = true;
		//��ʱ��bitmap��null����δ���֮��options.outWidth �� options.outHeight����������Ҫ�Ŀ�͸���
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath,options);
		//����������ȡ������ͼƬ�ı߳�����ά��ͼƬ�������εģ�����Ϊ400����
		/**
			options.outHeight = 400;
			options.outWidth = 400;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(bitmapPath, options);
		*/
		//����������������Ȼ��bitmap�޶���������Ҫ�Ĵ�С�����ǲ�û�н�Լ�ڴ棬���Ҫ��Լ�ڴ棬���ǻ���Ҫʹ��inSimpleSize�������
		options.inSampleSize = options.outHeight / 400;
		if(options.inSampleSize <= 0){
			options.inSampleSize = 1; //��ֹ��ֵС�ڻ����0
		}
		/**
		 * ������Լ�ڴ�����
		 * 
		 * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // Ĭ����Bitmap.Config.ARGB_8888
		 * options.inPurgeable = true; 
		 * options.inInputShareable = true; 
		 */
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, options); 
		//�½�һ��RGBLuminanceSource���󣬽�bitmapͼƬ�����˶���
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
		//��ͼƬת���ɶ�����ͼƬ
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
		//��ʼ����������
		QRCodeReader reader = new QRCodeReader();
		//��ʼ����
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
					//��ȡ���û�ѡ��Ķ�ά��ͼƬ�ľ���·��
					imgPath = cursor.getString(columnIndex);
				}
				cursor.close();
				
				//��ȡ�������
				Result ret = parseQRcodeBitmap(imgPath);
				//Toast.makeText(Binding.this,"���������" + ret.toString(), Toast.LENGTH_LONG).show();
				
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
								//д�����ݿ�;
								int ret = 0;
								ret = dobinding(imforStrings[0], imforStrings[1], imforStrings[2], imforStrings[3],
										imforStrings[4], imforStrings[5], imforStrings[6], imforStrings[7], imforStrings[8], imforStrings[9], imforStrings[10],user);
								if(ret == 0)
								{
									Toast.makeText(Binding.this, "��ʧ��,������!", Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(Binding.this, "�󶨳ɹ�!", Toast.LENGTH_LONG).show();
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
					Toast.makeText(Binding.this, "����Դ��ʽ����ȷ�������쳣,����", Toast.LENGTH_LONG).show();
				}
			
				break;
			case PHOTO_PIC:
				String result = data.getExtras().getString("result");
			//	Toast.makeText(Binding.this,"���������" + result, Toast.LENGTH_LONG).show();
				
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
								//д�����ݿ�;
								int ret = 0;
								ret = dobinding(imforStrings[0], imforStrings[1], imforStrings[2], imforStrings[3],
										imforStrings[4], imforStrings[5], imforStrings[6], imforStrings[7], imforStrings[8], imforStrings[9], imforStrings[10],user);
								if(ret == 0)
								{
									Toast.makeText(Binding.this, "��ʧ��,������!", Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(Binding.this, "�󶨳ɹ�!", Toast.LENGTH_LONG).show();
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
					Toast.makeText(Binding.this, "����Դ��ʽ����ȷ�������쳣,����", Toast.LENGTH_LONG).show();
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
	alertDialog = new AlertDialog.Builder(this).setNegativeButton("ȡ��", null)
	.setPositiveButton("ȷ��", null).create();
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
	alertDialog.setTitle("ȷ�ϰ󶨳�����Ϣ!");
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
		Toast.makeText(Binding.this, "���������쳣", Toast.LENGTH_LONG).show();
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
			//��ת��ͼƬѡ�����ȥѡ��һ�Ŷ�ά��ͼƬ
			Intent intent1 = new Intent();

			intent1.setAction(Intent.ACTION_PICK);
			
			intent1.setType("image/*");
			
			Intent intent2 =  Intent.createChooser(intent1, "ѡ���ά��ͼƬ");
			startActivityForResult(intent2, CHOOSE_PIC);
			break;
		case R.id.scanner:
			//��ת�����ս���ɨ���ά��
			Intent intent3 = new Intent(Binding.this, com.zxing.activity.CaptureActivity.class);
			startActivityForResult(intent3, PHOTO_PIC);
			break;

		default:
			break;
		}

	}


	
}
