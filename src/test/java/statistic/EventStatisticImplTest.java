package statistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import timestamp.ChangeableClock;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStatisticImplTest {
    private Instant startTime;
    private ChangeableClock clock;
    private EventStatistic eventStatistic;

    @BeforeEach
    public void beforeEach() {
        startTime = Instant.now();
        clock = new ChangeableClock(startTime);
        eventStatistic = new EventStatisticImpl(clock);
    }

    @Test
    public void simpleTest() {
        Map<String, Double> res =
                Map.of("event1", 20.0 / 60.0,
                        "event2", 10.0 / 60.0,
                        "event3", 1.0 / 60.0);
        for (int i = 0; i < 20; ++i) {
            clock.setTimestamp(startTime.plusSeconds(i));
            eventStatistic.incEvent("event1");
            if (i < 10) eventStatistic.incEvent("event2");
        }
        eventStatistic.incEvent("event3");

        var fullInfo = eventStatistic.getAllEventStatistic();
        assertEquals(3, fullInfo.size());
        assertEquals(res.get("event1"), eventStatistic.getEventStatisticByName("event1"));
        assertEquals(res.get("event2"), eventStatistic.getEventStatisticByName("event2"));
        assertEquals(res.get("event3"), eventStatistic.getEventStatisticByName("event3"));
        for (var eventInfo : fullInfo.entrySet()) {
            assertTrue(res.containsKey(eventInfo.getKey()));
            assertEquals(res.get(eventInfo.getKey()), eventInfo.getValue());
        }
    }

    @Test
    public void emptyEventTest() {
        assertEquals(0, eventStatistic.getEventStatisticByName("emptyEvent"));
    }

    @Test
    public void oneEventTest() {
        eventStatistic.incEvent("event");
        var fullInfo = eventStatistic.getAllEventStatistic();
        assertEquals(1, fullInfo.size());
        var eventInfo = fullInfo.entrySet().stream().findAny().get();
        assertEquals("event", eventInfo.getKey());
        assertEquals(1.0 / 60.0, eventInfo.getValue());
    }

    @Test
    public void moreOneHourTest() {
        eventStatistic.incEvent("event");
        clock.setTimestamp(startTime.plusSeconds(4000));
        var fullInfo = eventStatistic.getAllEventStatistic();
        assertEquals(0, eventStatistic.getEventStatisticByName("event"));
        assertTrue(fullInfo.isEmpty());
    }

    @Test
    public void printStatisticExample() {
        eventStatistic.incEvent("event1");
        clock.setTimestamp(startTime.plusSeconds(4000));
        eventStatistic.incEvent("event1");
        eventStatistic.incEvent("event2");
        eventStatistic.printStatistic();
    }
}
