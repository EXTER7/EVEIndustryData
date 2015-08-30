package exter.eveindustry.dataprovider.planet;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetBuildingDA
{

  static public final Cache<Integer, PlanetBuilding> buildings = new Cache<Integer, PlanetBuilding>(new ProductCacheMiss());

  static private class ProductCacheMiss implements Cache.IMissListener<Integer, PlanetBuilding>
  {
    @Override
    public PlanetBuilding onCacheMiss(Integer pid)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile("test_eid.zip");
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("planet/" + String.valueOf(pid) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("planetbuilding"))
          {
            raw.close();
            zip.close();
            return new PlanetBuilding(new TSLObject(reader));
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
}
