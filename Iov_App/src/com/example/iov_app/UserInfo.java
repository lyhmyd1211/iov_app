package com.example.iov_app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import com.baidu.android.bbalbs.common.a.c;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.deahu.weizhang.R.color;
import com.example.iov_app.R;
import com.juhe.petrolstation.bean.Petrol;
import com.juhe.petrolstation.bean.Station;
import com.juhe.petrolstation.util.StationData;

import android.R.array;
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint({ "NewApi", "HandlerLeak" })
public class UserInfo extends Activity implements OnClickListener{
public static final String serverUrl = "http://10.80.182.140:8080/Iov/plate";
public static final String serverUrlorder = "http://10.80.182.140:8080/Iov/orderlist";
private long mExitTime = 0;
public Car_Service.MyBinder mBinder;

AlertDialog alertDialog;
String nobind = "请选择当前车辆";
ArrayList<String> aList = new ArrayList<String>();
ArrayList<String> bList = new ArrayList<String>();
ArrayAdapter<String> sadapter;
ArrayAdapter<String> oadapter;
//{add(nobind);}};

int delete = 0;
String plate_num = null ;
Intent startIntent;
ImageView drawerbtn,alertimage;
Button bindbtn;	
Button checkbtn;	
TextView musicbtn;
Button cancelbtn;
TextView usernametext;
Spinner current_car;
ListView orderlist;
ProgressBar gasBar;
TextView gaslinetView;
TextView kilometersView;
TextView engineView;
TextView transView;
TextView lighting;
DrawerLayout mdrawerLayout;

Intent musicintent;
String name;
Runnable runnable;
Handler handler;
private final Timer timer = new Timer();  
private TimerTask task;  
private Context mContext;
private MapView mMapView = null;
private BaiduMap mBaiduMap = null;
private LocationClient mLocationClient = null;
private BDLocationListener mListener = new MyLocationListener();

private ImageView  iv_loc;
private Toast mToast;
private TextView tv_title_right, tv_name, tv_distance, tv_price_a, tv_price_b;
private LinearLayout ll_summary;
private Dialog selectDialog, loadingDialog;
private StationData stationData;
private BDLocation loc;

private ArrayList<Station> mList;
private Station mStation = null;

private int mDistance = 3000;
private Marker lastMarker = null;

	

@SuppressLint("NewApi")
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.user_info);
	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
			detectDiskWrites().detectNetwork().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
			detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
			 musicintent = new Intent(UserInfo.this,MusicService.class);
			  startService(musicintent);
			mContext = this;
			stationData = new StationData(mHandler);
	findviewbyid();
	initbutton();
	aList.add(nobind);
	Intent intent = getIntent();
	name = intent.getStringExtra("username");
	usernametext.setText(name);
	displayplate();
	do_spinner();

	displayorder();
	do_listview();
	opendrawer();
	
	  
			
		

	
}
void findviewbyid(){
	bindbtn = (Button) findViewById(R.id.bind);
	checkbtn = (Button) findViewById(R.id.check);
	musicbtn =  (TextView) findViewById(R.id.music);
	cancelbtn =  (Button) findViewById(R.id.cancel);
	usernametext = (TextView) findViewById(R.id.username_perform);
	current_car = (Spinner) findViewById(R.id.current_car);
	gasBar = (ProgressBar) findViewById(R.id.progress_horizontal);
	gaslinetView = (TextView) findViewById(R.id.gasline_show);
	kilometersView = (TextView) findViewById(R.id.kilometers);
	engineView = (TextView) findViewById(R.id.engine_per);
	transView = (TextView) findViewById(R.id.transmission);
	lighting = (TextView) findViewById(R.id.lighting_perform);
	drawerbtn = (ImageView) findViewById(R.id.iv_drawer);
	orderlist = (ListView) findViewById(R.id.listorder);
	mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	
	
	iv_loc = (ImageView) findViewById(R.id.iv_loc);
	iv_loc.setOnClickListener(this);
	tv_title_right = (TextView) findViewById(R.id.tv_title_button);
	tv_title_right.setText("3km" + " >");
	tv_title_right.setVisibility(View.VISIBLE);
	tv_title_right.setOnClickListener(this);

	ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
	ll_summary.setOnClickListener(this);
	tv_name = (TextView) findViewById(R.id.tv_name);
	tv_distance = (TextView) findViewById(R.id.tv_distance);
	tv_price_a = (TextView) findViewById(R.id.tv_price_a);
	tv_price_b = (TextView) findViewById(R.id.tv_price_b);

	mMapView = (MapView) findViewById(R.id.bmapView);
	mMapView.showScaleControl(false);
	mMapView.showZoomControls(false);
	mBaiduMap = mMapView.getMap();

	mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
	mBaiduMap.setMyLocationEnabled(true);

	mLocationClient = new LocationClient(mContext);
	mLocationClient.registerLocationListener(mListener);

	LocationClientOption option = new LocationClientOption();
	option.setLocationMode(LocationMode.Hight_Accuracy);// 高精度;
														// Battery_Saving:低精度.
	option.setCoorType("bd09ll"); // 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09
									// 返回百度经纬度坐标系 ：bd09ll
	option.setScanSpan(0);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
	option.setIsNeedAddress(true);// 设置是否需要地址信息，默认为无地址
	option.setNeedDeviceDirect(true);// 在网络定位时，是否需要设备方向
	mLocationClient.setLocOption(option);

	
}

