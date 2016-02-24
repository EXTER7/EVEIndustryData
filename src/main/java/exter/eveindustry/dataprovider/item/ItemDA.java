package exter.eveindustry.dataprovider.item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import exter.eveindustry.dataprovider.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class ItemDA
{
  private class ItemCacheMiss implements Cache.IMissListener<Integer, Item>
  {
    @Override
    public Item onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("item/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new Item(obj);
        } catch(InvalidTSLException e)
        {
          return null;
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
  }

  private class ItemGroupCacheMiss implements Cache.IMissListener<Integer, ItemGroup>
  {
    @Override
    public ItemGroup onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("item/group/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new ItemGroup(obj);
        } catch(InvalidTSLException e)
        {
          return null;
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
  }

  private class ItemCategoryCacheMiss implements Cache.IMissListener<Integer, ItemCategory>
  {
    @Override
    public ItemCategory onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("item/category/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new ItemCategory(obj);
        } catch(InvalidTSLException e)
        {
          return null;
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
  }

  private class ItemMetaGroupCacheMiss implements Cache.IMissListener<Integer, ItemMetaGroup>
  {
    @Override
    public ItemMetaGroup onCacheMiss(Integer id)
    {
      ZipFile zip;
      try
      {
        zip = new ZipFile(eid_zip);
        try
        {
          InputStream raw = zip.getInputStream(zip.getEntry("item/metagroup/" + String.valueOf(id) + ".tsl"));
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          TSLObject obj = new TSLObject(reader);
          return new ItemMetaGroup(obj);
        } catch(InvalidTSLException e)
        {
          return null;
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
  }

  public final Cache<Integer, Item> items = new Cache<Integer, Item>(new ItemCacheMiss());
  public final Cache<Integer, ItemGroup> groups = new Cache<Integer, ItemGroup>(new ItemGroupCacheMiss());
  public final Cache<Integer, ItemCategory> categories = new Cache<Integer, ItemCategory>(new ItemCategoryCacheMiss());
  public final Cache<Integer, ItemMetaGroup> metagroups = new Cache<Integer, ItemMetaGroup>(new ItemMetaGroupCacheMiss());
  private final File eid_zip;
  
  public ItemDA(File eid_zip)
  {
    this.eid_zip = eid_zip;
  }
}
