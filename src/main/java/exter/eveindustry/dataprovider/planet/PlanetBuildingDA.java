package exter.eveindustry.dataprovider.planet;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetBuildingDA
{
  private class ProductCacheMiss implements Cache.IMissListener<Integer, PlanetBuilding>, IFileSystemHandler.IReadHandler<PlanetBuilding>
  {
    @Override
    public PlanetBuilding onCacheMiss(Integer pid)
    {
      return fs.readFile("planet/" + String.valueOf(pid) + ".tsl",this);
    }

    @Override
    public PlanetBuilding readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        return new PlanetBuilding(new TSLObject(reader), inventory);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, PlanetBuilding> buildings = new Cache<Integer, PlanetBuilding>(new ProductCacheMiss());
  
  private IFileSystemHandler fs;
  private ItemDA inventory;
  public final Index index;
  public final Index index_adv;
  
  public PlanetBuildingDA(IFileSystemHandler fs, ItemDA inventory)
  {
    this.fs = fs;
    this.inventory = inventory;
    this.index = new Index(fs,"planet/index.tsl");
    this.index_adv = new Index(fs,"planet/index_advanced.tsl");
  }
}
