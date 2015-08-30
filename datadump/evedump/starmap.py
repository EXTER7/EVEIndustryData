class SolarSystemRegion:
  def __init__(self,row):
    self.id = int(row[0])
    self.name = str(row[1])

  def get_list(dbc):
    query = "\
      SELECT \
        regionID, \
        regionName \
      FROM \
        mapRegions \
      ORDER BY \
        regionName \
    "
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(SolarSystemRegion(row))
    return res


class SolarSystem:
  def __init__(self,row):
    self.id = int(row[0])
    self.name = str(row[1])
    self.region = int(row[2])

  def get_list(dbc):
    query = "\
      SELECT \
        solarSystemID, \
        solarSystemName, \
        regionID \
      FROM \
        mapSolarSystems \
      ORDER BY \
        solarSystemName \
      "
    dbc.execute(query)
    table = dbc.fetchall()
    res = []
    for row in table:
      res.append(SolarSystem(row))
    return res

