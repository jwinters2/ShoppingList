/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.winters.shoppinglist.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "ShoppingList")
//@IdClass(EntryKey.class)
@NamedQueries
({
    @NamedQuery(name  = "getAll",
                query = "select e from Entry e"),
    @NamedQuery(name  = "getByStore",
                query = "select e from Entry e where e.store = :store"),
    @NamedQuery(name  = "getByUser",
                query = "select e from Entry e where e.reqUser = :user"),
    @NamedQuery(name  = "getByNameAndStore",
                query = "select e from Entry e where e.name = :name and e.store = :store"),
})
public class Entry implements Serializable
{
    @Transient
    Logger logger = LogManager.getLogger(Entry.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "reqDate", length = 100)
    private String reqDate;
    
    @Column(name = "amount", length = 100)
    private String amount;
    
    @Column(name = "store", length = 100)
    private String store;
    
    @Column(name = "reqUser", length = 20)
    private String reqUser;
    
    @Column(name = "notes", length = 200)
    private String notes;

    @Transient
    private LocalDateTime formattedReqDate;

    public String getName()
		{
        return name;
    }

    public int getId() 
    {
      return id;
    }

    public void setId(int id) 
    {
      this.id = id;
    }

    public void setName(String name)
		{
        this.name = (name == null || name.isEmpty()) ? null : name;
    }

    public String getReqDate()
		{
        return reqDate;
    }

    public void setReqDate(String reqDate)
		{
        this.reqDate = reqDate;
    }

    public String getAmount() 
    {
        return amount == null ? "" : amount;
    }

    public void setAmount(String amount) 
    {
        this.amount = (amount == null || amount.isEmpty()) ? null : amount;
    }

    public String getStore() 
    {
        if(store.isEmpty())
        {
          return "";
        }
        return store.substring(0,1).toUpperCase() + store.substring(1);
    }

    public void setStore(String store) 
    {
        this.store = store.toLowerCase();
    }

    public String getReqUser() 
    {
        return reqUser;
    }

    public void setReqUser(String reqUser) 
    {
        this.reqUser = reqUser;
    }

    public String getNotes() 
    {
        return notes == null ? "" : notes;
    }

    public void setNotes(String notes)
    {
        this.notes = (notes == null || notes.isEmpty()) ? null : notes;
    }

    public String getDate()
    {
      if(formattedReqDate == null)
      {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        formattedReqDate = LocalDateTime.parse(reqDate, f);
      }
      DateTimeFormatter f = DateTimeFormatter.ofPattern("LLL d");
      return formattedReqDate.format(f);
    }
    
    @Override
    public String toString() 
    {
        return "Entry{" + "name=" + name + ", reqDate=" + reqDate + ", amount=" + amount + ", store=" + store + ", reqUser=" + reqUser + ", notes=" + notes + ", id=" + id + '}';
    }
}