void initbutton(){
	bindbtn.setOnClickListener(this);
	checkbtn.setOnClickListener(this);
	musicbtn.setOnClickListener(this);
	cancelbtn.setOnClickListener(this);

	
}

void do_spinner(){
	
	sadapter= new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,aList);
	
		current_car.setAdapter(sadapter);
		current_car.setSelection(0, true);
	current_car.setOnItemSelectedListener(new OnItemSelectedListener() {
   
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			plate_num=  parent.getItemAtPosition(position).toString();
			// TODO Auto-generated method stub
			   if (position==0)
			    {
			return;
			    }
			if (mBinder!=null) {
				stopService(startIntent);
				unbindService(connection); 
			}
			
			if (null!=plate_num||""!=plate_num) {
				
				mservice();
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
	}
	});
	
}

void do_listview(){
	oadapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_expandable_list_item_1,bList);
	orderlist.setAdapter(oadapter);
	orderlist.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			 Intent intent = new Intent(mContext,orderinfo.class);
			 intent.putExtra("order_num", bList.get(position));
			 intent.putExtra("name", name);
			 intent.putExtra("position", position);
			 startActivityForResult(intent, 1);
			
		}
	});
}
@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK) {
	 delete	=	data.getExtras().getInt("delete");
	 if (delete==1) {
			bList.remove(data.getExtras().getInt("theposition"));
			oadapter.notifyDataSetChanged();
		}
	
		}
	}

void opendrawer(){
	drawerbtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mdrawerLayout.openDrawer(Gravity.LEFT);
		}
	});
}
public void setMarker(ArrayList<Station> list) {
	View view = LayoutInflater.from(mContext).inflate(R.layout.marker, null);
	final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
	for (int i = 0; i < list.size(); i++) {
		Station s = list.get(i);
		tv.setText((i + 1) + "");
		if (i == 0) {
			tv.setBackgroundResource(R.drawable.icon_focus_mark);
		} else {
			tv.setBackgroundResource(R.drawable.icon_mark);
		}
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
		LatLng latLng = new LatLng(s.getLat(), s.getLon());
		Bundle b = new Bundle();
		b.putParcelable("s", list.get(i));
		OverlayOptions oo = new MarkerOptions().position(latLng).icon(bitmap).title((i + 1) + "").extraInfo(b);
		if (i == 0) {
			lastMarker = (Marker) mBaiduMap.addOverlay(oo);
			mStation = s;
			showLayoutInfo((i + 1) + "", mStation);
		} else {
			mBaiduMap.addOverlay(oo);
		}
	}

	mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			// TODO Auto-generated method stub
			if (lastMarker != null) {
				tv.setText(lastMarker.getTitle());
				tv.setBackgroundResource(R.drawable.icon_mark);
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
				lastMarker.setIcon(bitmap);
			}
			lastMarker = marker;
			String position = marker.getTitle();
			tv.setText(position);
			tv.setBackgroundResource(R.drawable.icon_focus_mark);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
			marker.setIcon(bitmap);
			mStation = marker.getExtraInfo().getParcelable("s");
			showLayoutInfo(position, mStation);
			return false;
		}
	});

}

Handler mHandler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0x01:
			mList = (ArrayList<Station>) msg.obj;
			setMarker(mList);
			loadingDialog.dismiss();
			break;
		case 0x02:
			loadingDialog.dismiss();
			showToast(String.valueOf(msg.obj));
			break;
		default:
			break;
		}

	}

};

public void showLayoutInfo(String position, Station s) {
	tv_name.setText(position + "." + s.getName());
	tv_distance.setText(s.getDistance() + "");
	List<Petrol> list = s.getGastPriceList();

	if (list != null && list.size() > 0) {
		tv_price_a.setText(list.get(0).getType() + " " + list.get(0).getPrice());
		if (list.size() > 1) {
			tv_price_b.setText(list.get(1).getType() + " " + list.get(1).getPrice());
		}
	}
	ll_summary.setVisibility(View.VISIBLE);
}

