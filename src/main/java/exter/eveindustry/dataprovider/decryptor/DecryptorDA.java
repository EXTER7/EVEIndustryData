package exter.eveindustry.dataprovider.decryptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.eveindustry.dataprovider.item.ItemDA;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class DecryptorDA implements IFileSystemHandler.IReadHandler<Object>
{
  public final Map<Integer, Decryptor> decryptors = new HashMap<Integer, Decryptor>();

  private final ItemDA inventory;
  
  public DecryptorDA(IFileSystemHandler fs,ItemDA inventory)
  {
    this.inventory = inventory;
    fs.readFile("blueprint/decryptors.tsl", this);
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
          Decryptor d = new Decryptor(new TSLObject(tsl),inventory);
          decryptors.put(d.getID(), d);
        }
      }
    } catch(InvalidTSLException e)
    {
      throw new RuntimeException(e);
    }
    return null;
  }
}
