package exter.eveindustry.dataprovider.decryptor;

import exter.eveindustry.data.decryptor.IDecryptor;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.dataprovider.inventory.Item;
import exter.tsl.TSLObject;

public class Decryptor implements IDecryptor
{
  public final Item Item;
  public final int ME;
  public final int TE;
  public final int Runs;
  public final double Chance;

  public Decryptor(TSLObject tsl)
  {
    Item = InventoryDA.items.get(tsl.getStringAsInt("id",-1));
    ME = tsl.getStringAsInt("me",0);
    TE = tsl.getStringAsInt("te",0);
    Runs = tsl.getStringAsInt("runs",0);
    Chance = tsl.getStringAsDouble("chance",1);
  }

  @Override
  public int getID()
  {
    return Item.ID;
  }

  @Override
  public IItem getItem()
  {
    return Item;
  }

  @Override
  public int getModifierME()
  {
    return ME;
  }

  @Override
  public int getModifierTE()
  {
    return TE;
  }

  @Override
  public int getModifierRuns()
  {
    return Runs;
  }

  @Override
  public double getModifierChance()
  {
    return Chance;
  }
}
