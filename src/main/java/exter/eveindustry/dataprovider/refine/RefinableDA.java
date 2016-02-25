package exter.eveindustry.dataprovider.refine;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.index.Index;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class RefinableDA
{
  private class RefineCacheMiss implements Cache.IMissListener<Integer, Refinable>, IFileSystemHandler.IReadHandler<Refinable>
  {
    @Override
    public Refinable onCacheMiss(Integer refine)
    {
      return fs.readFile("refine/" + String.valueOf(refine) + ".tsl", this);
    }

    @Override
    public Refinable readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        return new Refinable(new TSLObject(reader),inventory);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }  

  public final Cache<Integer, Refinable> refinables = new Cache<Integer, Refinable>(new RefineCacheMiss());
  
  private IFileSystemHandler fs;
  private ItemDA inventory;
  
  public final Index index;
  
  public RefinableDA(IFileSystemHandler fs,ItemDA inventory)
  {
    this.fs = fs;
    this.inventory = inventory;
    this.index = new Index(fs,"refine/index.tsl");
  }

}
