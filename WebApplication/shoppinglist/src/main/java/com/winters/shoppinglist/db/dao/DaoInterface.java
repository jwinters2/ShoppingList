package com.winters.shoppinglist.db.dao;

import com.winters.shoppinglist.db.entity.Entry;
import java.util.List;

interface DaoInterface
{
  public List<Entry> getAllEntries();
  public int addItem(Entry entry);
  public void deleteEntries(List<Integer> toDelete);
}
