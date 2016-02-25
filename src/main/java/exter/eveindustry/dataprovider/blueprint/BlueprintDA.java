package exter.eveindustry.dataprovider.blueprint;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class BlueprintDA
{
  private class BlueprintMissListener implements Cache.IMissListener<Integer, Blueprint>, IFileSystemHandler.IReadHandler<Blueprint>
  {
    @Override
    public Blueprint onCacheMiss(Integer key)
    {
      return fs.readFile("blueprint/" + String.valueOf(key) + ".tsl", this);
    }

    @Override
    public Blueprint readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        return new Blueprint(new TSLObject(reader),inventory,installations);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, Blueprint> blueprints = new Cache<Integer, Blueprint>(new BlueprintMissListener());

  private final IFileSystemHandler fs;
  private final ItemDA inventory;
  private final InstallationDA installations;
  
  public final Index index;
  
  public BlueprintDA(IFileSystemHandler fs,ItemDA inventory,InstallationDA installations)
  {
    this.fs = fs;
    this.inventory = inventory;
    this.installations = installations;
    this.index = new Index(fs,"blueprint/index.tsl");
  }
}
