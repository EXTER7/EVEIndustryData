#!/usr/bin/python3

# This script takes CCP's EVE Static Data Export and extracts/converts 
# industry data to TSL files for use in the EVEIndustryData Java library.

import sys
import os
import hashlib
import shutil
import math
import re
import zipfile
import shutil
import bz2
import sqlite3

from evedump.item import *
from evedump.blueprint import *
from evedump.refinable import *
from evedump.planet import *
from evedump.reaction import *
from evedump.index import *
from evedump.starmap import *
from tsl.tslwriter import *



# Industry skills used in manufacturing and refining tasks.
skills = [\
   3380,  # Industry \
   3385,  # Refining \
   3388,  # Production Efficiency \
   3389,  # Refinery Efficiency \
   18025, # Ice Processing \
   12196, # Scrapmetal Processing \
]

def add_to_inventory_tsl(item_id):
  if item_id not in inventory_tsl.keys():
    inventory_tsl[item_id] = inventory.get_item(item_id)

def unbz2(source_path,dest_path):
  with open(dest_path, 'wb') as new_file, bz2.BZ2File(source_path, 'rb') as file:
    for data in iter(lambda : file.read(100 * 1024), b''):
      new_file.write(data)


flags = set()
for arg in sys.argv[1:]:
  if arg.startswith("--"):
    flags.add(arg)

def mkdir(name):
  try:
    os.mkdir(name)
  except OSError:
    pass
  

try:
  shutil.rmtree('eid')
except FileNotFoundError:
  pass

mkdir("eid")
mkdir("eid/blueprint")
mkdir("eid/blueprint/installation")
mkdir("eid/blueprint/installation/group")
mkdir("eid/blueprint/installation/invention")
mkdir("eid/refine")
mkdir("eid/planet")
mkdir("eid/reaction")
mkdir("eid/item")
mkdir("eid/item/group")
mkdir("eid/item/category")
mkdir("eid/item/metagroup")
mkdir("eid/solarsystem")
mkdir("eid/solarsystem/region")

if os.path.isfile("sde/eve.db.bz2"):
  print("Extracting 'sde/eve.db.bz2' ... ",end="",flush=True)
  try:
    unbz2("sde/eve.db.bz2","sde/eve.db")
  except Error:
    print(" Failed.")
    print("Error extracting 'sde/eve.db.bz2', aborting.")
    exit(1)
  try:
    os.unlink("sde/eve.db.bz2")
  except IOError:
    pass
  print(" Done.")

try:
  database = sqlite3.connect("sde/eve.db")
  dbc = database.cursor()
except IOError:
  print("Error opening 'sde/eve.db', aborting.")
  exit(1)

process_icons = os.path.isfile("sde/Types/0_64.png")

if process_icons:
  try:
    os.mkdir("eid/icons")
  except OSError:
    pass
else:
  print("Icon files not present, skipping icon data conversion.")

inventory = InventoryDA(dbc)



#Set of items that will go in the inventory.tsl file
inventory_tsl = {}

bp_list = Index()
bp_list.add_group(0,"Blueprints")
ore_list = []
ice_list = []

group_list = []

bpid_list = []


print("Loading Blueprints")
try:
  blueprints = Blueprint.get_list("sde/blueprints.yaml",dbc,inventory)
except IOError:
  print("Error opening 'sde/blueprints.yaml', aborting.")
  exit(1)
  

print("Converting Blueprints")
for i in blueprints:
  bp = blueprints[i]

  if bp.prodid == -1:
    continue

  prod = inventory.get_item(bp.prodid)
  if prod.id == -1:
    print("Bad blueprint product id: ",bp.prodid)
    continue


  if prod.market_group == -1:
    continue
  
  if len(bp.materials) == 0:
    continue

  if prod.group == 1194: # Special Edition Commodities
    continue

  if not prod.group in group_list:
    group_list.append(prod.group)

