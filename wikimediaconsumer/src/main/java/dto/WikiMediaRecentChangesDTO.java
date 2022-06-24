package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WikiMediaRecentChangesDTO {

    private Map<String, String> meta;
    private long timestamp;
    private String user;
    private boolean bot;
    private String wiki;

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUser() {
        return user;
    }

    public boolean isBot() {
        return bot;
    }

    public String getWiki() {
        return wiki;
    }
}
