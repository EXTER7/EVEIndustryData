package exter.eveindustry.dataprovider;

import java.math.BigDecimal;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.blueprint.IInstallationGroup;
import exter.eveindustry.data.blueprint.IInventionInstallation;
import exter.eveindustry.data.decryptor.IDecryptor;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.data.planet.IPlanet;
import exter.eveindustry.data.planet.IPlanetBuilding;
import exter.eveindustry.data.reaction.IReaction;
import exter.eveindustry.data.reaction.IStarbaseTower;
import exter.eveindustry.data.refinable.IRefinable;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.dataprovider.blueprint.BlueprintDA;
import exter.eveindustry.dataprovider.blueprint.InstallationDA;
import exter.eveindustry.dataprovider.blueprint.InstallationGroup;
import exter.eveindustry.dataprovider.decryptor.DecryptorDA;
import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.dataprovider.planet.PlanetBuildingDA;
import exter.eveindustry.dataprovider.planet.PlanetDA;
import exter.eveindustry.dataprovider.reaction.ReactionDA;
import exter.eveindustry.dataprovider.refine.RefinableDA;
import exter.eveindustry.dataprovider.starbase.StarbaseTowerDA;
import exter.eveindustry.dataprovider.systemcost.DummySystemCost;
import exter.eveindustry.task.Task.Market;


public class EVEDataProvider implements IEVEDataProvider
{
  @Override
  public int getDefaultSolarSystem()
  {
    return 30000142;
  }

  @Override
  public IItem getItem(int item_id)
  {
    return InventoryDA.items.get(item_id);
  }

  @Override
  public IBlueprint getBlueprint(int blueprint_id)
  {
    return BlueprintDA.blueprints.get(blueprint_id);
  }

  @Override
  public IInstallationGroup getDefaultInstallation(IBlueprint blueprint)
  {
    for(InstallationGroup ig:InstallationDA.group_installations.get(blueprint.getProduct().item.getGroupID()))
    {
      if(ig.InstallationID == 6)
      {
        return ig;
      }
    }
    return null;
  }

  @Override
  public IInstallationGroup getInstallationGroup(int inst_group_id)
  {
    return InstallationDA.installation_groups.get(inst_group_id);
  }

  @Override
  public IInventionInstallation getInventionInstallation(int inv_inst_id)
  {
    return InstallationDA.invention_installations.get(inv_inst_id);
  }

  @Override
  public IInventionInstallation getDefaultInventionInstallation(IBlueprint blueprint)
  {
    int id = blueprint.getInvention().usesRelics()?38:151;
    return  InstallationDA.invention_installations.get(id);
  }

  @Override
  public IDecryptor getDecryptor(int decryptor_id)
  {
    return DecryptorDA.decryptors.get(decryptor_id);
  }

  @Override
  public IPlanet getPlanet(int planet_id)
  {
    return PlanetDA.planets.get(planet_id);
  }

  @Override
  public IPlanet getDefaultPlanet()
  {
    return PlanetDA.planets.get(2015);
  }

  @Override
  public IPlanetBuilding getPlanetBuilding(int building_id)
  {
    return PlanetBuildingDA.buildings.get(building_id);
  }

  @Override
  public IPlanetBuilding getPlanetBuilding(IItem building_product)
  {
    return PlanetBuildingDA.buildings.get(building_product.getID());
  }

  @Override
  public IReaction getReaction(int reaction_id)
  {
    return ReactionDA.reactions.get(reaction_id);
  }

  @Override
  public IRefinable getRefinable(int refinable_id)
  {
    return RefinableDA.refinables.get(refinable_id);
  }

  @Override
  public IStarbaseTower getStarbaseTower(int tower_id)
  {
    return StarbaseTowerDA.towers.get(tower_id);
  }

  @Override
  public IStarbaseTower getDefaultStarbaseTower()
  {
    return StarbaseTowerDA.towers.get(16213);
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
