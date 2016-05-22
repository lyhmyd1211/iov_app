package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class orderinfo extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
	// TODO Auto-generated method stub
	resp.setContentType("text/html; charset=UTF-8" );
	resp.setCharacterEncoding("UTF-8");
	req.setCharacterEncoding("UTF-8");
	DataOutputStream dos = new DataOutputStream(resp.getOutputStream());
    DBManager db = new DBManager();
    
    String operation = req.getParameter("operation").trim();
	String result = "null";
	if (operation.equals("display")) {
		String ordernum = req.getParameter("orderinfo").trim();
		result = db.displayorder(DBManager.TABLE_ORDERINFO, DBManager.COLUMN_ORDER_NUM, ordernum);
		System.out.println("result = " + result);
	}else if (operation.equals("delete")) {
		String ordernum = req.getParameter("dorder").trim();
		result = db.dodeleteorder(DBManager.TABLE_ORDERINFO, DBManager.COLUMN_ORDER_NUM, ordernum);
	}
	{
		
	}
	
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
