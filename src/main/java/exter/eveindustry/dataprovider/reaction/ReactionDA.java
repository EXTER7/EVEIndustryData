package exter.eveindustry.dataprovider.reaction;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class ReactionDA
{
  static public final Cache<Integer, Reaction> reactions = new Cache<Integer, Reaction>(new ReactionCacheMiss());

  static private class ReactionCacheMiss implements Cache.IMissListener<Integer, Reaction>
  {
    @Override
    public Reaction onCacheMiss(Integer rid)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile("test_eid.zip");
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("reaction/" + String.valueOf(rid) + ".tsl"));

          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("reaction"))
          {
            raw.close();
            zip.close();
            return new Reaction(new TSLObject(reader));
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
}
