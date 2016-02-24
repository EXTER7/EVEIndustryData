package exter.eveindustry.dataprovider.starmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class Starmap
{
  private class SolarSystemCacheMiss implements Cache.IMissListener<Integer, SolarSystem>
  {
    @Override
    public SolarSystem onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("solarsystem/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new SolarSystem(obj);
        } catch(InvalidTSLException e)
        {
          return null;
        } catch(IOException e)
        {
          return null;
        } finally
        {
          zip.close();
        }
      } catch(IOException e1)
      {
        return null;
      }
    }
  }

  private class RegionCacheMiss implements Cache.IMissListener<Integer, Region>
  {
    @Override
    public Region onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("solarsystem/region/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new Region(obj);
        } catch(InvalidTSLException e)
        {
          return null;
        } catch(IOException e)
        {
          return null;
        } finally
        {
          zip.close();
        }
      } catch(IOException e1)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, SolarSystem> systems = new Cache<Integer, SolarSystem>(new SolarSystemCacheMiss());
  public final Cache<Integer, Region> regions = new Cache<Integer, Region>(new RegionCacheMiss());

  private final File eid_zip;
  
  public Starmap(File eid_zip)
  {
    this.eid_zip = eid_zip;
  }
}
