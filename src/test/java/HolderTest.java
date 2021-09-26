import com.example.demo.libraries.Holder;
import com.example.demo.libraries.ReturnValues;
import com.example.demo.libraries.SelfExpiringHashMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HolderTest {


    @Test
    void set() {
        Holder holder = new Holder();

        assertTrue(holder.set("key", "value"));

        holder.set("key", "new value");
        assertEquals("new value", holder.get("key"));

        holder.set("key", "new value", 5000);
        assertEquals(5000, holder.getLifeTime("key"));

        assertFalse(holder.set("key", null));

    }

    @Test
    void get() {
        Holder holder = new Holder();
        holder.set("a", 124, 1000);

        assertEquals(124, holder.get("a"));
        assertEquals(ReturnValues.NO_SUCH_ELEMENT, holder.get("b"));

        holder.remove("a");
        assertEquals(ReturnValues.NO_SUCH_ELEMENT, holder.get("a"));

    }

    @Test
    void remove() {
        Holder holder = new Holder();
        holder.set("a", 124, 1000);

        assertTrue(holder.remove("a"));
        assertFalse(holder.remove("a"));
    }

    @Test
    void size() throws InterruptedException {
        Holder holder = new Holder();
        holder.set("a", 124, 500);
        holder.set("b", 12.4f, 700);
        holder.set("c", "124", 900);
        holder.set("d", new int[] {1, 2, 3}, 1000);
        holder.set("e", TimeUnit.DAYS, 1300);

        assertEquals(5, holder.size());

        Thread.sleep(950);

        assertEquals(2, holder.size());

    }

    @Test
    void dump() throws IOException, ClassNotFoundException {
        Holder holder = new Holder();
        holder.set("a", 124, 500);
        holder.set("b", 12.4f, 700);
        holder.set("c", "124", 900);
        holder.set("d", new int[] {1, 2, 3}, 1000);
        holder.set("e", TimeUnit.DAYS, 1300);


        SelfExpiringHashMap<Object, Object> testStore = (SelfExpiringHashMap<Object, Object>) holder.dump();

        assertEquals(testStore.get("a"), holder.get("a"));
        assertEquals(testStore.size(), holder.size());

    }

    @Test
    void load() throws IOException, ClassNotFoundException {
        Holder holder = new Holder();
        holder.set("a", 124, 5000);
        holder.set("b", 12.4f, 7000);
        holder.set("c", "124", 9000);
        holder.set("d", new int[] {1, 2, 3}, 1000);
        holder.set("e", TimeUnit.DAYS, 1300);


        holder.dump();
        assertEquals(5, holder.size());

        holder.clearStore();
        assertEquals(0, holder.size());

        holder.load();
        assertEquals(5, holder.size());
        assertEquals(124, holder.get("a"));


    }

}