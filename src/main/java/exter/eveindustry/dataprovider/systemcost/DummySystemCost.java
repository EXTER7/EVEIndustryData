package exter.eveindustry.dataprovider.systemcost;

import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;


public class DummySystemCost implements ISolarSystemIndustryCost
{
  public final int SystemID;
  
  public DummySystemCost(int sys)
  {
    SystemID = sys;
  }

  @Override
  public double getManufacturingCost()
  {
    return 0;
  }

  @Override
  public double getInventionCost()
  {
    return 0;
  }
}
