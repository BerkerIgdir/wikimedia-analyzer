package repo.interfaces;

import repo.pojo.WikiMediaRecentChangesStatsPojo;

import java.util.Collection;

public interface WikiMediaApiRepo {
    void save(WikiMediaRecentChangesStatsPojo pojo);
    void save(Collection<WikiMediaRecentChangesStatsPojo> pojoList);
}
