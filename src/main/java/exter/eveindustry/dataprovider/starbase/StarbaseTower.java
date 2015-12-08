package exter.eveindustry.dataprovider.starbase;

import exter.eveindustry.data.reaction.IStarbaseTower;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.dataprovider.inventory.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public class StarbaseTower implements IStarbaseTower
{
  public final Item TowerItem;
  public final ItemStack RequiredFuel;
  public final String Name;

  public StarbaseTower(TSLObject tsl,InventoryDA inventory)
  {
    TowerItem = inventory.items.get(tsl.getStringAsInt("id",-1));
    RequiredFuel = new ItemStack(inventory.items.get(tsl.getStringAsInt("fuel_id",-1)),tsl.getStringAsInt("fuel_amount",-1));
    Name = tsl.getString("name",null);
  }

  @Override
  public int getID()
  {
    return TowerItem.getID();
  }

  @Override
  public ItemStack getRequiredFuel()
  {
    return RequiredFuel;
  }
}
