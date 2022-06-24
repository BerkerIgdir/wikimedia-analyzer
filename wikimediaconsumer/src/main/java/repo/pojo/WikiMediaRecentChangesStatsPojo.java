package repo.pojo;

public final class WikiMediaRecentChangesStatsPojo {

    public final String id;
    public final long timestamp;
    public final String user;
    public final boolean bot;
    public final String wiki;

    public WikiMediaRecentChangesStatsPojo(String id, long timestamp, String user, boolean bot, String wiki) {
        this.id = id;
        this.timestamp = timestamp;
        this.user = user;
        this.bot = bot;
        this.wiki = wiki;
    }
}
