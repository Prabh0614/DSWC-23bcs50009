import java.util.LinkedHashMap;
import java.util.Map;

class VideoCache<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public VideoCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class VideoCacheDemo {

    public static void main(String[] args) {

        VideoCache<Integer, String> cache = new VideoCache<>(100);

        cache.put(1, "Movie A");
        cache.put(2, "Movie B");
        cache.put(3, "Movie C");
        cache.put(4, "Movie D");
        cache.put(5, "Movie E");

        cache.get(1);

        for (int i = 6; i <= 101; i++) {
            cache.put(i, "Movie " + i);
        }

        System.out.println("Cache Size: " + cache.size());
        System.out.println("Contains Movie 1: " + cache.containsKey(1));
        System.out.println("Contains Movie 2: " + cache.containsKey(2));

        System.out.println(cache);
    }
}