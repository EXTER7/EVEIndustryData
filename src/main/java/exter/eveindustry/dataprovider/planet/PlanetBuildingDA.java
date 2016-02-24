package exter.eveindustry.dataprovider.planet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetBuildingDA
{
  private class ProductCacheMiss implements Cache.IMissListener<Integer, PlanetBuilding>
  {
    @Override
    public PlanetBuilding onCacheMiss(Integer pid)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_path);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("planet/" + String.valueOf(pid) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("planetbuilding"))
          {
            raw.close();
            zip.close();
            return new PlanetBuilding(new TSLObject(reader), inventory);
          } else
          {
            raw.close();
            zip.close();
            return null;
          }
        } catch(InvalidTSLException e)
        {
          zip.close();
          return null;
        } catch(IOException e)
        {
          zip.close();
          return null;
        }
      } catch(IOException e1)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, PlanetBuilding> buildings = new Cache<Integer, PlanetBuilding>(new ProductCacheMiss());
  
  private File eid_path;
  private ItemDA inventory;
  public final Index index;
  public final Index index_adv;
  
  public PlanetBuildingDA(File eid_zip, ItemDA inventory)
  {
    this.eid_path = eid_zip;
    this.inventory = inventory;
    this.index = new Index(eid_zip,"planet/index.tsl",inventory);
    this.index_adv = new Index(eid_zip,"planet/index_advanced.tsl",inventory);
  }
}
