package exter.eveindustry.dataprovider.reaction;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class ReactionDA
{
  private class ReactionCacheMiss implements Cache.IMissListener<Integer, Reaction>, IFileSystemHandler.IReadHandler<Reaction>
  {
    @Override
    public Reaction onCacheMiss(Integer rid)
    {
      return fs.readFile("reaction/" + String.valueOf(rid) + ".tsl", this);
    }

    @Override
    public Reaction readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        return new Reaction(new TSLObject(reader),inventory);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, Reaction> reactions = new Cache<Integer, Reaction>(new ReactionCacheMiss());

  private IFileSystemHandler fs;
  private ItemDA inventory;
  public final Index index;
  public final Index index_moon;
  
  public ReactionDA(IFileSystemHandler fs, ItemDA inventory)
  {
    this.fs = fs;
    this.inventory = inventory;
    this.index = new Index(fs, "reaction/index.tsl");
    this.index_moon = new Index(fs, "reaction/index_moon.tsl");
  }
}
