package exter.eveindustry.dataprovider.planet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetDA
{

  static public final Map<Integer, Planet> planets = new HashMap<Integer, Planet>();

  static
  {
    ZipFile zip;
    try
    {
      zip = new ZipFile("test_eid.zip");
      try
      {
        InputStream raw = zip.getInputStream(zip.getEntry("planet/planets.tsl"));

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
            Planet p = new Planet(new TSLObject(tsl));
            planets.put(p.ID, p);
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
