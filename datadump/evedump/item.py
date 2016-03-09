
def deunicode(str):
   str = str.replace(u"\u2018","'")
   str = str.replace(u"\u2019","'")
   str = str.replace(u"\u2013"," - ")
   str = str.replace(u"\u201c",'"')
   str = str.replace(u"\u201d",'"')
   return str


class InventoryDA:
  def __init__(self,dbc):
    self.__dbc = dbc
    self.__items = {}
    self.__categories = {}
    self.__groups = {}
    

  def get_item(self,iid):
    if iid in self.__items:
      return self.__items[iid]
    else:
      item = Item(self.__dbc,iid)
      self.__items[iid] = item
      return item
    
class Item:
  def __init__(self,dbc,iid):
    if iid < 0:
      raise Exception
    query = "\
      SELECT \
        typeName, \
        volume, \
        groupID, \
        IFNULL(marketGroupID,-1),  \
        IFNULL(invMetaTypes.metaGroupID,1) \
      FROM \
        invTypes \
      LEFT OUTER JOIN \
        invMetaTypes \
        ON invMetaTypes.typeID = invTypes.typeID \
      WHERE \
        invTypes.typeID = %i \
      " % (iid)
    dbc.execute(query)
    table = dbc.fetchall()
    if len(table) < 1:
      self.id = -1
      print("Unknown item: %i" % (iid))
      return
    row = table[0]
    self.id = iid
    self.name = deunicode(str(row[0]))
    self.volume = float(row[1])
    self.group = int(row[2])
    self.market_group = int(row[3])
    self.meta_group = 14 if self.group in [954,955,956,957,958,963,1305] else int(row[4])
    #                 Fix T3 subsystems meta group.

class ItemStack:
  def __init__(self,itemid,amount):
    self.itemid = int(itemid)
    self.amount = int(amount)

class Group:
  def __init__(self,dbc,gid):
    query = "\
      SELECT \
        groupName, \
        categoryID \
      FROM \
        invGroups \
      WHERE \
        groupID = %i \
    " % (gid)
    dbc.execute(query)
    table = dbc.fetchall()
    row = table[0]
    self.id = gid
    self.name = deunicode(str(row[0]))
    self.category = int(row[1])

class Category:
  def __init__(self,dbc,cid):
    query = "\
      SELECT \
        categoryName \
      FROM \
        invCategories \
      WHERE \
        categoryID = %i \
    " % (cid)
    dbc.execute(query)
    table = dbc.fetchall()
    row = table[0]
    self.id = cid
    self.name = deunicode(str(row[0]))

class MetaGroup:
  def __init__(self,dbc,mgid):
    query = "\
      SELECT \
        metaGroupName \
      FROM \
        invMetaGroups \
      WHERE \
        metaGroupID = %i \
    " % (mgid)
    dbc.execute(query)
    table = dbc.fetchall()
    row = table[0]
    self.id = mgid
    self.name = deunicode(str(row[0]))

