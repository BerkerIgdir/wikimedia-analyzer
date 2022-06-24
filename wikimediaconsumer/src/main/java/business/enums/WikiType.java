package business.enums;

public enum WikiType {
    TRWIKI("trwiki"),
    ENWIKI("enwiki"),
    DEWIKI("dewiki"),
    COMMONSWIKI("commonswiki"),
    DATAWIKI("wikidatawiki");

    private final String wikiType;

    WikiType(String wikiType) {
        this.wikiType = wikiType;
    }

    public String getWikiType() {
        return this.wikiType;
    }

}
