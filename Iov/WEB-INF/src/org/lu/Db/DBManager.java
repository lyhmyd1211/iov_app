package org.lu.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Object;

import com.google.gson.Gson;
public class DBManager {
	
	public final static String TABLE_USER = "user_login";
	public final static String COLUMN_USERNAME = "user_name";
	public final static String COLUMN_USER_PASSWORD = "user_password";
	public final static String TBALE_USERINFO = "binding_car";
	public final static String COLUMN_REALNAME = "real_name";

	public final static String TABLE_CARINFO ="car_info";
    public final static String COLUMN_AUTOBRAND ="auto_brand";
    public final static String COLUMN_AUTOLOGO = "auto_logo";
    public final static String COLUMN_AUTOMODEL ="auto_model";
    public final static String COLUMN_LICENSE_PLATE_NUM ="license_plate_num";
    public final static String COLUMN_ENGINE_NUM = "engine_number";
    public final static String COLUMN_AUTOLEVEL = "auto_level";
    public final static String COLUMN_KILOMETERS = "kilometers";
    public final static String COLUMN_GASLINE = "gasline";
    public final static String COLUMN_ENGINE_PERFORM= "engine_performance";
    public final static String COLUMN_TRANSMISSION_PERFORM = "transmission_performance";
    public final static String COLUMN_AUTO_LIGHTING = "auto_lighting";
    
    public final static String TABLE_ORDERINFO = "order_info";
    public final static String COLUMN_ORDER_TIME = "order_time";
    public final static String COLUMN_ORDER_NUM="order_num";
    public final static String COLUMN_GAS_STATION = "gas_station";
    public final static String COLUMN_GAS_TYPE ="gas_type";
    public final static String COLUMN_GAS_NUM="gas_num";
    public final static String COLUMN_ADDR = "address";
    public final static String COLUMN_TOTALPRICE = "total_price";
  
	ArrayList<String> arrayList = new ArrayList<String>();
	String url = "jdbc:mysql://localhost:3306/lovapp?"
            + "user=root&password=flrshe1211&useUnicode=true&characterEncoding=UTF8";
	String user = "root";
	String password = "flrshe1211";
	
	public   Statement getStatement() {
		Connection connection = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection(url,user,password);
			stmt = connection.createStatement();           
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("加载驱动出错");
			e.printStackTrace();
		}
		
