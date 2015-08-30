package exter.eveindustry.dataprovider.blueprint;

import exter.eveindustry.data.blueprint.IInstallationGroup;
import exter.tsl.TSLObject;

public class InstallationGroup implements IInstallationGroup
{
  public final int ID;
  public final int GroupID;
  public final int InstallationID;
  public final double Time;
  public final double Material;
  public final double Cost;
  
  public InstallationGroup(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    GroupID = tsl.getStringAsInt("group", -1);
    InstallationID = tsl.getStringAsInt("installation", -1);
    Time = tsl.getStringAsDouble("time", -1);
    Material = tsl.getStringAsDouble("material", -1);
    Cost = tsl.getStringAsDouble("cost", -1);
  }

  @Override
  public int hashCode()
  {
    return GroupID * 16553 + InstallationID;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(this == obj)
    {
      return true;
    }
    if(getClass() != obj.getClass())
    {
      return false;
    }
    InstallationGroup other = (InstallationGroup) obj;
    if(GroupID != other.GroupID || InstallationID != other.InstallationID)
    {
      return false;
    }
    return true;
  }

  @Override
  public int getID()
  {
    return ID;
  }

  @Override
  public int getGroupID()
  {
    return GroupID;
  }

  @Override
  public double getTimeBonus()
  {
    return Time;
  }

  @Override
  public double getMaterialBonus()
  {
    return Material;
  }

  @Override
  public double getCostBonus()
  {
    return Cost;
  }
}
