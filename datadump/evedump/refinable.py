import sqlite3
from .item import ItemStack

REFINABLE_CATEGORY_ORE = 0
REFINABLE_CATEGORY_ICE = 1
REFINABLE_CATEGORY_COMPRESSED_ORE = 2
REFINABLE_CATEGORY_COMPRESSED_ICE = 3
REFINABLE_CATEGORY_ALCHEMY = 4

class Refinable:
  def __init__(self,rid,category,batch,skill,products):
    self.rid = int(rid)
    self.category = int(category)
    self.batch = int(batch)
    self.skill = int(skill)
    self.products = products

  def get_refinables(dbc,inventory):
    result = []
    _query_ore(dbc,inventory,result)
    _query_ice(dbc,inventory,result)
    _query_unrefined(dbc,result)
    return result

def _query_ore(dbc,inventory,reflist):
  query = "\
    SELECT \
      invTypes.typeID, \
      invTypes.portionSize \
    FROM \
      invTypes \
    INNER JOIN \
      ( \
        SELECT \
          invMarketGroups.marketGroupID \
        FROM \
          invMarketGroups \
        WHERE \
          parentGroupID = 54 \
      ) AS oreGroups \
    ON invTypes.marketGroupID = oreGroups.marketGroupID \
    ORDER BY invTypes.marketGroupID,invTypes.typeID \
  "
  dbc.execute(query)
  table = dbc.fetchall()
  for row in table:
    rid = row[0]
    cat = REFINABLE_CATEGORY_COMPRESSED_ORE if "Compressed " in inventory.get_item(rid).name else REFINABLE_CATEGORY_ORE
    reflist.append(Refinable(rid,cat,row[1],_query_refinable_skill(dbc,rid),_query_refinable_products(dbc,rid)))

def _query_unrefined(dbc,reflist):
  query = "\
    SELECT \
      invTypes.typeID, \
      invTypes.portionSize \
    FROM \
      invTypes \
    WHERE \
      marketGroupID = 500 AND typeName LIKE 'Unrefined %'\
    ORDER BY typeName \
  "
  dbc.execute(query)
  table = dbc.fetchall()
  for row in table:
    rid = row[0]
    reflist.append(Refinable(rid,REFINABLE_CATEGORY_ALCHEMY,row[1],_query_refinable_skill(dbc,rid),_query_refinable_products(dbc,rid)))


def _query_refinable_skill(dbc,oreid):
  query = "\
    SELECT \
      IFNULL(valueInt,12196) AS skill \
    FROM \
      dgmTypeAttributes \
    WHERE \
      attributeID = 790 AND typeID = %i \
  " % (oreid)
  dbc.execute(query)
  table = dbc.fetchall()
  if len(table) < 1:
    return 12196
  return int(table[0][0])


def _query_refinable_products(dbc,item):
  query = "\
    SELECT \
      materialTypeID, \
      quantity \
    FROM \
      invTypeMaterials \
    WHERE \
      invTypeMaterials.typeID = '%s' \
  " % (item)
  dbc.execute(query)
  table = dbc.fetchall()
  result = []
  for row in table:
    result.append(ItemStack(row[0],row[1]))
  return result


def _query_ice(dbc,inventory,reflist):
  query = "\
    SELECT \
      invTypes.typeID \
    FROM \
      invTypes \
    WHERE \
      marketGroupID = 1855\
    ORDER BY invTypes.marketGroupID,invTypes.typeID \
  "
  dbc.execute(query)
  table = dbc.fetchall()
  for row in table:
    rid = row[0]
    cat = REFINABLE_CATEGORY_COMPRESSED_ICE if "Compressed " in inventory.get_item(rid).name else REFINABLE_CATEGORY_ICE
    reflist.append(Refinable(rid,cat,1,_query_refinable_skill(dbc,rid),_query_refinable_products(dbc,rid)))


