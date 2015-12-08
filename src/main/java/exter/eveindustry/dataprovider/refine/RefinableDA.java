package exter.eveindustry.dataprovider.refine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class RefinableDA
{
  private class RefineCacheMiss implements Cache.IMissListener<Integer, Refinable>
  {
    @Override
    public Refinable onCacheMiss(Integer refine)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("refine/" + String.valueOf(refine) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("refine"))
          {
            raw.close();
            zip.close();
            return new Refinable(new TSLObject(reader),inventory);
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

  public final Cache<Integer, Refinable> refinables = new Cache<Integer, Refinable>(new RefineCacheMiss());
  
  private File eid_zip;
  private InventoryDA inventory;
  
  public RefinableDA(File eid_zip,InventoryDA inventory)
  {
    this.eid_zip = eid_zip;
    this.inventory = inventory;
  }
}
