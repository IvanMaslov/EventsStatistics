package timestamp;

import java.time.Instant;

public class ChangeableClock implements Clock {
    private Instant timestamp;

    public ChangeableClock(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Instant now() {
        return timestamp;
    }
}
