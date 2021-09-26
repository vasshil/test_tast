package com.example.demo.libraries;

import java.io.Serializable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

public class SerializableDelayQueue<E extends Delayed> extends DelayQueue<E> implements Serializable { }
