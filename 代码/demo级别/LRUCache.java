import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int CAHCE_SIZE;

    public LRUCache(int cacheSize) {
        // 装载因子:0,75,保证resize时的确发生在大于CACHE_SIZE时
        // true表示最近访问的在链表头,最早范围的在链表尾
        super((int)Math.ceil(cacheSize / 0.75f)+1, 0.75f, true);
        CAHCE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > CAHCE_SIZE;
    }
}
