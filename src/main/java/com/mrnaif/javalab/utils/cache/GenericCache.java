package com.mrnaif.javalab.utils.cache;

import java.util.Optional;

public interface GenericCache<K, T> {

  void put(K key, T value);

  Optional<T> get(K key);

  void invalidate(K key);

  void clear();
}
