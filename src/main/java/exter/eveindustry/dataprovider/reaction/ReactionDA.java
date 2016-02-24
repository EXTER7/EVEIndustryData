package exter.eveindustry.dataprovider.reaction;

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

public class ReactionDA
{
  private class ReactionCacheMiss implements Cache.IMissListener<Integer, Reaction>
  {
    @Override
    public Reaction onCacheMiss(Integer rid)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("reaction/" + String.valueOf(rid) + ".tsl"));

          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("reaction"))
          {
            raw.close();
            zip.close();
            return new Reaction(new TSLObject(reader),inventory);
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

  public final Cache<Integer, Reaction> reactions = new Cache<Integer, Reaction>(new ReactionCacheMiss());

  private File eid_zip;
  private ItemDA inventory;
  public final Index index;
  public final Index index_moon;
  
  public ReactionDA(File eid_zip, ItemDA inventory)
  {
    this.eid_zip = eid_zip;
    this.inventory = inventory;
    this.index = new Index(eid_zip,"reaction/index.tsl",inventory);
    this.index_moon = new Index(eid_zip,"reaction/index_moon.tsl",inventory);
  }
}
