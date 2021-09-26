package com.example.demo.libraries;


import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * A HashMap which entries expires after the specified life time.
 * The life-time can be defined on a per-key basis, or using a default one, that is passed to the
 * constructor.
 *
 * @author Pierantonio Cangianiello
 * @param <K> the Key type
 * @param <V> the Value type
 */
public class SelfExpiringHashMap<K, V> implements SelfExpiringMap<K, V> {

    private final Map<K, V> internalMap;

    private final Map<K, ExpiringKey<K>> expiringKeys;

    /**
     * Holds the map keys using the given life time for expiration.
     */
    private final DelayQueue<ExpiringKey<K>> delayQueue = new SerializableDelayQueue<>();

    /**
     * The default max life time in milliseconds.
     */
    private final long maxLifeTimeMillis;

    public SelfExpiringHashMap() {
        internalMap = new ConcurrentHashMap<>();
        expiringKeys = new SerializableWeakHashMap<>();
        this.maxLifeTimeMillis = Long.MAX_VALUE;
    }

    public SelfExpiringHashMap(long defaultMaxLifeTimeMillis) {
        internalMap = new ConcurrentHashMap<>();
        expiringKeys = new WeakHashMap<>();
        this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
    }

    public SelfExpiringHashMap(long defaultMaxLifeTimeMillis, int initialCapacity) {
        internalMap = new ConcurrentHashMap<>(initialCapacity);
        expiringKeys = new WeakHashMap<>(initialCapacity);
        this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
    }

    public SelfExpiringHashMap(long defaultMaxLifeTimeMillis, int initialCapacity, float loadFactor) {
        internalMap = new ConcurrentHashMap<>(initialCapacity, loadFactor);
        expiringKeys = new WeakHashMap<>(initialCapacity, loadFactor);
        this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        cleanup();
        return internalMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        cleanup();
        return internalMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        cleanup();
        return internalMap.containsKey((K) key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        cleanup();
        return internalMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        cleanup();
//        renewKey((K) key);
        return internalMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        return this.put(key, value, maxLifeTimeMillis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value, long lifeTimeMillis) {
        cleanup();
        ExpiringKey<K> delayedKey = new ExpiringKey<>(key, lifeTimeMillis);
        ExpiringKey<K> oldKey = expiringKeys.put(key, delayedKey);
        if(oldKey != null) {
//            expireKey(oldKey);
            expiringKeys.put(key, delayedKey);
        }
        delayQueue.offer(delayedKey);
        return internalMap.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        V removedValue = internalMap.remove(key);
        expireKey(expiringKeys.remove(key));
        return removedValue;
    }


    public long getLifeTime(Object key) {
        return expiringKeys.get(key).getDelay(TimeUnit.MILLISECONDS);
    }

    /**
     * Not supported.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
//        throw new UnsupportedOperationException();
    }

    public V replace(K key, V value, long lifeTimeMillis) {
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
            curValue = put(key, value, lifeTimeMillis);
        }
        return curValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean renewKey(K key) {
        ExpiringKey<K> delayedKey = expiringKeys.get((K) key);
        if (delayedKey != null) {
            delayedKey.renew();
            return true;
        }
        return false;
    }

    private void expireKey(ExpiringKey<K> delayedKey) {
        if (delayedKey != null) {
            delayedKey.expire();
            cleanup();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delayQueue.clear();
        expiringKeys.clear();
        internalMap.clear();
    }

    /**
     * supported
     */
    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    public void setupKeys() {
        Map<K, V> map = new HashMap<>(internalMap);

        clear();

        for (K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    /**
     * Not supported.
     */
    @Override
    public Collection<V> values() {
//        throw new UnsupportedOperationException();
        return null;
    }

    /**
     * Not supported.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
//        throw new UnsupportedOperationException();
        return null;
    }

    private void cleanup() {
        ExpiringKey<K> delayedKey = delayQueue.poll();
        while (delayedKey != null) {
            internalMap.remove(delayedKey.getKey());
            expiringKeys.remove(delayedKey.getKey());
            delayedKey = delayQueue.poll();
        }
    }


    @Override
    public String toString() { String out = "";

        for (Object key : keySet()) {
            out += "Key:<b> " + key + " </b>Value:<b> " + get(key) + " </b>Ttl:<b> " + getLifeTime(key) + "</b><br>";
        }

        return out;
    }

}