installation_list = {}
gi_list = []
installation_blacklist = [13,145,150,149,36,170,159] #Outposts and other unused installations.
default_installations = {}
for g in group_list:
  group_inst = GroupInstallation.get_list(dbc,g)
  def_gi = -1
  for gi in group_inst:
    inst = Installation.get(dbc,gi.installation)
    # Convert installation names to their proper starbase module name.
    if inst.activity == 1 and (not "Outpost" in inst.name) and (not "+ " in inst.name) and (not inst.id in installation_blacklist):
      if inst.name == "STATION manufacturing":
        inst.name = "Station" 
      if inst.name == "Booster Manufacturing":
        inst.name = "Drug Lab" 
      if inst.name == "Subsystem Manufacturing":
        inst.name = "Subsystem Assembly Array" 
      if inst.name == "Capital Ship Assembly":
        inst.name = "Capital Ship Assembly Array" 
      installation_list[inst.id] = inst
      gi.time *= inst.time
      gi.material *= inst.material
      gi.cost *= inst.cost
      gi.group = g
      gi.id = gi.installation * 23537 + gi.group
      gi_list.append(gi)
      if gi.installation == 6 or inst.name == "Subsystem Assembly Array": 
        default_installations[g] = gi.id
      if def_gi < 0:
        def_gi = gi.id
  if g not in default_installations:
    default_installations[g] = def_gi


inv_installation_list = {}
for ins in Installation.get_invention_list(dbc):
  # Convert installation names to their proper starbase module name.
  if (not "Outpost" in ins.name) and (not "+ " in ins.name) and (not ins.id in installation_blacklist):
    if ins.name == "STATION Invention":
      ins.name = "Station" 
    if ins.name == "Mobile Laboratory Invention":
      ins.name = "Design Lab" 
    if ins.name == "Ancient Relic Invention":
      continue
    ins.relics = False
    if ins.name == "Experimental Laboratory": 
      ins.name = "Experimental Lab"
      ins.relics = True 
    print(ins.name + " " + str(ins.time))
    inv_installation_list[ins.id] = ins
    
for gi in gi_list:
  tslfile = TSLWriter("eid/blueprint/installation/group/%i.tsl" % (gi.id))
  tslfile.start_collection("insgroup")
  tslfile.put_value("id",gi.id)
  tslfile.put_value("group",gi.group)
  tslfile.put_value("installation",gi.installation)
  tslfile.put_value("time",gi.time)
  tslfile.put_value("material",gi.material)
  tslfile.put_value("cost",gi.cost)
  tslfile.end_collection()

for key in installation_list:
  ins = installation_list[key]
  tslfile = TSLWriter("eid/blueprint/installation/%i.tsl" % (key))
  tslfile.start_collection("installation")
  tslfile.put_value("id",key)
  tslfile.put_value("name",ins.name)
  tslfile.end_collection()

for key in inv_installation_list:
  ins = inv_installation_list[key]
  tslfile = TSLWriter("eid/blueprint/installation/invention/%i.tsl" % (key))
  tslfile.start_collection("invention")
  tslfile.put_value("id",key)
  tslfile.put_value("name",ins.name)
  tslfile.put_value("time",ins.time)
  tslfile.put_value("cost",ins.cost)
  tslfile.put_value("relics",1 if ins.relics else 0)
  tslfile.end_collection()


for i in blueprints:
  bp = blueprints[i]

  if bp.prodid == -1:
    continue

  prod = inventory.get_item(bp.prodid)
  if prod.id == -1:
    continue

  if prod.market_group == -1:
    continue
  
  if len(bp.materials) == 0:
    continue

  if prod.group == 1194: # Special Edition Commodities
    continue


  if len(bp.materials) == 0: # Skip empty BPs.
    continue
  if prod.name.startswith("Compressed "): # Skip ore compression.
    continue
  if inventory.get_item(bp.bpid).group == 1517: # Skip unreleased citadel related blueprints.
    continue

  add_to_inventory_tsl(bp.prodid)
  bp_list.add_entry(bp.prodid,0)
  bpid_list.append(bp.prodid)


  tslfile = TSLWriter("eid/blueprint/%i.tsl" % (bp.prodid))
  tslfile.start_collection("blueprint")
  tslfile.put_value("id",bp.prodid)
  tslfile.put_value("amount",bp.amount)
  tslfile.put_value("time",bp.time)
  tslfile.put_value("installation",default_installations[prod.group])
  for mat in bp.materials:
    add_to_inventory_tsl(mat.itemid)
    if mat.amount > 0:
      tslfile.start_collection("material")
      tslfile.push_formatter()
      tslfile.formatter.set_newline(False)
      tslfile.put_value("id",mat.itemid)
      tslfile.put_value("amount",mat.amount)
      tslfile.end_collection()
      tslfile.pop_formatter()
  for s in bp.skills:
    add_to_inventory_tsl(s) 
    tslfile.put_value("skill",s)

  if bp.invention != None:
    tslfile.start_collection("invention")
    tslfile.put_value("time",int(bp.invention.time))
    if len(bp.invention_relics) == 0:
      tslfile.put_value("chance",bp.invention_chance)
      tslfile.put_value("runs",bp.invention_runs)
    add_to_inventory_tsl(bp.invention.eskill)
    tslfile.put_value("eskill",bp.invention.eskill)
    for s in bp.invention.dskills:
      add_to_inventory_tsl(s)
      tslfile.put_value("dskill",s)
    for m in bp.invention.materials:
      add_to_inventory_tsl(m.itemid)
      if m.amount > 0:
        tslfile.start_collection("material")
        tslfile.push_formatter()
        tslfile.formatter.set_newline(False)
        tslfile.put_value("id",m.itemid)
        tslfile.put_value("amount",m.amount)
        tslfile.end_collection()
        tslfile.pop_formatter()
    for m in bp.invention_relics:
      add_to_inventory_tsl(m.rid)
      tslfile.start_collection("relic")
      tslfile.push_formatter()
      tslfile.formatter.set_newline(False)
      tslfile.put_value("id",m.rid)
      tslfile.put_value("chance",m.chance)
      tslfile.put_value("runs",m.runs)
      tslfile.end_collection()
      tslfile.pop_formatter()
    tslfile.end_collection()
  tslfile.end_collection()