public void searchStation(double lat, double lon, int distance) {
	showLoadingDialog();
	mBaiduMap.clear();
	ll_summary.setVisibility(View.GONE);
	stationData.getStationData(lat, lon, distance);
}

public class MyLocationListener implements BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if (location == null) {
			return;
		}
		loc = location;
		MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		searchStation(location.getLatitude(), location.getLongitude(), mDistance);
	}

}

/**
 * dialog点击事件
 * 
 * @param v
 *            点击的view
 */
public void onDialogClick(View v) {
	switch (v.getId()) {
	case R.id.bt_3km:
		distanceSearch("3km >", 3000);
		break;
	case R.id.bt_5km:
		distanceSearch("5km >", 5000);
		break;
	case R.id.bt_8km:
		distanceSearch("8km >", 8000);
		break;
	case R.id.bt_10km:
		distanceSearch("10km >", 10000);
		break;
	default:
		break;
	}
}

/**
 * 根据distance,获取当前位置附近的加油站
 * @param text
 * @param distance
 */
public void distanceSearch(String text, int distance) {
	mDistance = distance;
	tv_title_right.setText(text);
	searchStation(loc.getLatitude(), loc.getLongitude(), distance);
	selectDialog.dismiss();
}




/**
 * 显示范围选择dialog
 */
@SuppressLint("InflateParams")
private void showSelectDialog() {
	if (selectDialog != null) {
		selectDialog.show();
		return;
	}
	selectDialog = new Dialog(mContext, R.style.dialog);
	View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_distance, null);
	selectDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
	selectDialog.setCanceledOnTouchOutside(true);
	selectDialog.show();
}

@SuppressLint("InflateParams")
private void showLoadingDialog() {
	if (loadingDialog != null) {
		loadingDialog.show();
		return;
	}
	loadingDialog = new Dialog(mContext, R.style.dialog_loading);
	View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
	loadingDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
	loadingDialog.setCancelable(false);
	loadingDialog.show();
}

/**
 * 显示通知
 * 
 * @param msg
 */
private void showToast(String msg) {
	if (mToast == null) {
		mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
	}
	mToast.setText(msg);
	mToast.show();
}

@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		displayplate();
		displayorder();
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	mMapView.onResume();
	mLocationClient.start();
	displayplate();
	displayorder();
	
}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	mMapView.onPause();
	mLocationClient.stop();
	 
}
@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
      sadapter.notifyDataSetChanged();
      oadapter.notifyDataSetChanged();
   	
	}

	
public void mservice(){
	 startIntent = new Intent(this, Car_Service.class);  
		startService(startIntent);  
	
    Intent bindIntent = new Intent(this, Car_Service.class);  
 
		
    	bindIntent.putExtra("which_car", plate_num);
	
    bindService(bindIntent, connection, BIND_AUTO_CREATE);  
    

}
private ServiceConnection connection = new ServiceConnection() {  
	public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
		mBinder = (Car_Service.MyBinder) service;
	
		 handler = new Handler() {  
		    @Override  
		    public void handleMessage(Message msg) {  
		        // TODO Auto-generated method stub  
	
		        // TODO Auto-generated method stub  
		        //要做的事情 
		    	Log.d("LongRunningService", "executed at " + new Date().toString());
				mBinder.update();
				gasBar.setProgress(Integer.parseInt(mBinder.gasline));
				gaslinetView.setText(mBinder.gasline);
				gaslinetView.setTextColor(Color.parseColor("#000000"));
				kilometersView.setText(mBinder.kilometers);
				kilometersView.setTextColor(Color.parseColor("#000000"));
				engineView.setText(mBinder.engine);
				engineView.setTextColor(Color.parseColor("#000000"));
				transView.setText(mBinder.trans);
				transView.setTextColor(Color.parseColor("#000000"));
				lighting.setText(mBinder.lighting);
				lighting.setTextColor(Color.parseColor("#000000"));
				if (Integer.parseInt(gaslinetView.getText().toString().trim())<20) {
					gaslinetView.setTextColor(Color.parseColor("#ff0000"));
					Toast.makeText(UserInfo.this, "油量不足"+gaslinetView.getText().toString().trim()+"%了快加油啊!", Toast.LENGTH_SHORT).show();
				}
				if ((Integer.parseInt(kilometersView.getText().toString().trim())>15000)&&(Integer.parseInt(kilometersView.getText().toString().trim())%15000==0)) {
					kilometersView.setTextColor(Color.parseColor("#ff0000"));
					Toast.makeText(UserInfo.this, "跑了15000Km了,快保养保养车吧!", Toast.LENGTH_SHORT).show();
				}
				if (engineView.getText().toString().trim().equals("异常")) {
					engineView.setTextColor(Color.parseColor("#ff0000"));
					Toast.makeText(UserInfo.this, "发动机异常了,快停下来修修!", Toast.LENGTH_SHORT).show();
				}
				if (transView.getText().toString().trim().equals("异常")) {
					transView.setTextColor(Color.parseColor("#ff0000"));
					Toast.makeText(UserInfo.this, "变速器异常了,快停下来修修!", Toast.LENGTH_SHORT).show();
				}
				if (lighting.getText().toString().trim().equals("异常")) {
					lighting.setTextColor(Color.parseColor("#ff0000"));
					Toast.makeText(UserInfo.this, "车灯异常了,快停下来修修!", Toast.LENGTH_SHORT).show();
				}
		        // 要做的事情  
		        super.handleMessage(msg);  
		    }  
		};  
		task = new TimerTask() {  
		    @Override  
		    public void run() {  
		        // TODO Auto-generated method stub  
		        Message message = new Message();  
		        message.what = 3;  
		        handler.sendMessage(message);  
		    }  
		    
		};   
		timer.schedule(task, 3000, 3000);   
	};
	
	public void onServiceDisconnected(android.content.ComponentName name) {};
};

