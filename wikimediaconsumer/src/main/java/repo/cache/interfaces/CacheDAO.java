package repo.cache.interfaces;

import java.util.List;
import java.util.Optional;

public interface CacheDAO<T> {
    void put(List<T> dto);
    void remove(T dto);
    Optional<T> get(T dto);
}
