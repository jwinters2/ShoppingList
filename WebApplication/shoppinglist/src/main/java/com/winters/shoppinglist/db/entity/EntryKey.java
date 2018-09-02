/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.winters.shoppinglist.db.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author jamie
 */
public class EntryKey implements Serializable
{
  protected String name;
  protected String store;

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || !(o instanceof EntryKey)) return false;

    EntryKey other = (EntryKey) o;

    return name.equals(other.name) && store.equals(other.store);
  }

  @Override
  public int hashCode() 
  {
    int hash = 5;
    hash = 13 * hash + Objects.hashCode(this.name);
    hash = 13 * hash + Objects.hashCode(this.store);
    return hash;
  }
}
