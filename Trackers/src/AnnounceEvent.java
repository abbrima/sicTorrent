public enum AnnounceEvent {
    STARTED,COMPLETED,STOPPED,NONE

    //started: The first request to the tracker must include the event key with this value.

    //stopped: Must be sent to the tracker if the client is shutting down gracefully.

    //completed: Must be sent to the tracker when the download completes. However,
    // must not be sent if the download was already 100% complete when the client
    // started. Presumably, this is to allow the tracker to increment the "completed downloads"
    // metric based solely on this event.
}
