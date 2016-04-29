import yaml
from .item import *

class InventionResult:
  def __init__(self,data):
    self.rid = int(data["typeID"])
    if "probability" in data:
      self.chance = float(data["probability"])
    else:
      self.chance = float(-1)
    self.runs = int(data["quantity"])

class InventionRelic:
  def __init__(self,rid,chance,runs):
    self.rid = int(rid)
    self.chance = float(chance)
    self.runs = int(runs)

class BlueprintInvention:
  def __init__(self,dbc,inventory,data):
    self.time = int(data["time"])

    self.results = []   
    for r in data["products"]:
      self.results.append(InventionResult(r))


    self.eskill = -1

    self.dskills = []
    for s in data["skills"]:
      sid = int(s["typeID"])
      if "Encryption Methods" in inventory.get_item(sid).name:
        self.eskill = sid
      else:
        self.dskills.append(sid)

    self.materials = []
    for m in data["materials"]:
      g = inventory.get_item(int(m["typeID"])).group
      if g != 716 and g != 979:
         self.materials.append(ItemStack(m["typeID"],m["quantity"]))

# ItemIDs of skills that affect manufacturing time of T2 blueprints.
t2skills = [
  3395,
  3397,
  3398,
  3396,
  11433,
  11441,
  11443,
  11444,
  11445,
  11446,
  11447,
  11448,
  11449,
  11450,
  11451,
  11452,
  11453,
  11454,
  11455,
  11529
]

class Blueprint:
  def get_list(path,dbc,inventory):
    fd = open(path,"r")
    table = yaml.load(fd,Loader=yaml.CLoader)
    result = {}
    for row in table:
      data = table[row]
      bpid = int(row)
      if inventory.get_item(bpid).id < 0:
        continue
      bp = Blueprint(bpid,data,inventory)
      if bp.prodid >= 0:
        result[bp.prodid] = bp
        result[bp.prodid].invention_relics = []
      else:
        result[bp.bpid] = bp
        result[bp.bpid].invention_relics = []
    fd.close()


    for i in result:
      bp = result[i]
      if not bp.invdata is None and "products" in bp.invdata.keys():
        inv = BlueprintInvention(dbc,inventory,bp.invdata)
        if inv.eskill != -1:
          for r in inv.results:
            if not r.rid in table or r.chance < 0:
              print("Bad blueprint invention result: ",r.rid," - ",inventory.get_item(r.rid).name)
              continue
            d = table[r.rid]
            p = int(d["activities"]["manufacturing"]["products"][0]["typeID"])
            if p in result:
              result[p].invention = inv
              item = inventory.get_item(p)
              if item.id < 0:
                print("Bad blueprint invention result: ",p)
                continue
              if item.meta_group == 14:
                result[p].invention_relics.append(InventionRelic(bp.bpid,r.chance,r.runs))
                result[p].invention_chance = 0
                result[p].invention_runs = 0
              else:
                result[p].invention_chance = r.chance
                result[p].invention_runs = r.runs
    return result

  def __init__(self,bpid,data,inventory):
    self.bpid = bpid
    self.prodid = -1
    self.invdata = None if not "invention" in data["activities"] else data["activities"]["invention"]
    self.invention = None
    if "manufacturing" in data["activities"]:
      manufacturing = data["activities"]["manufacturing"]

      self.prodid = int(manufacturing["products"][0]["typeID"])
      self.amount = int(manufacturing["products"][0]["quantity"])
      prod = inventory.get_item(self.prodid)
      if prod.id < 0:
        print("Bad blueprint manufacturing result: ",self.prodid)
        return
      self.time = manufacturing["time"]
      self.materials = []
      if "materials" in manufacturing:
        for m in manufacturing["materials"]:
          it = inventory.get_item(int(m["typeID"]))
          if it.id >= 0:
            g = it.group
            if g != 716 and g != 979:
              self.materials.append(ItemStack(m["typeID"],m["quantity"]))
      self.skills = []
      if "skills" in manufacturing and prod.meta_group == 2:
        for s in manufacturing["skills"]:
           sid = int(s["typeID"])
           if sid in t2skills:
             self.skills.append(sid)



