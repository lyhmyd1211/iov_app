package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class Platelist extends HttpServlet{

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
    String nameString = req.getParameter("username").trim();
	String result = "null";
	result = db.displayplate(DBManager.TABLE_CARINFO, DBManager.COLUMN_LICENSE_PLATE_NUM,DBManager.COLUMN_USERNAME,nameString);
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
