package repo.cache.interfaces;

import java.util.List;

public interface CacheDAO<T> {
    void put(T messageId);
    void put(List<T> messageIds);
    void remove(T messageId);
}
