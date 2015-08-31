package exter.eveindustry.dataprovider.starmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class Starmap
{
  public final Map<Integer, SolarSystem> systems = new HashMap<Integer, SolarSystem>();

  public Starmap(File eid_zip)
  {
    ZipFile zip;
    try
    {
      zip = new ZipFile(eid_zip);
      try
      {
        InputStream raw = zip.getInputStream(zip.getEntry("starmap.tsl"));
        TSLReader reader = new TSLReader(raw);

        reader.moveNext();

        while(true)
        {
          reader.moveNext();
          TSLReader.State type = reader.getState();
          if(type == TSLReader.State.ENDOBJECT)
          {
            break;
          } else if(type == TSLReader.State.OBJECT)
          {
            String node_name = reader.getName();
            if(node_name.equals("s"))
            {
              TSLObject obj = new TSLObject(reader);
              systems.put(obj.getStringAsInt("id", -1), new SolarSystem(obj));
            } else
            {
              reader.skipObject();
            }
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