bp_list.sort_entries(lambda entry: inventory.get_item(entry.item).name)
bp_list.write_to_file("eid/blueprint/index.tsl")

decr = Decryptor.get_list(dbc)


tslfile = TSLWriter("eid/blueprint/decryptors.tsl")
tslfile.start_collection("decryptors")
for d in decr:
  add_to_inventory_tsl(d.item)
  tslfile.start_collection("d")
  tslfile.push_formatter()
  tslfile.formatter.set_newline(False)
  tslfile.put_value("id",d.item)
  tslfile.put_value("me",d.me)
  tslfile.put_value("te",d.te)
  tslfile.put_value("runs",d.runs)
  tslfile.put_value("chance",d.chance)
  tslfile.end_collection()
  tslfile.pop_formatter()
tslfile.end_collection()
  


print("Converting Refinables")
ref_list = Index()
ref_list.add_group(REFINABLE_CATEGORY_ORE,"Ore")
ref_list.add_group(REFINABLE_CATEGORY_ICE,"Ice")
ref_list.add_group(REFINABLE_CATEGORY_COMPRESSED_ORE,"Compressed Ore")
ref_list.add_group(REFINABLE_CATEGORY_COMPRESSED_ICE,"Compressed Ice")
ref_list.add_group(REFINABLE_CATEGORY_ALCHEMY,"Alchemy Reaction")

for refinable in Refinable.get_refinables(dbc,inventory):
  add_to_inventory_tsl(refinable.rid)
  ref_list.add_entry(refinable.rid,refinable.category)

  add_to_inventory_tsl(refinable.skill)

  tslfile = TSLWriter("eid/refine/%i.tsl" % (refinable.rid))
  tslfile.start_collection("refine")
  tslfile.put_value("id",refinable.rid)
  tslfile.put_value("batch",refinable.batch)
  tslfile.put_value("sid",refinable.skill)
  for product in refinable.products:
    add_to_inventory_tsl(product.itemid)
    tslfile.start_collection("product")
    tslfile.push_formatter()
    tslfile.formatter.set_newline(False)
    tslfile.put_value("id",product.itemid)
    tslfile.put_value("amount",product.amount)
    tslfile.end_collection()
    tslfile.pop_formatter()
  tslfile.end_collection()

ref_list.write_to_file("eid/refine/index.tsl")

print("Converting PI")

planets = Planet.get_planet_list(dbc)
tslfile = TSLWriter("eid/planet/planets.tsl")
tslfile.start_collection("planets")
for p in planets:
  it = inventory.get_item(p.pid)
  add_to_inventory_tsl(p.pid)

  tslfile.start_collection("planet")
  tslfile.put_value("id",p.pid)
  tslfile.put_value("name",p.name)
  tslfile.put_value("advanced",1 if p.advanced else 0)
  for r in p.resources:
    add_to_inventory_tsl(r)
    tslfile.put_value("resource",r)
  tslfile.end_collection()
tslfile.end_collection()


pi_list = Index()
pi_list_p4 = Index()

pi_list.add_group(0,"Processed (P1)")
pi_list.add_group(1,"Refined (P2)")
pi_list.add_group(2,"Specialized (P3)")

pi_list_p4.add_group(0,"Processed (P1)")
pi_list_p4.add_group(1,"Refined (P2)")
pi_list_p4.add_group(2,"Specialized (P3)")
pi_list_p4.add_group(3,"Advanced (P4)")

