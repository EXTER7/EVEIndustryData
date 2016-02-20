package exter.eveindustry.dataprovider.planet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.planet.IPlanetBuilding;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public class PlanetBuilding implements IPlanetBuilding
{

  public final ItemStack ProductItem;
  public final int Tax;
  public final int Level;
  public final List<ItemStack> Materials;
 
  public PlanetBuilding(TSLObject tsl, InventoryDA inventory)
  {
    ArrayList<ItemStack> matlist = new ArrayList<ItemStack>();
    ProductItem = new ItemStack(inventory.items.get(tsl.getStringAsInt("id",-1)),tsl.getStringAsInt("amount",-1));
    Level = tsl.getStringAsInt("level",-1);
    Tax = tsl.getStringAsInt("tax",-1);
    List<TSLObject> tsl_materials = tsl.getObjectList("in");
    for(TSLObject mat_tsl:tsl_materials)
    {
      int mat_id = mat_tsl.getStringAsInt("id",-1);
      int raw_amount = mat_tsl.getStringAsInt("amount",0);

      matlist.add(new ItemStack(inventory.items.get(mat_id), raw_amount));
    }
    Materials = Collections.unmodifiableList(matlist);
  }

  @Override
  public ItemStack getProduct()
  {
    return ProductItem;
  }

  @Override
  public int getCustomsOfficeTax()
  {
    return Tax;
  }

  @Override
  public int getLevel()
  {
    return Level;
  }

  @Override
  public List<ItemStack> getMaterials()
  {
    return Materials;
  }

  @Override
  public int getID()
  {
    return ProductItem.item.getID();
  }
}
