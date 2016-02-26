package exter.eveindustry.dataprovider.starmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class Starmap
{
  private class SolarSystemCacheMiss implements Cache.IMissListener<Integer, SolarSystem>, IFileSystemHandler.IReadHandler<SolarSystem>
  {
    @Override
    public SolarSystem onCacheMiss(Integer id)
    {
      return fs.readFile("solarsystem/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public SolarSystem readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new SolarSystem(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  private class RegionCacheMiss implements Cache.IMissListener<Integer, Region>, IFileSystemHandler.IReadHandler<Region>
  {
    @Override
    public Region onCacheMiss(Integer id)
    {
      return fs.readFile("solarsystem/region/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public Region readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new Region(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public class SolarSystemIterator implements Iterator<SolarSystem>
  {
    private final SolarSystemCacheMiss loader;
    private final List<String> files;
    private int index;

    public SolarSystemIterator()
    {
      loader = new SolarSystemCacheMiss();
      files = fs.listDirectoryFiles("solarsystem");
      index = 0;
    }
    
    @Override
    public boolean hasNext()
    {
      return index < files.size();
    }

    @Override
    public SolarSystem next()
    {
      return fs.readFile(files.get(index++), loader);
    }
  }

  public class RegionIterator implements Iterator<Region>
  {
    private final RegionCacheMiss loader;
    private final List<String> files;
    private int index;

    public RegionIterator()
    {
      loader = new RegionCacheMiss();
      files = fs.listDirectoryFiles("solarsystem/region");
      index = 0;
    }
    
    @Override
    public boolean hasNext()
    {
      return index < files.size();
    }

    @Override
    public Region next()
    {
      return fs.readFile(files.get(index++), loader);
    }
  }

  public final Cache<Integer, SolarSystem> systems = new Cache<Integer, SolarSystem>(new SolarSystemCacheMiss());
  public final Cache<Integer, Region> regions = new Cache<Integer, Region>(new RegionCacheMiss());

  private final IFileSystemHandler fs;
  
  public Starmap(IFileSystemHandler fs)
  {
    this.fs = fs;
  }
}
