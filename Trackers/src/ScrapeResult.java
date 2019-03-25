public class ScrapeResult {
    private int seeders;
    private int leechers;
    private int downloaded;

    public int getSeeders() {
        return seeders;
    }

    public int getLeechers() {
        return leechers;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public ScrapeResult(int seeders, int leechers, int downloaded) {
        this.seeders=seeders;
        this.leechers=leechers;
        this.downloaded=downloaded;
    }
    public ScrapeResult(){
        seeders=-1; leechers=-1; downloaded=-1;
    }

    public void print(){
        System.out.println("Seedes: "+seeders + "  Leeches: " + leechers + "  Downloaded: " + downloaded);
    }
}
