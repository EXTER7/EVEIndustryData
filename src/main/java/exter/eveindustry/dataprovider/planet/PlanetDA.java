package exter.eveindustry.dataprovider.planet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetDA implements IFileSystemHandler.IReadHandler<Object>
{
  public final Map<Integer, Planet> planets = new HashMap<Integer, Planet>();
  private final ItemDA inventory;
  
  public PlanetDA(IFileSystemHandler fs,ItemDA inventory)
  {
    this.inventory = inventory;
    fs.readFile("planet/planets.tsl", this);
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
          Planet p = new Planet(new TSLObject(tsl),inventory);
          planets.put(p.ID, p);
        }
      }
    } catch(InvalidTSLException e)
    {
      throw new RuntimeException(e);
    }
    return null;
  }
}
