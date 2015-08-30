from tsl.tslwriter import *

class IndexGroup:
  def __init__(self,gid,name):
    self.gid = int(gid)
    self.name = str(name)

class IndexEntry:
  def __init__(self,item,gid):
    self.item = int(item)
    self.gid = int(gid)


class Index:
  def __init__(self):
    self.__entries = []
    self.__groups = {}

  def add_group(self,gid,name):
    self.__groups[gid] = IndexGroup(gid,name)

  def add_entry(self,item,gid):
    if not gid in self.__groups.keys():
      raise ValueError(("Group ID %i is not in index.") % gid)
    self.__entries.append(IndexEntry(item,gid))

  def sort_entries(self,func):
    self.__entries = sorted(self.__entries,key=func)
 

  def write_to_file(self,filename):
    tslfile = TSLWriter(filename)
    tslfile.start_collection("index")
    for i in self.__groups:
      group = self.__groups[i]
      tslfile.start_collection("group")
      tslfile.push_formatter()
      tslfile.formatter.set_newline(False)
      tslfile.put_value("id",group.gid)
      tslfile.put_value("name",group.name)
      tslfile.end_collection()
      tslfile.pop_formatter()
    for entry in self.__entries:
      tslfile.start_collection("item")
      tslfile.push_formatter()
      tslfile.formatter.set_newline(False)
      tslfile.put_value("id",entry.item)
      tslfile.put_value("group",entry.gid)
      tslfile.end_collection()
      tslfile.pop_formatter()
    tslfile.end_collection()
