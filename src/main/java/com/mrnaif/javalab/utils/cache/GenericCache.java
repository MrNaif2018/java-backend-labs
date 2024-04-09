package com.mrnaif.javalab.utils.cache;

import java.util.Optional;

public interface GenericCache<K, V> {

  void put(K key, V value);

  Optional<V> get(K key);

  void invalidate(K key);

  void clear();
}