class Installation:
  def __init__(self,iid,row):
    self.id = int(iid)
    self.name = deunicode(row[1])
    self.time = float(row[2])
    self.material = float(row[3])
    self.activity = int(row[4])
    self.cost = float(row[5])

  def get(dbc,iid):
    query = "\
      SELECT \
        assemblyLineTypeID, \
        assemblyLineTypeName, \
        baseTimeMultiplier, \
        baseMaterialMultiplier, \
        activityID, \
        baseCostMultiplier \
      FROM \
        ramAssemblyLineTypes \
      WHERE \
        assemblyLineTypeID = %i\
    " % (iid)
    dbc.execute(query)
    table = dbc.fetchall()
    return Installation(iid,table[0])

  def get_invention_list(dbc):
    query = "\
      SELECT \
        assemblyLineTypeID, \
        assemblyLineTypeName, \
        baseTimeMultiplier, \
        IFNULL(baseMaterialMultiplier,1), \
        activityID, \
        baseCostMultiplier \
      FROM \
        ramAssemblyLineTypes \
      WHERE \
        activityID = 8\
    "
    dbc.execute(query)
  
    res = []
    table = dbc.fetchall()
    for row in table:
      print(row)
      res.append(Installation(row[0],row))
    return res

class GroupInstallation:
  def __init__(self,row):
    self.installation = int(row[0])
    self.time = float(row[1])
    self.material = float(row[2])
    self.cost = float(row[3])

  def get_list(dbc,gid):
    query = "\
      SELECT \
        assemblyLineTypeID, \
        timeMultiplier, \
        materialMultiplier, \
        costMultiplier \
      FROM \
        ramAssemblyLineTypeDetailPerGroup \
      WHERE \
        groupID = %i\
    " % (gid)
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(GroupInstallation(row))

    cat = Group(dbc,gid).category

    query = "\
      SELECT \
        assemblyLineTypeID, \
        timeMultiplier, \
        materialMultiplier, \
        costMultiplier \
      FROM \
        ramAssemblyLineTypeDetailPerCategory \
      WHERE \
        categoryID = %i\
    " % (cat)
    dbc.execute(query)
    table = dbc.fetchall()

    for row in table:
      res.append(GroupInstallation(row))
    return res


class Decryptor:
  def __init__(self,row):
    self.item = int(row[0])
    self.me = int(row[1])
    self.te = int(row[2])
    self.runs = int(row[3])
    self.chance = float(row[4])

  def get_list(dbc):
    query = " \
      SELECT \
        invTypes.typeID, \
        COALESCE(me.valueInt,me.valueFloat), \
        COALESCE(te.valueInt,te.valueFloat), \
        COALESCE(runs.valueInt,runs.valueFloat), \
        COALESCE(chance.valueInt,chance.valueFloat) \
      FROM invTypes \
      INNER JOIN \
      ( SELECT typeID, valueInt, valueFloat \
        FROM dgmTypeAttributes \
        WHERE attributeID = 1113 \
      ) AS me ON me.typeID = invTypes.typeID \
      INNER JOIN \
      ( SELECT typeID, valueInt, valueFloat \
        FROM dgmTypeAttributes \
        WHERE attributeID = 1114 \
      ) AS te ON te.typeID = invTypes.typeID \
      INNER JOIN \
      ( SELECT typeID, valueInt, valueFloat \
        FROM dgmTypeAttributes \
        WHERE attributeID = 1124 \
      ) AS runs ON runs.typeID = invTypes.typeID \
      INNER JOIN \
      ( SELECT typeID, valueInt, valueFloat \
        FROM dgmTypeAttributes \
        WHERE attributeID = 1112 \
      ) AS chance ON chance.typeID = invTypes.typeID \
      WHERE invTypes.groupID = 1304"
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(Decryptor(row))
    return res