private void displayplate(){
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(serverUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("username", name));
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
						
							
							aList.add(array.getString(i));
						
				}
					HashSet set  =   new  HashSet();
				      List newList  =   new  ArrayList();
				   for  (Iterator iter  =  aList.iterator(); iter.hasNext();)   {
				         Object element  =  iter.next();
				         if  (set.add(element))
				            newList.add(element);
				     } 
				     aList.clear();
				     aList.addAll(newList);
				   
				     
							
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
			unbindService(connection);  
			timer.cancel(); 
			mHandler = null;
			Toast.makeText(UserInfo.this, "网络连接异常", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
}

private void displayorder(){
	HttpClient client = new DefaultHttpClient();
	HttpPost httpPost = new HttpPost(serverUrlorder);
	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	params.add(new BasicNameValuePair("username", name));
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
			JSONArray array =  jsonObject.optJSONArray("order_num");
			if (array!=null) {
				
				for (int i = 0; i <array.length() ; i++) {
					
						
						bList.add(array.getString(i));
					
			}
				HashSet set  =   new  HashSet();
			      List newList  =   new  ArrayList();
			   for  (Iterator iter  =  bList.iterator(); iter.hasNext();)   {
			         Object element  =  iter.next();
			         if  (set.add(element))
			            newList.add(element);
			     } 
			     bList.clear();
			     bList.addAll(newList);
			  
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
		unbindService(connection);  
		timer.cancel(); 
		mHandler = null;
		Toast.makeText(UserInfo.this, "网络连接异常", Toast.LENGTH_LONG).show();
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
	case R.id.bind:
		 Intent bindintent = new Intent(UserInfo.this,Binding.class);
		 bindintent.putExtra("name", name);
		
		 startActivity(bindintent);
		
		break;
	case R.id.music:
		Intent  musicIntent = new Intent(UserInfo.this, musicActivity.class);
		startActivity(musicIntent);
		break;
	case R.id.check:
		 Intent checkintent = new Intent(UserInfo.this,com.deahu.activity.MainActivity.class);
		 startActivity(checkintent);
		 break;
	case R.id.cancel:
		 finish();
		 break;
	
	case R.id.iv_loc:
		int r = mLocationClient.requestLocation();
		switch (r) {
		case 1:
			showToast("服务没有启动。");
			break;
		case 2:
			showToast("没有监听函数。");
			break;
		case 6:
			showToast("请求间隔过短。");
			break;

		default:
			break;
		}

		break;
	case R.id.tv_title_button:
		showSelectDialog();
		break;
	case R.id.ll_summary:
		Intent infoIntent = new Intent(mContext, StationInfoActivity.class);
		infoIntent.putExtra("s", mStation);
		infoIntent.putExtra("locLat", loc.getLatitude());
		infoIntent.putExtra("locLon", loc.getLongitude());
		infoIntent.putExtra("username", name);
		startActivity(infoIntent);
		break;
	default:
		break;
	}
}


@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();

            } else {
                    finish();
            }
            return true;
    }
    return super.onKeyDown(keyCode, event);
}
@Override
protected void onDestroy() {
	super.onDestroy();
	if (mBinder!=null) {
		stopService(startIntent);
		unbindService(connection); 
	}
	stopService(musicintent);
	timer.cancel();  
	mMapView.onDestroy();
	mHandler = null;
}
}