for building in PlanetBuilding.get_buildings(dbc):
  add_to_inventory_tsl(building.product.itemid)
  if building.level > 0:
    if building.level < 4:
      pi_list.add_entry(building.product.itemid,building.level - 1)
    pi_list_p4.add_entry(building.product.itemid,building.level - 1)

  tslfile = TSLWriter("eid/planet/%i.tsl" % (building.product.itemid))
  tslfile.start_collection("planetbuilding")
  tslfile.put_value("id",building.product.itemid)
  tslfile.put_value("amount",building.product.amount)
  tslfile.put_value("level",building.level)
  tslfile.put_value("tax",building.tax)
  if len(building.materials) > 0:
    for m in building.materials:
      add_to_inventory_tsl(m.itemid)
      tslfile.start_collection("in")
      tslfile.push_formatter()
      tslfile.formatter.set_newline(False)
      tslfile.put_value("id",m.itemid)
      tslfile.put_value("amount",m.amount)
      tslfile.end_collection()
      tslfile.pop_formatter()
  tslfile.end_collection()

pi_list.sort_entries(lambda entry: inventory.get_item(entry.item).name)
pi_list.write_to_file("eid/planet/index.tsl")
pi_list_p4.sort_entries(lambda entry: inventory.get_item(entry.item).name)
pi_list_p4.write_to_file("eid/planet/index_advanced.tsl")

print("Converting Reactions")

reaction_index = Index()
reaction_index.add_group(REACTION_CATEGORY_SIMPLE,"Simple")
reaction_index.add_group(REACTION_CATEGORY_COMPLEX,"Complex")
reaction_index.add_group(REACTION_CATEGORY_POLYMER,"Polymer")
reaction_index.add_group(REACTION_CATEGORY_BOOSTER,"Booster")
reaction_index.add_group(REACTION_CATEGORY_ALCHEMY,"Alchemy")
moon_index = Index()
moon_index.add_group(0,"Moon Materials")

for reaction in Reaction.get_reactions(dbc,inventory):
  tslfile = TSLWriter("eid/reaction/%i.tsl" % (reaction.rid))
  tslfile.start_collection("reaction")
  tslfile.put_value("id",reaction.rid)

  for material in reaction.inputs:
    add_to_inventory_tsl(material.itemid)
    tslfile.start_collection("in")
    tslfile.push_formatter()
    tslfile.formatter.set_newline(False)
    tslfile.put_value("id",int(material.itemid))
    tslfile.put_value("amount",int(material.amount))
    tslfile.end_collection()
    tslfile.pop_formatter()
  for material in reaction.outputs:
    add_to_inventory_tsl(material.itemid)
    tslfile.start_collection("out")
    tslfile.push_formatter()
    tslfile.formatter.set_newline(False)
    tslfile.put_value("id",int(material.itemid))
    tslfile.put_value("amount",int(material.amount))
    tslfile.end_collection()
    tslfile.pop_formatter()
  tslfile.end_collection()
  
  if reaction.category == REACTION_CATEGORY_MOON:
    moon_index.add_entry(reaction.rid,0)
  else:
    reaction_index.add_entry(reaction.rid,reaction.category)


reaction_index.sort_entries(lambda entry: inventory.get_item(entry.item).name)
reaction_index.write_to_file("eid/reaction/index.tsl")
moon_index.sort_entries(lambda entry: inventory.get_item(entry.item).name)
moon_index.write_to_file("eid/reaction/index_moon.tsl")

tslfile = TSLWriter("eid/starbases.tsl")
tslfile.start_collection("starbases")
for sb in Starbase.get_starbases(dbc):
  tower = inventory.get_item(sb.sbid)
  name = tower.name.replace(" Control Tower","")
  if "Medium" not in name and "Small" not in name:
    name = name + " Large"

  add_to_inventory_tsl(sb.sbid)
  add_to_inventory_tsl(sb.fuel)

  tslfile.start_collection("_")
  tslfile.push_formatter()
  tslfile.formatter.set_newline(False)
  tslfile.put_value("id",int(sb.sbid))
  tslfile.put_value("name",name)
  tslfile.put_value("fuel_id",int(sb.fuel))
  tslfile.put_value("fuel_amount",int(sb.fuel_amount))
  tslfile.end_collection()
  tslfile.pop_formatter()
tslfile.end_collection()

print("Converting Starmap")

# Jove regions, inaccessible to players.
jove_regions = [10000019, 10000017, 10000004]

