from .item import InventoryDA,ItemStack

class Planet:
  def __init__(self,pid,name,resources,advanced):
    self.pid = pid
    self.name = name
    self.resources = resources
    self.advanced = advanced



  def get_planet_list(dbc):
    inventory = InventoryDA(dbc)

    query = "\
      SELECT \
        planetType.tlValue, \
        planetResource.tlValue \
      FROM \
        invTypes \
      INNER JOIN \
        ( \
          SELECT \
            COALESCE(dgmTypeAttributes.valueInt,CAST(dgmTypeAttributes.valueFloat AS INTEGER)) AS tlValue, \
            dgmTypeAttributes.typeID \
          FROM \
            dgmTypeAttributes \
          WHERE \
            dgmTypeAttributes.attributeID = 1632 \
        ) AS planetType \
      ON invTypes.typeID = planetType.typeID \
        INNER JOIN \
        ( \
          SELECT \
            COALESCE(dgmTypeAttributes.valueInt,CAST(dgmTypeAttributes.valueFloat AS INTEGER)) AS tlValue, \
            dgmTypeAttributes.typeID \
          FROM \
            dgmTypeAttributes \
          WHERE \
            dgmTypeAttributes.attributeID = 709 \
        ) AS planetResource \
      ON invTypes.typeID = planetResource.typeID"
    dbc.execute(query)
    table = dbc.fetchall()
    planet_names = {}
    planet_res = {}
    planet_adv = {}
    for row in table:
      pid = row[0]
      rid = row[1]
      if not pid in planet_names.keys() or not pid in planet_res.keys():
        planet_names[pid] = inventory.get_item(pid).name.replace("Planet (","").replace(")","")
        planet_res[pid] = []
      planet_res[pid].append(rid)
      planet_adv[pid] = bool(planet_names[pid] == "Barren" or planet_names[pid] == "Temperate")

    res = []
    for i in planet_names:
      res.append(Planet(i,planet_names[i],planet_res[i],planet_adv[i]))
    return res


class PlanetBuilding:
  def __init__(self,product,tax,level,materials):
    self.product = product
    self.tax = int(tax)
    self.level = int(level)
    self.materials = materials

  def get_buildings(dbc):
    query = "\
      SELECT \
        planetSchematicsTypeMap.typeID, \
        planetSchematicsTypeMap.schematicID, \
        planetSchematicsTypeMap.quantity, \
        invTypes.marketGroupID, \
        planetSchematics.cycleTime \
      FROM \
        planetSchematicsTypeMap \
      LEFT OUTER JOIN \
        invTypes ON planetSchematicsTypeMap.typeID = invTypes.typeID \
      INNER JOIN \
        planetSchematics ON planetSchematics.schematicID = planetSchematicsTypeMap.schematicID \
      WHERE \
        planetSchematicsTypeMap.isInput = 0 \
    "
    result = []
    dbc.execute(query)
    table = dbc.fetchall()
    for row in table:
      gid = row[3]
      if gid == 1334:
        tax = 400
        level = 1
      elif gid == 1335:
        tax = 7200
        level = 2
      elif gid == 1336:
        tax = 60000
        level = 3
      elif gid == 1337:
        tax = 1200000 
        level = 4
      amount = int(int(row[2]) * 3600 / int(row[4]))

      materials = []
      query = "\
        SELECT \
          typeID, \
          quantity \
        FROM \
          planetSchematicsTypeMap \
        WHERE \
          isInput = 1 AND \
          schematicID = %i \
      " % (int(row[1]))
      dbc.execute(query)
      mtable = dbc.fetchall()
      for mrow in mtable:
        materials.append(ItemStack(mrow[0],int(int(mrow[1]) * 3600 / int(row[4]))))
      result.append(PlanetBuilding(ItemStack(row[0],amount),tax,level,materials))

      query = "\
      SELECT \
        typeID \
      FROM \
        invTypes \
      WHERE \
        marketGroupID = 1333 \
    "
    dbc.execute(query)
    table = dbc.fetchall()
    for row in table:
      result.append(PlanetBuilding(ItemStack(row[0],6000),4,0,[]))
    return result
