package com.example.iov_app;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker.OnTimeChangedListener;

import com.example.iov_app.R;


public class DateTimePicker implements OnDateChangedListener,
OnTimeChangedListener {
private DatePicker datePicker;
private TimePicker timePicker;
private AlertDialog ad;
private String dateTime;
private String initDateTime;
private Activity activity;


public DateTimePicker(Activity activity, String initDateTime) {
this.activity = activity;
this.initDateTime = initDateTime;

}

public void init(DatePicker datePicker, TimePicker timePicker) {
Calendar calendar = Calendar.getInstance();
if (!(null == initDateTime || "".equals(initDateTime))) {
	calendar = this.getCalendarByInintData(initDateTime);
} else {
	initDateTime = calendar.get(Calendar.YEAR) + "-"
			+ calendar.get(Calendar.MONTH) + "-"
			+ calendar.get(Calendar.DAY_OF_MONTH) + "_"
			+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
			+ calendar.get(Calendar.MINUTE);
}

datePicker.init(calendar.get(Calendar.YEAR),
		calendar.get(Calendar.MONTH),
		calendar.get(Calendar.DAY_OF_MONTH), this);
timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
}


@SuppressLint("InflateParams")
public AlertDialog dateTimePicKDialog(final TextView timeView) {
LinearLayout dateTimeLayout = (LinearLayout) activity
		.getLayoutInflater().inflate(R.layout.common_datetime, null);
datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
resizePikcer(datePicker);
resizePikcer(timePicker);
init(datePicker, timePicker);
timePicker.setIs24HourView(true);
timePicker.setOnTimeChangedListener(this);

ad = new AlertDialog.Builder(activity)
		.setTitle(initDateTime)
		.setView(dateTimeLayout)
		.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			public void onClick(DialogInterface dialog, int whichButton) {
				timeView.setText(dateTime);
				SimpleDateFormat    formatter2    =   new    SimpleDateFormat ("yyyy-MM-dd_HH:mm");    
				 Date    curDate    =   new    Date(System.currentTimeMillis());  
				 Calendar c1= Calendar.getInstance();
				 Calendar c2 = Calendar.getInstance();
				 try
				 {
				 c1.setTime(formatter2.parse(formatter2.format(curDate)));
				 c2.setTime(formatter2.parse(timeView.getText().toString()));
				 }catch(java.text.ParseException e){
				 System.err.println("��ʽ����ȷ");
				 }
				 int result=c1.compareTo(c2);
				System.out.println("result"+result);
					  if(result>0)
					  {
						  timeView.setText(formatter2.format(curDate));
						  Toast.makeText(activity, "��������ȷʱ��,�ڵ�ǰʱ��֮��", Toast.LENGTH_SHORT).show();
					  }
			}
		})
		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		}).show();

onDateChanged(null, 0, 0, 0);
return ad;
}

public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
onDateChanged(null, 0, 0, 0);
}

@SuppressLint("SimpleDateFormat")
public void onDateChanged(DatePicker view, int year, int monthOfYear,
	int dayOfMonth) {
// �������ʵ��
Calendar calendar = Calendar.getInstance();

calendar.set(datePicker.getYear(), datePicker.getMonth(),
		datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
		timePicker.getCurrentMinute());
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

dateTime = sdf.format(calendar.getTime());
ad.setTitle(dateTime);
}


private Calendar getCalendarByInintData(String initDateTime) {
Calendar calendar = Calendar.getInstance();

// 
String date = spliteString(initDateTime, "_", "index", "front"); // ����
String time = spliteString(initDateTime, "_", "index", "back"); // ʱ��

String yearStr = spliteString(date, "-", "index", "front"); // ���
String monthAndDay = spliteString(date, "-", "index", "back"); // ����

String monthStr = spliteString(monthAndDay, "-", "index", "front"); // ��
String dayStr = spliteString(monthAndDay, "-", "index", "back"); // ��

String hourStr = spliteString(time, ":", "index", "front"); // ʱ
String minuteStr = spliteString(time, ":", "index", "back"); // ��

int currentYear = Integer.valueOf(yearStr.trim()).intValue();
int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
int currentDay = Integer.valueOf(dayStr.trim()).intValue();
int currentHour = Integer.valueOf(hourStr.trim()).intValue();
int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

calendar.set(currentYear, currentMonth, currentDay, currentHour,
		currentMinute);
return calendar;
}


public static String spliteString(String srcStr, String pattern,
	String indexOrLast, String frontOrBack) {
String result = "";
int loc = -1;
if (indexOrLast.equalsIgnoreCase("index")) {
	loc = srcStr.indexOf(pattern); // ȡ���ַ�����һ�γ��ֵ�λ��
} else {
	loc = srcStr.lastIndexOf(pattern); // ���һ��ƥ�䴮��λ��
}
if (frontOrBack.equalsIgnoreCase("front")) {
	if (loc != -1)
		result = srcStr.substring(0, loc); // ��ȡ�Ӵ�
} else {
	if (loc != -1)
		result = srcStr.substring(loc + 1, srcStr.length()); // ��ȡ�Ӵ�
}
return result;
}



//�ı�picker�Ĵ�С	

private void resizePikcer(FrameLayout tp){
List<NumberPicker> npList = findNumberPicker(tp);
for(NumberPicker np:npList){
	resizeNumberPicker(np);
}
}
private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
List<NumberPicker> npList = new ArrayList<NumberPicker>();
View child = null;
if(null != viewGroup){
	for(int i = 0;i<viewGroup.getChildCount();i++){
		child = viewGroup.getChildAt(i);
		if(child instanceof NumberPicker){
			npList.add((NumberPicker)child);
		}
		else if(child instanceof LinearLayout){
			List<NumberPicker> result = findNumberPicker((ViewGroup)child);
			if(result.size()>0){
				return result;
			}
		}
	}
}
return npList;
}
private void resizeNumberPicker(NumberPicker np){
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, LayoutParams.WRAP_CONTENT);
params.setMargins(20, 0, 20, 0);
np.setLayoutParams(params);
}

}
