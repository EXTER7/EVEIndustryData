package exter.eveindustry.dataprovider.decryptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class DecryptorDA
{
  public final Map<Integer, Decryptor> decryptors = new HashMap<Integer, Decryptor>();

  public DecryptorDA(File eid_zip,ItemDA inventory)
  {
    ZipFile zip;
    try
    {
      zip = new ZipFile(eid_zip);
      TSLReader tsl = null;
      InputStream raw = null;
      try
      {
        raw = zip.getInputStream(zip.getEntry("blueprint/decryptors.tsl"));
        tsl = new TSLReader(raw);

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
            Decryptor d = new Decryptor(new TSLObject(tsl),inventory);
            decryptors.put(d.getID(), d);
          }
        }
        raw.close();
      } catch(InvalidTSLException e)
      {
        throw new RuntimeException(e);
      } catch(IOException e)
      {
        throw new RuntimeException(e);
      }
      zip.close();
    } catch(IOException e1)
    {
      throw new RuntimeException(e1);
    }
  }
}
