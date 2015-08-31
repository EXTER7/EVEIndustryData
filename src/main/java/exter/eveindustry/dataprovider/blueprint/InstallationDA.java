package exter.eveindustry.dataprovider.blueprint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class InstallationDA
{
  public final Map<Integer, InventionInstallation> invention_installations = new HashMap<Integer, InventionInstallation>();
  public final Map<Integer, InstallationGroup> installation_groups = new HashMap<Integer, InstallationGroup>();
  public final Map<Integer, List<InstallationGroup>> group_installations = new HashMap<Integer, List<InstallationGroup>>();
  
  public InstallationDA(File eid_zip)
  {
    ZipFile zip;
    try
    {
      zip = new ZipFile(eid_zip);

      TSLReader reader = null;
      InputStream raw = null;
      try
      {
        raw = zip.getInputStream(zip.getEntry("blueprint/installations.tsl"));
        reader = new TSLReader(raw);
      } catch(IOException e)
      {
        throw new RuntimeException(e);
      }

      try
      {
        reader.moveNext();

        while(true)
        {
          reader.moveNext();
          TSLReader.State type = reader.getState();
          if(type == TSLReader.State.ENDOBJECT)
          {
            break;
          }

          if(type == TSLReader.State.OBJECT)
          {
            if(reader.getName().equals("group"))
            {
              TSLObject obj = new TSLObject(reader);
              InstallationGroup ins = new InstallationGroup(obj);
              installation_groups.put(ins.ID, ins);
              List<InstallationGroup> inslist = group_installations.get(ins.GroupID);
              if(inslist == null)
              {
                inslist = new ArrayList<InstallationGroup>();
                group_installations.put(ins.GroupID,inslist);
              }
              inslist.add(new InstallationGroup(obj));
            } else if(reader.getName().equals("invention"))
            {
              TSLObject obj = new TSLObject(reader);
              invention_installations.put(obj.getStringAsInt("id", -1), new InventionInstallation(obj));
            } else
            {
              reader.skipObject();
            }
          }
        }
      } catch(InvalidTSLException e)
      {
        raw.close();
        zip.close();
        throw new RuntimeException(e);
      } catch(IOException e)
      {
        raw.close();
        zip.close();
        throw new RuntimeException(e);
      }

      try
      {
        raw.close();
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
