package exter.eveindustry.dataprovider.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class InstallationDA
{
  private class InstallationGroupCacheMiss implements Cache.IMissListener<Integer, InstallationGroup>, IFileSystemHandler.IReadHandler<InstallationGroup>
  {
    @Override
    public InstallationGroup onCacheMiss(Integer id)
    {
      return fs.readFile("blueprint/installation/group/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public InstallationGroup readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new InstallationGroup(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  private class InventionInstallationCacheMiss implements Cache.IMissListener<Integer, InventionInstallation>, IFileSystemHandler.IReadHandler<InventionInstallation>
  {
    @Override
    public InventionInstallation onCacheMiss(Integer id)
    {
      return fs.readFile("blueprint/installation/invention/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public InventionInstallation readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new InventionInstallation(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public class InstallationGroupIterator implements Iterator<InstallationGroup>
  {
    private final InstallationGroupCacheMiss loader;
    private final List<String> files;
    private int index;

    public InstallationGroupIterator()
    {
      loader = new InstallationGroupCacheMiss();
      files = fs.listDirectoryFiles("blueprint/installation/group");
      index = 0;
    }
    
    @Override
    public boolean hasNext()
    {
      return index < files.size();
    }

    @Override
    public InstallationGroup next()
    {
      return fs.readFile(files.get(index++), loader);
    }
  }
  
  
  public final Cache<Integer, InstallationGroup> installation_groups = new Cache<Integer, InstallationGroup>(new InstallationGroupCacheMiss());
  public final Cache<Integer, InventionInstallation> invention_installations = new Cache<Integer, InventionInstallation>(new InventionInstallationCacheMiss());
  IFileSystemHandler fs;
  
  public InstallationDA(IFileSystemHandler fs)
  {
    this.fs = fs;
  }
}
