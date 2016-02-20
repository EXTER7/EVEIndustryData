package exter.eveindustry.dataprovider;

import java.io.File;
import java.math.BigDecimal;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.dataprovider.blueprint.Blueprint;
import exter.eveindustry.dataprovider.blueprint.BlueprintDA;
import exter.eveindustry.dataprovider.blueprint.InstallationDA;
import exter.eveindustry.dataprovider.blueprint.InstallationGroup;
import exter.eveindustry.dataprovider.blueprint.InventionInstallation;
import exter.eveindustry.dataprovider.decryptor.Decryptor;
import exter.eveindustry.dataprovider.decryptor.DecryptorDA;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.dataprovider.inventory.Item;
import exter.eveindustry.dataprovider.planet.Planet;
import exter.eveindustry.dataprovider.planet.PlanetBuilding;
import exter.eveindustry.dataprovider.planet.PlanetBuildingDA;
import exter.eveindustry.dataprovider.planet.PlanetDA;
import exter.eveindustry.dataprovider.reaction.Reaction;
import exter.eveindustry.dataprovider.reaction.ReactionDA;
import exter.eveindustry.dataprovider.refine.Refinable;
import exter.eveindustry.dataprovider.refine.RefinableDA;
import exter.eveindustry.dataprovider.starbase.StarbaseTower;
import exter.eveindustry.dataprovider.starbase.StarbaseTowerDA;
import exter.eveindustry.dataprovider.systemcost.DummySystemCost;
import exter.eveindustry.task.Task.Market;


public class EVEDataProvider implements IEVEDataProvider
{
  private InventoryDA da_inventory;
  private BlueprintDA da_blueprint;
  private InstallationDA da_installation;
  private DecryptorDA da_decryptor;
  private PlanetBuildingDA da_planetbuilding;
  private PlanetDA da_planet;
  private ReactionDA da_reaction;
  private RefinableDA da_refinable;
  private StarbaseTowerDA da_tower;
  
  public EVEDataProvider(File eid_zip)
  {
    da_inventory = new InventoryDA(eid_zip);
    da_blueprint = new BlueprintDA(eid_zip,da_inventory);
    da_installation = new InstallationDA(eid_zip);
    da_decryptor = new DecryptorDA(eid_zip,da_inventory);
    da_planetbuilding = new PlanetBuildingDA(eid_zip,da_inventory);
    da_planet = new PlanetDA(eid_zip,da_inventory);
    da_reaction = new ReactionDA(eid_zip,da_inventory);
    da_refinable = new RefinableDA(eid_zip,da_inventory);
    da_tower = new StarbaseTowerDA(eid_zip,da_inventory);
  }
  
  @Override
  public int getDefaultSolarSystem()
  {
    return 30000142;
  }

  @Override
  public Item getItem(int item_id)
  {
    return da_inventory.items.get(item_id);
  }

  @Override
  public Blueprint getBlueprint(int blueprint_id)
  {
    return da_blueprint.blueprints.get(blueprint_id);
  }

  @Override
  public InstallationGroup getDefaultInstallation(IBlueprint blueprint)
  {
    for(InstallationGroup ig:da_installation.group_installations.get(blueprint.getProduct().item.getGroupID()))
    {
      if(ig.InstallationID == 6)
      {
        return ig;
      }
    }
    return null;
  }

  @Override
  public InstallationGroup getInstallationGroup(int inst_group_id)
  {
    return da_installation.installation_groups.get(inst_group_id);
  }

  @Override
  public InventionInstallation getInventionInstallation(int inv_inst_id)
  {
    return da_installation.invention_installations.get(inv_inst_id);
  }

  @Override
  public InventionInstallation getDefaultInventionInstallation(IBlueprint blueprint)
  {
    int id = blueprint.getInvention().usesRelics()?38:151;
    return da_installation.invention_installations.get(id);
  }

  @Override
  public Decryptor getDecryptor(int decryptor_id)
  {
    return da_decryptor.decryptors.get(decryptor_id);
  }

  @Override
  public Planet getPlanet(int planet_id)
  {
    return da_planet.planets.get(planet_id);
  }

  @Override
  public PlanetBuilding getPlanetBuilding(int building_id)
  {
    return da_planetbuilding.buildings.get(building_id);
  }

  @Override
  public PlanetBuilding getPlanetBuilding(IItem building_product)
  {
    return da_planetbuilding.buildings.get(building_product.getID());
  }

  @Override
  public Reaction getReaction(int reaction_id)
  {
    return da_reaction.reactions.get(reaction_id);
  }

  @Override
  public Refinable getRefinable(int refinable_id)
  {
    return da_refinable.refinables.get(refinable_id);
  }

  @Override
  public StarbaseTower getStarbaseTower(int tower_id)
  {
    return da_tower.towers.get(tower_id);
  }

  @Override
  public BigDecimal getItemBaseCost(IItem item)
  {
    return BigDecimal.ZERO;
  }

  @Override
  public ISolarSystemIndustryCost getSolarSystemIndustryCost(int system_id)
  {
    return new DummySystemCost(system_id);
  }

  @Override
  public BigDecimal getMarketPrice(IItem item, Market market)
  {
    return BigDecimal.ZERO;
  }

  @Override
  public int getIndustrySkillID()
  {
    return 3380;
  }

  @Override
  public int getAdvancedIndustrySkillID()
  {
    return 3388;
  }

  @Override
  public int getRefiningSkillID()
  {
    return 3385;
  }

  @Override
  public int getRefineryEfficiencySkillID()
  {
    return 3389;
  }

  @Override
  public int getDefaultSkillLevel(int skill_id)
  {
    return 0;
  }

  @Override
  public Market getDefaultProducedMarket()
  {
    return new Market();
  }

  @Override
  public Market getDefaultRequiredMarket()
  {
    return new Market();
  }

  @Override
  public int getDefaultBlueprintME(IBlueprint bp)
  {
    return 0;
  }

  @Override
  public int getDefaultBlueprintTE(IBlueprint bp)
  {
    return 0;
  }
}
