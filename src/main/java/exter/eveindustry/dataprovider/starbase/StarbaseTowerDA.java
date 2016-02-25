package exter.eveindustry.dataprovider.starbase;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class StarbaseTowerDA implements IFileSystemHandler.IReadHandler<Object>
{
  public final Map<Integer, StarbaseTower> towers = new HashMap<Integer, StarbaseTower>();
  private final ItemDA inventory;
  
  public StarbaseTowerDA(IFileSystemHandler fs,ItemDA inventory)
  {
    this.inventory = inventory;
    fs.readFile("starbases.tsl", this);
  }

  @Override
  public Object readFile(InputStream stream) throws IOException
  {
    try
    {
      TSLReader tsl = new TSLReader(stream);
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
    } catch(InvalidTSLException e)
    {
      throw new RuntimeException(e);
    }
    return null;
  }
}
