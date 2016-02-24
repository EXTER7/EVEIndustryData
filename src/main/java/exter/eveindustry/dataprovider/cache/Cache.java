package exter.eveindustry.dataprovider.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Cache<K,V>
{
  private Map<K,V> cache;
  private Set<K> nulls;
  
  public interface IMissListener<K,V>
  {
    public V onCacheMiss(K key);
  }
  
  protected final IMissListener<K,V> miss;
  
  public Cache(IMissListener<K,V> miss_listener)
  {
    miss = miss_listener;
    cache = new HashMap<K,V>();
    nulls = new HashSet<K>();
  }

  public synchronized V get(K key)
  {
    if(nulls.contains(key))
    {
      return null;
    }
    V value = cache.get(key);
    if(value == null)
    {
      value = miss.onCacheMiss(key);
    }
    if(value != null)
    {
      cache.put(key, value);
    } else
    {
      nulls.add(key);
    }
    return value;
  }

  public synchronized void flushAll()
  {
    cache.clear();
    nulls.clear();
  }

  public synchronized void flush(K key)
  {
    cache.remove(key);
    nulls.remove(key);
  }
}
