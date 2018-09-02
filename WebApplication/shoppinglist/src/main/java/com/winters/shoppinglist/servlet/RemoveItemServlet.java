/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.winters.shoppinglist.servlet;

import com.winters.shoppinglist.db.dao.Dao;
import com.winters.shoppinglist.db.entity.Entry;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jamie
 */
public class RemoveItemServlet extends HttpServlet
{
  @EJB
  Dao dao;

  String user = null;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) 
        {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>RemoveItemsServlet</title>");            
            out.println("<meta http-equiv=\"Refresh\" content=\"0;url=" + request.getContextPath() + "/MainServlet?user=" + request.getParameter("user") + "\">");
            out.println("</head>");
            out.println("<body>");
            out.println("processing request ...");
            /*
            out.println("<ul>");
            Enumeration<String> params = request.getParameterNames();
            while(params.hasMoreElements())
            {
                String param = params.nextElement();
                out.println("<li>" + param + "</li>");
                String[] values = request.getParameterValues(param);
                if(values != null && values.length != 0 && !values[0].isEmpty())
                {
                    out.println("<ul>");
                    for(int i=0; i<values.length; i++)
                    {
                        out.println("<li>" + values[i] + "</li>");
                    }
                    out.println("</ul>");
                }
            }
            out.println("</ul>");
            */
            
            List<Integer> toDelete = new ArrayList<Integer>();
            Enumeration<String> args = request.getParameterNames();
            while(args.hasMoreElements())
            {
              String param = args.nextElement();
              if(param.startsWith("d"))
              {
                String[] values = request.getParameterValues(param);
                if(values != null && values.length != 0 && values[0].equals("t"))
                {
                  toDelete.add(Integer.parseInt(param.substring(1)));
                }
              }
            }

            dao.deleteEntries(toDelete);

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo()
    {
        return "GetList";
    }        
}
