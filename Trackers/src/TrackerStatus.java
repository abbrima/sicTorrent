import java.io.Serializable;

public enum TrackerStatus implements Serializable {
    WORKING,SCRAPING,SCRAPEOK,UPDATING,TIMEDOUT,NONE,DISABLED
}
