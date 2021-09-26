package com.example.demo.libraries;

import java.io.Serializable;
import java.util.WeakHashMap;

public class SerializableWeakHashMap<K, V> extends WeakHashMap<K, V> implements Serializable { }
