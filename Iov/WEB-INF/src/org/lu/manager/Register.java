package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class Register extends HttpServlet{

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
		String username2 = req.getParameter("username").trim();
		String password2 = req.getParameter("password").trim();
		String realname2 = req.getParameter("realname").trim();
			 result = db.doRegister(DBManager.TABLE_USER,DBManager.COLUMN_USERNAME,DBManager.COLUMN_USER_PASSWORD,DBManager.COLUMN_REALNAME,username2,password2,realname2);
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

