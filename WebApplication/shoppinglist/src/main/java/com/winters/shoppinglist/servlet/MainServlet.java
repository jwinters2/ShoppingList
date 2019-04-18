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
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MainServlet extends HttpServlet
{
    @EJB
    Dao dao;

    String user = null;

    final String addItemName   = "<input class=\"addItemField\" type=\"text\" name=\"itemName\" value=\"\" onfocusout=\"detectTab(0)\" autofocus required>";
    final String addItemAmount = "<input class=\"addItemField\" type=\"text\" name=\"itemAmount\" value=\"\" onfocusout=\"detectTab(1)\">";
    final String addItemStore  = "<input class=\"addItemField\" type=\"text\" name=\"itemStore\" onfocusout=\"detectTab(2)\"  required>";
    final String addItemNotes  = "<input class=\"addItemField\" type=\"text\" name=\"itemNotes\" value=\"\" onfocusout=\"detectTab(3)\">";

    final String addItemNameIdFirst = "<input id=\"nameInput";
    final String addItemNameIdLast = "\" class=\"addItemField\" type=\"text\" name=\"itemAmount\" value=\"\" onfocusout=\"detectTab(1)\" required>";

    final String addItemStorePrefillBegin = "<input class=\"addItemField\" type=\"text\" name=\"itemStore\" value=\"";
    final String addItemNotesPrefillEnd   = "\" onfocusout=\"detectTab(2)\"  required>";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>MainServlet</title>");            
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/style.css\" />");
            out.println("<link rel=\"icon\" type=\"image/png\" href=\"icon.png\" />");

            out.println("<script>");

            out.println("function toggleActive(id)");
            out.println("{");
            out.println("  var input = \"input\" + id;");
            out.println("  console.log(id + \", \" + input);");
            out.println("  if(document.getElementById(input).value == \"t\")");
            out.println("  {" );
            out.println("    console.log(\"enabling row \" + id);");
            out.println("    document.getElementById(input).value = \"f\";");
            out.println("    document.getElementById(id).style.color = \"#000\";");
            out.println("  }");
            out.println("  else");
            out.println("  {");
            out.println("    console.log(\"disabling row \" + id);");
            out.println("    document.getElementById(input).value = \"t\";");
            out.println("    document.getElementById(id).style.color = \"#aaa\";");
            out.println("  }");
            out.println("}");


            out.println("function filter()");
            out.println("{");
            out.println("  var selectMenu = document.getElementById(\"filterMenu\");");
            out.println("  var index = selectMenu.selectedIndex;");
            out.println("  var storeName = selectMenu.options[index].value.toLowerCase();");

            out.println("  var rows = document.getElementById(\"mainTable\").rows;");
            out.println("  for(var i=1; i<rows.length; i++)");
            out.println("  {");
            out.println("    var rowStore = rows[i].cells[3].innerHTML.toLowerCase();");
            out.println("    console.log(\"rowStore = \" + rowStore + \", storeName = \" + storeName);");
            out.println("    console.log(rowStore.localeCompare(storeName));"
                      + "    console.log(rowStore.localeCompare(\"Any\"));"
                      + "    console.log(rowStore.length);");
            out.println("    if(storeName.localeCompare(\"any\") == 0"
                      + "    || rowStore.localeCompare(storeName) == 0"
                      + "    || rowStore.localeCompare(\"any\") == 0)");
            out.println("    {");
            out.println("      rows[i].style.display = \"table-row\";");
            out.println("    }");
            out.println("    else");
            out.println("    {");
            out.println("      rows[i].style.display = \"none\";");
            out.println("    }");
            out.println("  }");
            out.println("}");

            out.println("var lastEntryLostFocus = -1;");
            out.println("var newEntryID = 0;");

            out.println("function detectTab(entry)");
            out.println("{");
            out.println("  if(lastEntryLostFocus != entry)");
            out.println("  {");
            out.println("    lastEntryLostFocus = entry;");
            out.println("  }");
            out.println("}");

            out.println("function onFocusAddNew()");
            out.println("{");
            out.println("  if(lastEntryLostFocus == 3)");
            out.println("  {");
            out.println("     document.getElementById(\"AddItemSubmitButton\").submit();");
            out.println("  }");
            out.println("}");

            out.println("</script>");

            out.println("</head>");
            out.println("<body>");
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

            user = request.getParameter("user");

            out.println("<div class=\"tableDiv\">");
            out.println("<table id=\"mainTable\">");
            out.println("<tr>");
            out.println("<th>Item *</th>");
            out.println("<th>Date</th>");
            out.println("<th>Amount</th>");
            out.println("<th>Store *</th>");
            out.println("<th>Requested by</th>");
            out.println("<th>Notes</th>");
            out.println("</tr>");
            List<Entry> listOfEntries = dao.getAllEntries();
            int count = 0;
            // if this isn't an add-item page, make a form to toggle/delete items
            if(!Boolean.parseBoolean(request.getParameter("addItem")))
            {
              out.println("<form action=\"RemoveItemServlet\">");
              out.println("<input type=\"hidden\" name=\"user\" value=\"" + user + "\">");
            }
            for(Entry e : listOfEntries)
            {
                int id = e.getId();
                out.println("<input type=\"hidden\" id=\"input" + id + "\" name=\"d" + id + "\" value=\"f\">");
                out.println("<tr id=\"" + id + "\" onclick=\"toggleActive(" + id + ")\">");
                out.println("<td>" + e.getName() + "</td>");
                out.println("<td>" + e.getDate() + "</td>");
                out.println("<td>" + e.getAmount() + "</td>");
                out.println("<td>" + e.getStore() + "</td>");
                out.println("<td>" + e.getReqUser() + "</td>");
                out.println("<td>" + e.getNotes() + "</td>");
                out.println("</tr>");
            }
            if(Boolean.parseBoolean(request.getParameter("addItem")))
            {
                out.println("<form id=\"AddItemSubmitButton\" action=\"AddItemServlet\">");
                out.println("<input type=\"hidden\" name=\"user\" value=\"" + user + "\">");
                out.println("<tr>");
                out.println("<td> " + addItemName + " </td>");
                LocalDateTime now = LocalDateTime.now();
                out.println("<td>" + now.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + now.getDayOfMonth()+ "</td>");
                out.println("<td> " + addItemAmount + " </td>");
                String ls = request.getParameter("lastStore");
                out.println("<td> " + addItemStorePrefillBegin + (ls == null ? "" : ls) + addItemNotesPrefillEnd + " </td>");
                out.println("<td>" + request.getParameter("user") + "</td>");
                out.println("<td> " + addItemNotes + " </td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</div>");
            
            // modification buttons
            if(Boolean.parseBoolean(request.getParameter("addItem")))
            {
                out.println("<div class=\"tableDiv\">");
                out.println("<input class=\"submitButton\" type=\"submit\" value=\"Confirm Add\" onfocusin=\"onFocusAddNew()\">");
                out.println("<a href=\"MainServlet?user=" + request.getParameter("user") + "\">Back</a>");
                out.println("</div>");
            }
            else
            {
              out.println("<div class=\"tableDiv\">");

              out.println("<select class=\"submitButton\" id=\"filterMenu\" onchange=\"filter()\">");
              out.println("<option value=\"any\">Any</option>");
              List<String> storeList = dao.getListOfStores();
              for(String s : storeList)
              {
                String sCap = s;
                if(!s.isEmpty()) 
                {
                  sCap = s.substring(0,1).toUpperCase() + s.substring(1);
                }
                out.println("<option value=\"" + s + "\">" + sCap + "</option>");
              }
              out.println("</select>");

              if(user != null)
              {
                out.println("<a href=\"MainServlet?user=" + user + "&addItem=true\">Add Item</a>");
                out.println("<input class=\"submitButton\" type=\"submit\" value=\"Remove Items\">");
                out.println("<a href=\"app-release.apk\">Download App</a>");
              }
              out.println("</div>");
            }

            // closes both possible forms
            out.println("</form>");

            out.println("<div class=\"tableDiv\">");
            out.println("<div class=\"req\">* Required Fields</div>");
            out.println("</div>");

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

