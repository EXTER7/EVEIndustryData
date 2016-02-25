package exter.eveindustry.dataprovider.blueprint;

import exter.tsl.TSLObject;

public class Installation
{
  public final int ID;
  public final String Name;
  
  
  public Installation(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id", -1);
    Name = tsl.getString("name", null);
  }

  @Override
  public int hashCode()
  {
    return ID;
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
    Installation other = (Installation) obj;
    if(ID != other.ID)
    {
      return false;
    }
    return true;
  }
}
