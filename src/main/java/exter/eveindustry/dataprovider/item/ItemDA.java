package exter.eveindustry.dataprovider.item;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.eveindustry.dataprovider.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class ItemDA
{
  private class ItemCacheMiss implements Cache.IMissListener<Integer, Item>, IFileSystemHandler.IReadHandler<Item>
  {
    @Override
    public Item onCacheMiss(Integer id)
    {
      return fs.readFile("item/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public Item readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new Item(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  private class ItemGroupCacheMiss implements Cache.IMissListener<Integer, ItemGroup>, IFileSystemHandler.IReadHandler<ItemGroup>
  {
    @Override
    public ItemGroup onCacheMiss(Integer id)
    {
      return fs.readFile("item/group/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public ItemGroup readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new ItemGroup(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  private class ItemCategoryCacheMiss implements Cache.IMissListener<Integer, ItemCategory>, IFileSystemHandler.IReadHandler<ItemCategory>
  {
    @Override
    public ItemCategory onCacheMiss(Integer id)
    {
      return fs.readFile("item/category/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public ItemCategory readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new ItemCategory(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  private class ItemMetaGroupCacheMiss implements Cache.IMissListener<Integer, ItemMetaGroup>, IFileSystemHandler.IReadHandler<ItemMetaGroup>
  {
    @Override
    public ItemMetaGroup onCacheMiss(Integer id)
    {
      return fs.readFile("item/metagroup/" + String.valueOf(id) + ".tsl", this);
    }

    @Override
    public ItemMetaGroup readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        TSLObject obj = new TSLObject(reader);
        return new ItemMetaGroup(obj);
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }

  public final Cache<Integer, Item> items = new Cache<Integer, Item>(new ItemCacheMiss());
  public final Cache<Integer, ItemGroup> groups = new Cache<Integer, ItemGroup>(new ItemGroupCacheMiss());
  public final Cache<Integer, ItemCategory> categories = new Cache<Integer, ItemCategory>(new ItemCategoryCacheMiss());
  public final Cache<Integer, ItemMetaGroup> metagroups = new Cache<Integer, ItemMetaGroup>(new ItemMetaGroupCacheMiss());
  private final IFileSystemHandler fs;
  
  public ItemDA(IFileSystemHandler fs)
  {
    this.fs = fs;
  }
}
