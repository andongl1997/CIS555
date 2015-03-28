package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.UserDA;

public class RegisterServlet extends ApplicationServlet {
	
	@Override
	  public void init() throws ServletException {
	    super.init();
	    DBWrapper.setupDirectory("database");
	  }
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("user");
        String pwd = request.getParameter("pwd");
        String pwd_confirm = request.getParameter("pwd_confirm");
		response.setContentType("text/html");
		PrintWriter writer;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {
			username = URLDecoder.decode(username, "utf-8").trim();
			pwd = URLDecoder.decode(pwd, "utf-8").trim();
			pwd_confirm = URLDecoder.decode(pwd_confirm, "utf-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		if (!checkLogin(request)) {
			if (UserDA.getEntry(username) != null) {
				printErrorPage(writer, "Username exists.");
				return;
			}
			if (!pwd.equals(pwd_confirm)) {
				printErrorPage(writer, "two passwords not the same.");
				return;
			}
			User user = new User(username, pwd);
			UserDA.putEntry(user);
		} else {
			printWelcomePage(writer);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		PrintWriter writer;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (checkLogin(request)) {
			printWelcomePage(writer);	
		} else {
			printRegisterPage(writer);
		}
			
	}
	
	private void printErrorPage(PrintWriter writer, String error) {
		writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Welcome</title>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println(error);
        writer.println("</body>");
        writer.println("</html>");
        writer.close();
	}
	private void printRegisterPage(PrintWriter writer) {
		writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Register Page</title>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("<h2> Xiaobin Chen </h2>");
        writer.println("<h2> SEAS: xiaobinc </h2>");
        writer.println("Register Page.");
        writer.println("<form action=\"register\" method=\"post\">");
        writer.println("Username: <input type=\"text\" name=\"user\"><br>");
        writer.println("Password: <input type=\"password\" name=\"pwd\"><br>");
        writer.println("Password: <input type=\"password\" name=\"pwd_confirm\"><br>");
        writer.println("<input type=\"submit\" value=\"Login\">");
        writer.println("</form>");
        writer.println("</body>");
        writer.println("</html>");
		writer.close();	
	}
}
