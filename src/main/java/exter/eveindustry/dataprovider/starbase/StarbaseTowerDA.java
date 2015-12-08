package exter.eveindustry.dataprovider.starbase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.inventory.InventoryDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class StarbaseTowerDA
{
  public final Map<Integer, StarbaseTower> towers = new HashMap<Integer, StarbaseTower>();

  public StarbaseTowerDA(File eid_zip,InventoryDA inventory)
  {
    ZipFile zip;
    try
    {
      zip = new ZipFile(eid_zip);
      try
      {
        InputStream raw = zip.getInputStream(zip.getEntry("starbases.tsl"));
        TSLReader tsl = new TSLReader(raw);

        tsl.moveNext();

        while(true)
        {
          tsl.moveNext();
          TSLReader.State type = tsl.getState();
          if(type == TSLReader.State.ENDOBJECT)
          {
            break;
          }

          if(type == TSLReader.State.OBJECT)
          {
            StarbaseTower t = new StarbaseTower(new TSLObject(tsl),inventory);
            towers.put(t.TowerItem.ID, t);
          }
        }
        raw.close();
      } catch(InvalidTSLException e)
      {
        zip.close();
        throw new RuntimeException(e);
      } catch(IOException e)
      {
        zip.close();
        throw new RuntimeException(e);
      }
      zip.close();
    } catch(IOException e1)
    {
      throw new RuntimeException(e1);
    }
  }
}
