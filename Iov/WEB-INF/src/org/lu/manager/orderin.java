package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class orderin extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html; charset=UTF-8" );
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		DataOutputStream dos = new DataOutputStream(resp.getOutputStream());
	    DBManager db = new DBManager();
		String result = "null";
		String username = req.getParameter("username").trim();
		String time = req.getParameter("time").trim();
		String station = req.getParameter("station").trim();
		String gastype = req.getParameter("gas_type").trim();
		String  totalprice = req.getParameter("total_price").trim();
		String address = req.getParameter("address").trim();
		String ordernum = req.getParameter("order_num").trim();
		String gasnum = req.getParameter("gas_num").trim();
	
	
			result = db.doorderin(DBManager.TABLE_ORDERINFO, DBManager.COLUMN_ORDER_TIME, DBManager.COLUMN_GAS_STATION, DBManager.COLUMN_GAS_TYPE,
					DBManager.COLUMN_GAS_NUM, DBManager.COLUMN_ORDER_NUM, DBManager.COLUMN_USERNAME, DBManager.COLUMN_ADDR, DBManager.COLUMN_TOTALPRICE, 
					time, station, gastype, gasnum, ordernum, username, address, totalprice);
		
		System.out.println("result = " + result);
		dos.writeUTF(result);
		dos.flush();
		dos.close();
		
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}
	

}