		return stmt;
	}
	public  ResultSet query(String sql) {
		ResultSet rst = null;
		Statement stmt = getStatement();
		System.out.println("stmt = " + stmt);
		try {
			rst = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return rst;
	}
	
	public int update(String sql) {
		Statement stmt = getStatement();
		int result = 0  ;
		try {
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * login_result:
	 * -1：登陆失败，未知错误！
	 * 0: 登陆成功！
	 * 1：登陆失败，用户名或密码错误！
	 * 2：登陆失败，用户名不存在！
	 * */

	public String doLogin(String username2,
			String password2,String tb,String Coname,String  Copassword) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String sql = "select * from " + tb + " where " + Coname + " = " + "'" + username2 + "'" ;
		System.out.println("url = " + sql);
		DBManager db = new DBManager();
		ResultSet rst = db.query(sql);
		try {
			rst.next();
			String pwd = rst.getString(Copassword);
			if(!password2.equals(pwd)) {
				resultMap.put("result_code", 1);
			} else {
				resultMap.put(Coname, rst.getString(Coname));
				resultMap.put(Copassword, rst.getString(Copassword));
				resultMap.put("result_code", 0);
			}
		} catch (SQLException e) {
			resultMap.put("result_code", 2);
			e.printStackTrace();
		}
		
				return (new Gson()).toJson(resultMap);
				
	}
	

		
	public String doRegister(String tb,String Tusername,String  Tpassword,String Tname,
			String pusername,String ppassword,String pname) {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
			String sql = "insert  into " + tb+"("+ Tusername+","+Tpassword+","+Tname+")"
					+"values"+"("+"'"+pusername+"'"+","+"'"+ppassword+"'"+","+"'"+pname+"'"+")";
			System.out.println("url = " + sql);
		int 	num = update(sql);
			try{
				if (num == 0) {
					resultMap.put("result_code", 0);
				}else {
					
				resultMap.put("result_code", 1);
				}
		} catch (RuntimeException e) {
			// TODO: handle exception
			
		}	
		
		return (new Gson()).toJson(resultMap);
		}
	
	public String dobindcar(String tb,String brand,String logo,String model,String plate,
			String engine,String level,String kilometers,String gasline,String engineperform,
			String transperform,String lighting, String user,String pbrand,String plogo,String pmodel,String pplate,
			String pengine,String plevel,String pkilometers,String pgasline,String pengineperform,
			String ptransperform,String plighting,String puser){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		String sql = "insert  into " + tb+"("+ brand+","+logo+","+model+","+plate+","+engine+","+level+","+kilometers+","+gasline+","
		+engineperform+","+transperform+","+lighting+","+user+")"
				+"values"+"("+"'"+pbrand+"'"+","+"'"+plogo+"'"+","+"'"+pmodel+"'"+","+"'"+pplate+"'"+","+"'"+pengine+"'"+","+"'"+plevel+"'"+","+"'"+pkilometers+"'"+","+"'"
		+pgasline+"'"+","+"'"+pengineperform+"'"+","+"'"+ptransperform+"'"+","+"'"+plighting+"'"+","+"'"+puser+"'"+")";
		System.out.println("url = " + sql);
	int 	num = update(sql);
		try{
			if (num == 0) {
				resultMap.put("result_code", 0);
			}else {
				
			resultMap.put("result_code", 1);
			}
	} catch (RuntimeException e) {
		// TODO: handle exception
		
	}	
	
	return (new Gson()).toJson(resultMap);
	}
	
	public String displaybind(String tb,String tplate,String plate){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String bindinformation = "select *"+" from "+tb+" where "+tplate+" = "+"'"+plate+"'";
		System.out.println("url = " + bindinformation);
		DBManager db = new DBManager();
		
		ResultSet rst = db.query(bindinformation);
		
		try {
		 while(rst.next()){
			       
			    resultMap.put("brand", rst.getString(1));
				resultMap.put("logo", rst.getString(2));
				resultMap.put("model", rst.getString(3));
				resultMap.put("engine_num", rst.getString(5));
				resultMap.put("auto_level", rst.getString(6));
				resultMap.put("kilometers", rst.getString(7));
				resultMap.put("gas_line", rst.getString(8));
				resultMap.put("engine_perform", rst.getString(9));
				resultMap.put("trans_perform", rst.getString(10));
				resultMap.put("lighting", rst.getString(11));
				
		  }
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return  (new Gson()).toJson(resultMap);
	}	
	
	
	public String carinfo(String tb,String tplate, String rplate,String gasline,String kilometers,String engine, String trans, String lighting) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String carinformation = "select "+gasline+","+kilometers+","+engine+","+trans+","+lighting+" from "+tb+" where "+tplate+" = "+"'"+rplate+"'";
		System.out.println("url = " + carinformation);
		DBManager db = new DBManager();
		ResultSet rst = db.query(carinformation);
		try {
		  while(rst.next()){
		
			  resultMap.put(gasline,rst.getString(gasline));
			  resultMap.put(kilometers,rst.getString(kilometers));
			  resultMap.put(engine,rst.getString(engine));
			  resultMap.put(trans,rst.getString(trans));
			  resultMap.put(lighting,rst.getString(lighting));
			
			    
			
		  }
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return  (new Gson()).toJson(resultMap);
	}
public String dodelete(String tb,String tplate,String rplate){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	try {
	String sql = "delete from "+tb+" where "+tplate+" = "+"'"+rplate+"'";
	update(sql);
	System.out.println(sql);
	resultMap.put("result_code", 2);
		
	} catch (Exception e) {
		// TODO: handle exception
		resultMap.put("result_code", -1);
	}
	return (new Gson()).toJson(resultMap);
}

public String displayplate(String tb, String  tplate ,String tusername,String username){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	String plateinformation = "select "+tplate+" from "+tb+" where "+tusername+" = "+"'"+username+"'";
	System.out.println("url = " + plateinformation);
	DBManager db = new DBManager();
	
	ResultSet rst = db.query(plateinformation);
	
	try {
	 while(rst.next()){
		       
			arrayList.add(rst.getString(tplate));
			resultMap.put(tplate, arrayList);
	  }
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
	
	return  (new Gson()).toJson(resultMap);
}

public String doorderin(String tb,String tordertime ,String tgasstation, String tgastype, String tgas_num,
		String torder_num,String tuser_name,String taddress, String ttotal_price
		, String pordertimeString ,String pgasstation, String pgastype, String pgas_num ,
		String porder_num, String puser_name, String paddress, String ptotal_price){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	String sql = "insert  into " + tb+"("+ tordertime+","+tgasstation+","+tgastype+","+tgas_num+","
	+torder_num+","+tuser_name+","+taddress+","+ttotal_price+")"
			+"values"+"("+"'"+pordertimeString+"'"+","+"'"+pgasstation+"'"+","+"'"+pgastype+"'"+","+
	"'"+pgas_num+"'"+","+"'"+porder_num+"'"+","+"'"+puser_name+"'"+","+"'"+paddress+"'"+","+"'"
	+ptotal_price+"'"+")";
	System.out.println("url = " + sql);
	int 	num = update(sql);
		try{
			if (num == 0) {
				resultMap.put("order_code", 0);
			}else {
				
			resultMap.put("order_code", 1);
			}
	} catch (RuntimeException e) {
		// TODO: handle exception
		
	}	
	
	return (new Gson()).toJson(resultMap);
	}
public String displayorder(String tb,String torder_num,String order_num){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	String orderinformation = "select *"+" from "+tb+" where "+torder_num+" = "+"'"+order_num+"'";
	System.out.println("url = " + orderinformation);
	DBManager db = new DBManager();
	
	ResultSet rst = db.query(orderinformation);
	
	try {
	 while(rst.next()){
		       
		    resultMap.put("order_time", rst.getString(1));
			resultMap.put("gas_station", rst.getString(2));
			resultMap.put("gas_type", rst.getString(3));
			resultMap.put("gas_num", rst.getString(4));
			resultMap.put("address", rst.getString(7));
			resultMap.put("total_price", rst.getString(8));
			
	  }
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
	
	return  (new Gson()).toJson(resultMap);
}
public String displayordernum(String tb, String  tordernum ,String tusername,String username){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	String ordernuminfo = "select "+tordernum+" from "+tb+" where "+tusername+" = "+"'"+username+"'";
	System.out.println("url = " + ordernuminfo);
	DBManager db = new DBManager();
	
	ResultSet rst = db.query(ordernuminfo);
	
	try {
	 while(rst.next()){
		       
			arrayList.add(rst.getString(tordernum));
			resultMap.put(tordernum, arrayList);
	  }
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
	
	return  (new Gson()).toJson(resultMap);
}
public String dodeleteorder(String tb,String tordernum,String ordernum){
	HashMap<String, Object> resultMap = new HashMap<String, Object>();
	try {
	String sql = "delete from "+tb+" where "+tordernum+" = "+"'"+ordernum+"'";
	update(sql);
	System.out.println(sql);
	resultMap.put("delete_code", 1);
		
	} catch (Exception e) {
		// TODO: handle exception
		resultMap.put("delete_code", 0);
	}
	return (new Gson()).toJson(resultMap);
}


}
