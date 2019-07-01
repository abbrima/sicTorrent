public interface Announcable {
    public void announceFinished(Torrent torrent);
    public void announcePaused(Torrent torrent);
    public void announceResumed(Torrent torrent);
}
