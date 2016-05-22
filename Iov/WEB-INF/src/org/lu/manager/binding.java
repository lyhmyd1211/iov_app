package org.lu.manager;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lu.Db.DBManager;

public class binding extends HttpServlet{

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
	String result = "null";
	String result2 = "null";
	String operation = req.getParameter("operation").trim();
	String username = req.getParameter("user").trim();
	
	if(operation.equals("binding"))
	{
		String brand = req.getParameter("brand").trim();
		String logo = req.getParameter("logo").trim();
		String model = req.getParameter("model").trim();
		String plateNum = req.getParameter("plateNum").trim();
		String engineNum = req.getParameter("engineNum").trim();
		String level = req.getParameter("level").trim();
		String kilometers = req.getParameter("kilometers").trim();
		String gasline = req.getParameter("gasline").trim();
		String engineperform = req.getParameter("engineperform").trim();
		String transperform = req.getParameter("transperform").trim();
		String lighting = req.getParameter("lighting").trim();
		 result = db.dobindcar(DBManager.TABLE_CARINFO,DBManager.COLUMN_AUTOBRAND,DBManager.COLUMN_AUTOLOGO,DBManager.COLUMN_AUTOMODEL,
				 DBManager.COLUMN_LICENSE_PLATE_NUM,DBManager.COLUMN_ENGINE_NUM,DBManager.COLUMN_AUTOLEVEL,DBManager.COLUMN_KILOMETERS,
				 DBManager.COLUMN_GASLINE,
				 DBManager.COLUMN_ENGINE_PERFORM,DBManager.COLUMN_TRANSMISSION_PERFORM,DBManager.COLUMN_AUTO_LIGHTING,DBManager.COLUMN_USERNAME,
				 brand, logo, model, plateNum, engineNum, level, kilometers, gasline, engineperform, transperform, lighting,username);
	}
	
	result2 = db.displayplate(DBManager.TABLE_CARINFO, DBManager.COLUMN_LICENSE_PLATE_NUM,DBManager.COLUMN_USERNAME,username);
	System.out.println("result = " + result);
	System.out.println("result2 = " + result2);
	dos.writeUTF(result);
	dos.writeUTF(result2);
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
