import sqlite3
from .item import ItemStack

REACTION_CATEGORY_SIMPLE = 0
REACTION_CATEGORY_COMPLEX = 1
REACTION_CATEGORY_POLYMER = 2
REACTION_CATEGORY_BOOSTER = 3
REACTION_CATEGORY_ALCHEMY = 4
REACTION_CATEGORY_MOON = 5


def _query_reaction_pins(dbc,reaction_id,is_input):
  query = "\
    SELECT \
      typeID, \
      quantity \
    FROM \
      invTypeReactions \
    WHERE \
      input = %i AND reactionTypeID = %i \
  " % (is_input,reaction_id)
  dbc.execute(query)
  pins = dbc.fetchall()
  result = []
  for p in pins:
    query = "\
      SELECT \
        IFNULL(COALESCE(valueInt,valueFloat),1) \
      FROM \
        dgmTypeAttributes \
      WHERE \
        attributeID = 726 AND typeID = %i\
    " % (p[0])
    dbc.execute(query)
    table = dbc.fetchall()
    mult = 1
    if len(table) > 0:
      mult = table[0][0]
    result.append(ItemStack(p[0],int(p[1])*int(mult)))
  return result

class Reaction:
  def __init__(self,rid,category,inputs,outputs):
    self.rid = int(rid)
    self.category = int(category)
    self.inputs = inputs
    self.outputs = outputs


  def get_reactions(dbc,inventory):
    #Reactions
    query = " \
      SELECT DISTINCT \
        reactionTypeID \
      FROM \
        invTypeReactions \
      LEFT JOIN \
      ( \
        SELECT \
          marketGroupID, \
          typeID \
        FROM \
          invTypes \
      ) AS items \
      ON items.typeID = invTypeReactions.reactionTypeID \
    "
    dbc.execute(query)
    table = dbc.fetchall()
    result = []
    for row in table:
      i = row[0]
      outputs = _query_reaction_pins(dbc,i,0)
      inputs = _query_reaction_pins(dbc,i,1)
      rid = -1
      for out in outputs:
        main = True
        for inp in inputs:
          if inp.itemid == out.itemid:
            main = False
            break
        if main:
          rid = out.itemid
      item = inventory.get_item(rid)

      #Skip unpublished reactions.
      if inventory.get_item(rid).market_group == 23:
        continue

      group = -1
      if " Booster" in item.name:
        group = REACTION_CATEGORY_BOOSTER
      else:
        if item.market_group == 500:
          if "Unrefined " in item.name:
            group = REACTION_CATEGORY_ALCHEMY
          else:
            group = REACTION_CATEGORY_SIMPLE
      if item.market_group == 499:
        group = REACTION_CATEGORY_COMPLEX
      if item.market_group == 1860:
        group = REACTION_CATEGORY_POLYMER
      if group != -1:
        result.append(Reaction(rid,group,inputs,outputs))
        
    #Moon materials
    query = " \
      SELECT \
        typeID \
      FROM \
        invTypes \
      WHERE \
        marketGroupID = 501 \
      ; \
    "
    dbc.execute(query)
    table = dbc.fetchall()
    for row in table:
      rid = row[0]
      result.append(Reaction(rid,REACTION_CATEGORY_MOON,[],[ItemStack(rid,100)]))
    return result

class Starbase:
  def __init__(self,row):
    self.sbid = int(row[0])
    self.fuel = int(row[1])
    self.fuel_amount = int(row[2])

  def get_starbases(dbc):
    query = "\
      SELECT \
        invTypes.typeID, \
        sbfuel.resourceTypeID, \
        sbfuel.quantity \
      FROM  \
        invTypes \
      LEFT OUTER JOIN \
        ( \
          SELECT \
            controlTowerTypeID, \
            resourceTypeID, \
            quantity \
          FROM invControlTowerResources \
          WHERE purpose = 1 AND IFNULL(minSecurityLevel,0) < 0.4 \
        ) AS sbfuel ON sbfuel.controlTowerTypeID = invTypes.typeID \
      WHERE \
        invTypes.marketGroupID = 478 \
      ORDER BY \
        invTypes.typeName \
    ;"
    dbc.execute(query)
    table = dbc.fetchall()
    result = []
    for row in table:
      result.append(Starbase(row))
    return result

