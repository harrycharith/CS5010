package eventcalendar.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalendarAnalyticsTest {
  private MockCalendar mockCalendar;
  private CalendarAnalytics analytics;

  static class MockCalendar implements ICalendar {
    private final List<Event> events;
    private final String name;

    MockCalendar(String name, List<Event> events) {
      this.name = name;
      this.events = events;
    }

    @Override
    public List<Event> getEventByDate(LocalDate date) {
      return events.stream()
          .filter(e -> e.getEventStartDateTime().toLocalDate().equals(date))
          .collect(Collectors.toList());
    }

    @Override
    public String getName() { return name; }

    // Minimal implementation of other required methods
    @Override public boolean addEvent(SingleEvent.Builder eventBuilder, boolean autoDecline) { return false; }
    @Override public boolean createEvent(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                        String description, String location, boolean isPublic, boolean autoDecline) { return false; }
    @Override public boolean createAllDayEvent(String eventName, LocalDate date, LocalDate endDate,
                                              String description, String location, boolean isPublic, boolean autoDecline) { return false; }
    @Override public int createRecurringEventUntil(String eventName, LocalDateTime startDateTime,
                                                  LocalDateTime endDateTime, List<WeekDays> repeatDays, LocalDateTime repeatUntil,
                                                  String description, String location, boolean isPublic, boolean autoDecline) { return 0; }
    @Override public int createRecurringEventOccurrences(String eventName, LocalDateTime startDateTime,
                                                        LocalDateTime endDateTime, List<WeekDays> repeatDays, int occurrences,
                                                        String description, String location, boolean isPublic, boolean autoDecline) { return 0; }
    @Override public boolean editSingleEvent(String eventName, LocalDateTime startDateTime,
                                            LocalDateTime endDateTime, String property, String newValue) { return false; }
    @Override public int editEventsByNameAndStartTime(String eventName, LocalDateTime startDateTime,
                                                      String property, String newValue) { return 0; }
    @Override public int editAllEventsByName(String eventName, String property, String newValue) { return 0; }
    @Override public List<Event> getEvents() { return events; }
    @Override public List<Event> getEventsByName(String eventName) { return Collections.emptyList(); }
    @Override public boolean removeEvent(Event event) { return false; }
    @Override public void setName(String name) {}
    @Override public ZoneId getTimeZone() { return ZoneId.systemDefault(); }
    @Override public void setTimeZone(String timeZone) {}
  }

  static class MockEvent extends Event {
    MockEvent(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, String location) {
      super(UUID.randomUUID(), eventName, startDateTime, endDateTime, null, location, true);
    }
  }

  @BeforeEach
  void setUp() {
    mockCalendar = new MockCalendar("Test Calendar", Arrays.asList(
        new MockEvent("Meeting", LocalDate.of(2025, 4, 1).atTime(10, 0), 
                      LocalDate.of(2025, 4, 1).atTime(11, 0), "online"),
        new MockEvent("Meeting", LocalDate.of(2025, 4, 1).atTime(12, 0), 
                      LocalDate.of(2025, 4, 1).atTime(13, 0), "Office"),
        new MockEvent("Seminar", LocalDate.of(2025, 4, 2).atTime(9, 0), 
                      LocalDate.of(2025, 4, 2).atTime(10, 0), null)
    ));
    analytics = new CalendarAnalytics(mockCalendar);
  }

  @Test
  void testTotalEvents() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("Total number of events: 3"));
  }

  @Test
  void testEventsByWeekday() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("TUESDAY: 2")); // April 1, 2025 is a Tuesday
    assertTrue(dashboard.contains("WEDNESDAY: 1")); // April 2, 2025 is a Wednesday
  }

  @Test
  void testEventsByName() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("Meeting: 2"));
    assertTrue(dashboard.contains("Seminar: 1"));
  }

  @Test
  void testAverageEventsPerDay() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("Average events per day: 1.50")); // 3 events / 2 days
  }

  @Test
  void testBusiestAndLeastBusyDay() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("Busiest day: 2025-04-01 (2 events)"));
    assertTrue(dashboard.contains("Least busy day: 2025-04-02 (1 events)"));
  }

  @Test
  void testOnlineVsNonOnline() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
    assertTrue(dashboard.contains("Online events: 33.33%")); // 1 out of 3 events
    assertTrue(dashboard.contains("Non-online events: 66.67%")); // 2 out of 3 events
  }

  @Test
  void testEmptyRange() {
    String dashboard = analytics.generateDashboard(LocalDate.of(2025, 4, 3), LocalDate.of(2025, 4, 3));
    assertTrue(dashboard.contains("Total number of events: 0"));
    assertTrue(dashboard.contains("Average events per day: 0.00"));
    assertTrue(dashboard.contains("Online events: 0.00%"));
  }
}