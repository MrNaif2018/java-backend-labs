package com.mrnaif.javalab.utils.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> implements GenericCache<K, V> {

  private ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

  private static final int MAX_ITEMS = 100;

  public void put(K key, V value) {
    if (cache.size() == MAX_ITEMS) {
      clear();
    }
    cache.put(key, value);
  }

  public Optional<V> get(K key) {
    V value = cache.get(key);
    return Optional.ofNullable(value);
  }

  public void invalidate(K key) {
    cache.remove(key);
  }

  public void clear() {
    cache.clear();
  }
}
