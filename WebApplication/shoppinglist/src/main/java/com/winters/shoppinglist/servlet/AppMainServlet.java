/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.winters.shoppinglist.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.winters.shoppinglist.db.dao.Dao;
import com.winters.shoppinglist.db.entity.Entry;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AppMainServlet extends HttpServlet
{
    @EJB
    Dao dao;

    String user = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) 
        {
          JsonArrayBuilder jsonList = Json.createArrayBuilder();
          
          List<Entry> listOfEntries = dao.getAllEntries();
          for(Entry e : listOfEntries)
          {
            JsonObjectBuilder jsonEntry = Json.createObjectBuilder()
            .add("id",e.getId())
            .add("name",e.getName())
            .add("date",e.getDate())
            .add("amount",e.getAmount())
            .add("store",e.getStore())
            .add("user",e.getReqUser())
            .add("notes",e.getNotes());

            jsonList.add(jsonEntry);
          }

          Map<String, Boolean> config = new HashMap<String, Boolean>();
          config.put(JsonGenerator.PRETTY_PRINTING, true);
          JsonWriterFactory wf = Json.createWriterFactory(config);
          Writer w = new StringWriter();
          wf.createWriter(w).write(jsonList.build());

          out.print(w.toString());

          //out.println(jsonList.build());
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

