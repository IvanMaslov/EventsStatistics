package statistic;

import timestamp.Clock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStatisticImpl implements EventStatistic {
    private final Clock clock;
    private final Map<String, List<Instant>> events = new HashMap<>();

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        events.putIfAbsent(name, new ArrayList<>());
        events.get(name).add(clock.now());
    }

    @Override
    public Double getEventStatisticByName(String name) {
        Instant now = clock.now();
        List<Instant> instants = events.getOrDefault(name, List.of());
        int countInLastHour = 0;
        for (int i = instants.size() - 1; i >= 0; --i) {
            if (instants.get(i).plus(1, ChronoUnit.HOURS).compareTo(now) < 0) {
                break;
            }
            countInLastHour++;
        }
        return 1.0 * countInLastHour / 60.0;
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        Map<String, Double> res = new HashMap<>();
        for (var eventInfo : events.entrySet()) {
            String name = eventInfo.getKey();
            double rpm = getEventStatisticByName(name);
            if (rpm > 0) {
                res.put(name, rpm);
            }
        }
        return res;
    }

    @Override
    public void printStatistic() {
        for (var eventInfo : events.entrySet()) {
            String name = eventInfo.getKey();
            List<Instant> instants = eventInfo.getValue();
            int allCount = instants.size();
            double timePeriod = ChronoUnit.MINUTES
                    .between(instants.get(0), instants.get(allCount - 1));
            double res;
            if (allCount == 1) {
                res = 1.0 / 60.0;
            } else {
                res = (double) allCount / timePeriod;
            }
            System.out.println(name + ": rpm = " + res);
        }
    }
}
