package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class Login extends HttpServlet{

	private static final long serialVersionUID = 1L;
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
	// TODO Auto-generated method stub
	
	resp.setContentType("text/html; charset=UTF-8" );
	req.setCharacterEncoding("UTF-8");
	resp.setCharacterEncoding("UTF-8");
	DataOutputStream dos = new DataOutputStream(resp.getOutputStream());
    DBManager db = new DBManager();
	String result = "null";
	String username2 = req.getParameter("username").trim();
	String password2 = req.getParameter("password").trim();
		 result = db.doLogin(username2, password2,DBManager.TABLE_USER,DBManager.COLUMN_USERNAME,DBManager.COLUMN_USER_PASSWORD);
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
