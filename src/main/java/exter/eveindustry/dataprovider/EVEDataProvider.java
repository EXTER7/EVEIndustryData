package exter.eveindustry.dataprovider;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.dataprovider.blueprint.Blueprint;
import exter.eveindustry.dataprovider.blueprint.BlueprintDA;
import exter.eveindustry.dataprovider.blueprint.Installation;
import exter.eveindustry.dataprovider.blueprint.InstallationDA;
import exter.eveindustry.dataprovider.blueprint.InstallationGroup;
import exter.eveindustry.dataprovider.blueprint.InventionInstallation;
import exter.eveindustry.dataprovider.decryptor.Decryptor;
import exter.eveindustry.dataprovider.decryptor.DecryptorDA;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.Item;
import exter.eveindustry.dataprovider.item.ItemCategory;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.eveindustry.dataprovider.item.ItemGroup;
import exter.eveindustry.dataprovider.item.ItemMetaGroup;
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
import exter.eveindustry.dataprovider.starmap.Region;
import exter.eveindustry.dataprovider.starmap.SolarSystem;
import exter.eveindustry.dataprovider.starmap.Starmap;
import exter.eveindustry.dataprovider.systemcost.DummySystemCost;
import exter.eveindustry.task.Task.Market;


public class EVEDataProvider implements IEVEDataProvider
{
  private final ItemDA da_inventory;
  private final BlueprintDA da_blueprint;
  private final InstallationDA da_installation;
  private final DecryptorDA da_decryptor;
  private final PlanetBuildingDA da_planetbuilding;
  private final PlanetDA da_planet;
  private final ReactionDA da_reaction;
  private final RefinableDA da_refinable;
  private final StarbaseTowerDA da_tower;
  private final Starmap da_starmap;
  
  public EVEDataProvider(IFileSystemHandler fs)
  {
    da_inventory = new ItemDA(fs);
    da_installation = new InstallationDA(fs);
    da_blueprint = new BlueprintDA(fs,da_inventory,da_installation);
    da_decryptor = new DecryptorDA(fs,da_inventory);
    da_planetbuilding = new PlanetBuildingDA(fs,da_inventory);
    da_planet = new PlanetDA(fs,da_inventory);
    da_reaction = new ReactionDA(fs,da_inventory);
    da_refinable = new RefinableDA(fs,da_inventory);
    da_tower = new StarbaseTowerDA(fs,da_inventory);
    da_starmap = new Starmap(fs);
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

  public ItemGroup getItemGroup(int group_id)
  {
    return da_inventory.groups.get(group_id);
  }

  public ItemCategory getItemCategory(int category_id)
  {
    return da_inventory.categories.get(category_id);
  }

  public ItemMetaGroup getItemMetaGroup(int mg_id)
  {
    return da_inventory.metagroups.get(mg_id);
  }

  @Override
  public Blueprint getBlueprint(int blueprint_id)
  {
    return da_blueprint.blueprints.get(blueprint_id);
  }

  @Override
  public InstallationGroup getDefaultInstallation(IBlueprint blueprint)
  {
    return ((Blueprint)blueprint).Installation;
  }

  public Installation getInstallation(int inst_id)
  {
    return da_installation.installations.get(inst_id);
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

  public Collection<Decryptor> allDecryptors()
  {
    return Collections.unmodifiableCollection(da_decryptor.decryptors.values());
  }

  @Override
  public Planet getPlanet(int planet_id)
  {
    return da_planet.planets.get(planet_id);
  }

  public Collection<Planet> allPlanets()
  {
    return Collections.unmodifiableCollection(da_planet.planets.values());
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

  public Collection<StarbaseTower> allStarbaseTowers()
  {
    return Collections.unmodifiableCollection(da_tower.towers.values());
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
  
  public SolarSystem getSolarSystem(int ss_id)
  {
    return da_starmap.systems.get(ss_id);
  }

  public Region getSolarSystemRegion(int region_id)
  {
    return da_starmap.regions.get(region_id);
  }

  public Index getBlueprintIndex()
  {
    return da_blueprint.index;
  }
  
  public Index getRefinableIndex()
  {
    return da_refinable.index;
  }

  public Index getReactionIndex()
  {
    return da_reaction.index;
  }

  public Index getMoonProductIndex()
  {
    return da_reaction.index_moon;
  }
  
  public Index getPlanetProductIndex(boolean include_advanced)
  {
    return include_advanced?da_planetbuilding.index_adv:da_planetbuilding.index;
  }
  
  public Iterator<Item> allItems()
  {
    return da_inventory.new ItemIterator();
  }

  public Iterator<ItemGroup> allItemGroups()
  {
    return da_inventory.new ItemGroupIterator();
  }

  public Iterator<ItemCategory> allItemCategories()
  {
    return da_inventory.new ItemCategoryIterator();
  }

  public Iterator<ItemMetaGroup> allItemMetaGroups()
  {
    return da_inventory.new ItemMetaGroupIterator();
  }

  public Iterator<Installation> allInstallations()
  {
    return da_installation.new InstallationIterator();
  }

  public Iterator<InstallationGroup> allInstallationGroups()
  {
    return da_installation.new InstallationGroupIterator();
  }

  public Iterator<InventionInstallation> allInventionInstallations()
  {
    return da_installation.new InventionInstallationIterator();
  }

  public Iterator<SolarSystem> allSolarSystems()
  {
    return da_starmap.new SolarSystemIterator();
  }

  public Iterator<Region> allSolarSystemRegions()
  {
    return da_starmap.new RegionIterator();
  }
}
