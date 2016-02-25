package exter.eveindustry.dataprovider.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IFileSystemHandler
{
  public interface IReadHandler<T>
  {
    T readFile(InputStream stream) throws IOException;
  }
  
  <T> T readFile(String path,IReadHandler<T> handler);  
  List<String> listDirectoryFiles(String path);
}
