package exter.eveindustry.dataprovider.blueprint;

import exter.eveindustry.data.blueprint.IInventionInstallation;
import exter.tsl.TSLObject;

public class InventionInstallation implements IInventionInstallation
{
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(Cost);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ID;
    result = prime * result + ((Name == null) ? 0 : Name.hashCode());
    temp = Double.doubleToLongBits(Time);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    InventionInstallation other = (InventionInstallation) obj;
    if(Double.doubleToLongBits(Cost) != Double.doubleToLongBits(other.Cost))
      return false;
    if(ID != other.ID)
      return false;
    if(Name == null)
    {
      if(other.Name != null)
        return false;
    } else if(!Name.equals(other.Name))
      return false;
    if(Double.doubleToLongBits(Time) != Double.doubleToLongBits(other.Time))
      return false;
    return true;
  }

  public final int ID;
  public final String Name;
  public final double Time;
  public final double Cost;
  public final boolean Relics;
  
  public InventionInstallation(TSLObject tsl)
  {
    Name = tsl.getString("name", null);
    ID = tsl.getStringAsInt("id", -1);
    Time = tsl.getStringAsDouble("time", -1);
    Cost = tsl.getStringAsDouble("cost", -1);
    Relics = tsl.getStringAsInt("relics", 0) != 0;
  }

  @Override
  public int getID()
  {
    return ID;
  }

  @Override
  public double getTimeBonus()
  {
    return Time;
  }

  @Override
  public double getCostBonus()
  {
    return Cost;
  }

  @Override
  public boolean isForRelics()
  {
    return Relics;
  }
}
