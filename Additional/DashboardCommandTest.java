package eventcalendar.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import eventcalendar.model.CalendarAnalytics;
import eventcalendar.model.Event;
import eventcalendar.model.ICalendar;
import eventcalendar.view.View;

class DashboardCommandTest {
  private ICalendar mockCalendar;
  private View mockView;
  private CommandProcessor processor;

  static class MockView extends View {
    private String lastMessage;

    @Override
    public void displayMessage(String message) {
      this.lastMessage = message;
    }

    public String getLastMessage() {
      return lastMessage;
    }
  }

  @BeforeEach
  void setUp() {
    mockView = new MockView();
    mockCalendar = new CalendarAnalyticsTest.MockCalendar("Test Calendar", Collections.emptyList());
    processor = new CommandProcessor(null, null, null, mockCalendar, mockView, "");
  }

  @Test
  void testValidDashboardCommand() {
    String command = "show calendar dashboard from 2025-04-01 to 2025-04-02";
    String result = processor.processCommand(command);
    assertTrue(result.contains("Analytics Dashboard for Test Calendar"));
    assertTrue(result.contains("Total number of events: 0"));
    assertTrue(mockView.getLastMessage().contains("Analytics Dashboard"));
  }

  @Test
  void testInvalidDateFormat() {
    String command = "show calendar dashboard from 2025-04-01 to invalid-date";
    String result = processor.processCommand(command);
    assertTrue(result.contains("Invalid date format"));
  }

  @Test
  void testEndDateBeforeStartDate() {
    String command = "show calendar dashboard from 2025-04-02 to 2025-04-01";
    String result = processor.processCommand(command);
    assertTrue(result.contains("End date cannot be before start date"));
  }

  @Test
  void testInvalidCommandFormat() {
    String command = "show calendar dashboard from 2025-04-01";
    String result = processor.processCommand(command);
    assertTrue(result.contains("Invalid dashboard command format"));
  }
}