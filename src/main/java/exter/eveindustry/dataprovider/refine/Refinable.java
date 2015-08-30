package exter.eveindustry.dataprovider.refine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.refinable.IRefinable;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.dataprovider.inventory.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public class Refinable implements IRefinable
{
  public final List<ItemStack> Products;
  public final ItemStack RefineItem;
  public final int Skill;

  public Refinable(TSLObject tsl)
  {

    ArrayList<ItemStack> prodlist = new ArrayList<ItemStack>();

    RefineItem = new ItemStack(InventoryDA.items.get(tsl.getStringAsInt("id",-1)),tsl.getStringAsInt("batch",-1));
    Skill = tsl.getStringAsInt("sid",-1);
    
    List<TSLObject> products_tsl = tsl.getObjectList("product");
    for(TSLObject min:products_tsl)
    {
      Item min_id = InventoryDA.items.get(min.getStringAsInt("id",-1));
      int min_amount = min.getStringAsInt("amount",-1);
      prodlist.add(new ItemStack(min_id,min_amount));
    }
    Products = Collections.unmodifiableList(prodlist);
  }

  @Override
  public List<ItemStack> getProducts()
  {
    return Products;
  }

  @Override
  public int getID()
  {
    return RefineItem.item.getID();
  }

  @Override
  public ItemStack getRequiredItem()
  {
    return RefineItem;
  }

  @Override
  public int getSkill()
  {
    return Skill;
  }
}
