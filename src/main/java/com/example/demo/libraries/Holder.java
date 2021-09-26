package com.example.demo.libraries;

import com.example.demo.libraries.ReturnValues;
import com.example.demo.libraries.SelfExpiringHashMap;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTML;
import java.io.*;
import java.util.concurrent.TimeUnit;


@Service
public class Holder {

    private SelfExpiringHashMap<Object, Object> store;

    private final String fileName = "Expiring Map.ser";

    private final long ttl = TimeUnit.MINUTES.toMillis(2); // default ttl


    public Holder() {
        store = new SelfExpiringHashMap<>();
    }

    public Object get(Object key) {
        return store.getOrDefault(key, ReturnValues.NO_SUCH_ELEMENT);
    }

    public boolean set(Object key, Object value, long timeToLive) {
        if (value == null) {
            return false;

        } else {
            if (store.containsKey(key)) {
                store.replace(key, value, timeToLive);
            } else {
                store.put(key, value, timeToLive);
            }

            return true;
        }

    }

    public boolean set(Object key, Object value) {  //  use ttl default
        return set(key, value, ttl);
    }


    public boolean remove(Object key) {
        if (store.containsKey(key)) {
            store.remove(key);
            return true;
        }
        return false;
    }

    public Object dump() throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(store);
        oos.close();
        fos.close();
        System.out.println("Serialized HashMap data is saved in " + fileName);

        return new ObjectInputStream(new FileInputStream(fileName)).readObject();
    }

    public void load() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        store = (SelfExpiringHashMap<Object, Object>) ois.readObject();
        store.setupKeys();
        ois.close();
        fis.close();

    }

    public int size() {
        return store.size();
    }

    public long getLifeTime(Object key) {
        return store.getLifeTime(key);
    }

    public SelfExpiringHashMap<Object, Object> getStore() {
        return store;
    }

    public void clearStore() {
        store.clear();
    }

    @Override
    public String toString() {
        String separate = "<br>==================================<br><br>";
        String output = "<big>Holder</big><br>";
        output += separate;
        output += "Items count = " + size() + "<br>";
        output += "Items:<br>";

        output += store.toString();

        return output;
    }

}