regions = SolarSystemRegion.get_list(dbc)
systems = SolarSystem.get_list(dbc)
rids = {}
prog = re.compile("[A-Z]-R[0-9]{5}$")
regions.append(SolarSystemRegion([42,"WH-Space"]))
for reg in regions:
  rids[reg.id] = reg
  if not prog.match(reg.name) and reg.id not in jove_regions:
    tslfile = TSLWriter("eid/solarsystem/region/%i.tsl" % (reg.id))
    tslfile.start_collection("region")
    tslfile.put_value("id",reg.id)
    tslfile.put_value("name",reg.name)
    tslfile.end_collection()

for ss in systems:
  # Condense all WH space system into a single region
  ssrid = ss.region
  if prog.match(rids[ssrid].name):
    ssrid = 42
  if ss.region in rids:
    tslfile = TSLWriter("eid/solarsystem/%i.tsl" % (ss.id))
    tslfile.start_collection("system")
    tslfile.put_value("id",ss.id)
    tslfile.put_value("name",ss.name)
    tslfile.put_value("region",int(ssrid))
    tslfile.end_collection()

for s in skills:
  add_to_inventory_tsl(s)



print("Converting Items")

icons = []
groups = {}
categories = {}
metagroups = {}


for i in inventory_tsl:
  item = inventory_tsl[i]
  icon_id = -1
  if process_icons:
    try:
      icon_hash = hashlib.md5(open("sde/Types/%i_64.png" % (i), 'rb').read()).hexdigest()
      icon_id = len(icons)
      add = True
      for k,ic in enumerate(icons):
        if ic == icon_hash:
          add = False
          icon_id = k
          break
      if add:
        icons.append(icon_hash)
        shutil.copyfile("sde/Types/%i_64.png" % (i), "eid/icons/%i.png" % (icon_id))
    except IOError:
      print("Error processing sde/Types/%i_64.png" % (i))

  if item.group not in groups.keys():
    g = Group(dbc,item.group)
    g.icon = icon_id
    g.blueprints = 1 if i in bpid_list else 0
    groups[item.group] = g
    if g.category not in categories.keys():
      c = Category(dbc,g.category)
      if process_icons:
        c.icon = icon_id
      c.blueprints = 0
      categories[g.category] = c
  if item.meta_group not in metagroups.keys():
    g = MetaGroup(dbc,item.meta_group)
    metagroups[item.meta_group] = g
  tslfile = TSLWriter("eid/item/%i.tsl" % (item.id))
  tslfile.start_collection("item")
  tslfile.put_value("id",i)
  tslfile.put_value("gid",item.group)
  tslfile.put_value("name",item.name)
  tslfile.put_value("vol",item.volume)
  if process_icons:
    tslfile.put_value("icon",icon_id)
  tslfile.put_value("market",1 if item.market_group >=0 else 0)
  tslfile.put_value("mg",item.meta_group)
  tslfile.end_collection()

for i in groups:
  gr = groups[i]
  tslfile = TSLWriter("eid/item/group/%i.tsl" % (i))
  if gr.blueprints == 1:
    categories[gr.category].blueprints = 1
  tslfile.start_collection("group")
  tslfile.put_value("id",int(i))
  tslfile.put_value("name",gr.name)
  tslfile.put_value("cid",int(gr.category))
  if process_icons:
    tslfile.put_value("icon",int(gr.icon))
  tslfile.put_value("blueprints",int(gr.blueprints))
  tslfile.end_collection()


for i in categories:
  cat = categories[i]
  tslfile = TSLWriter("eid/item/category/%i.tsl" % (i))
  tslfile.start_collection("category")
  tslfile.put_value("id",int(i))
  tslfile.put_value("name",cat.name)
  if process_icons:
    tslfile.put_value("icon",int(cat.icon))
  tslfile.put_value("blueprints",int(cat.blueprints))
  tslfile.end_collection()

for i in metagroups:
  m = metagroups[i]
  tslfile = TSLWriter("eid/item/metagroup/%i.tsl" % (i))
  tslfile.start_collection("metagroup")
  tslfile.put_value("id",int(i))
  tslfile.put_value("name",m.name)
  tslfile.end_collection()

database.close()

if "--no-zip" not in flags:
  wd = os.getcwd()
  os.chdir(wd + "/eid")
  zipf = zipfile.ZipFile('../eid.zip', 'w')
  for root, dirs, files in os.walk("./"):
    for file in files:
      zipf.write(os.path.join(root, file))
  zipf.close()
  os.chdir(wd)
  shutil.rmtree('eid')

