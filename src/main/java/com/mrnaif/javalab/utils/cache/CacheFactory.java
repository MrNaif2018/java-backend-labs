package com.mrnaif.javalab.utils.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CacheFactory {

  private ConcurrentHashMap<Class<? extends Object>, GenericCache<String, ? extends Object>>
      caches = new ConcurrentHashMap<>();

  public <K, V> GenericCache<K, V> getCache(Class<V> type) {
    @SuppressWarnings("unchecked")
    GenericCache<String, V> cache =
        (GenericCache<String, V>) caches.computeIfAbsent(type, k -> new SimpleCache<>());
    return new GenericCache<K, V>() {
      private String generateKey(K key) {
        return type.getSimpleName() + ":" + key;
      }

      @Override
      public Optional<V> get(K key) {
        return cache.get(generateKey(key));
      }

      @Override
      public void put(K key, V value) {
        cache.put(generateKey(key), value);
      }

      @Override
      public void invalidate(K key) {
        cache.invalidate(generateKey(key));
      }

      @Override
      public void clear() {
        cache.clear();
      }
    };
  }
}
