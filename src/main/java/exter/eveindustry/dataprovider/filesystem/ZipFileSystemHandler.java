package exter.eveindustry.dataprovider.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipFileSystemHandler implements IFileSystemHandler
{
  private final File zip_file;
  public ZipFileSystemHandler(File zip_file)
  {
    this.zip_file = zip_file;
  }
  
  @Override
  public <T> T readFile(String path, IReadHandler<T> handler)
  {
    try
    {
      ZipFile zip = new ZipFile(zip_file);
      try
      {
        InputStream stream = zip.getInputStream(zip.getEntry(path));
        T result = handler.readFile(stream);
        stream.close();
        return result;
      } catch(IOException e)
      {
        return null;
      } finally
      {
        zip.close();
      }
    } catch(IOException e1)
    {
      return null;
    }
  }

  @Override
  public List<String> listDirectoryFiles(String path)
  {
    List<String> result = new ArrayList<String>();
    path = path + "/";
    try
    {
      ZipFile zip = new ZipFile(zip_file);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while(entries.hasMoreElements())
      {
        ZipEntry entry = entries.nextElement();
        if(!entry.isDirectory() && entry.getName().startsWith(path))
        {
          result.add(entry.getName());
        }
      }
      zip.close();
      return result;
    } catch(IOException e1)
    {
      return result;
    }
  }
}
