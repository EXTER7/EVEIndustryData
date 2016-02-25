package exter.eveindustry.dataprovider.blueprint;

import java.io.IOException;
import java.io.InputStream;

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

  public final Cache<Integer, InstallationGroup> installation_groups = new Cache<Integer, InstallationGroup>(new InstallationGroupCacheMiss());
  public final Cache<Integer, InventionInstallation> invention_installations = new Cache<Integer, InventionInstallation>(new InventionInstallationCacheMiss());
  IFileSystemHandler fs;
  
  public InstallationDA(IFileSystemHandler fs)
  {
    this.fs = fs;
  }
}
