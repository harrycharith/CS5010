package eventcalendar.view;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import eventcalendar.model.CalendarAnalyticsTest;

public class AnalyticsDashboardViewTest {
  private AnalyticsDashboardView dashboardView;
  private CalendarAnalyticsTest.MockCalendar mockCalendar;

  @BeforeEach
  void setUp() {
    mockCalendar = new CalendarAnalyticsTest.MockCalendar("Test Calendar", Collections.emptyList());
    dashboardView = new AnalyticsDashboardView(null, mockCalendar);
  }

  @Test
  void testGenerateDashboardValidInput() {
    dashboardView.startDateField.setText("2025-04-01");
    dashboardView.endDateField.setText("2025-04-02");
    dashboardView.generateDashboard();
    String dashboardText = dashboardView.dashboardArea.getText();
    assertTrue(dashboardText.contains("Analytics Dashboard for Test Calendar"));
    assertTrue(dashboardText.contains("Total number of events: 0"));
  }

  @Test
  void testInvalidDateFormat() {
    dashboardView.startDateField.setText("2025-04-01");
    dashboardView.endDateField.setText("invalid-date");
    dashboardView.generateDashboard();
    assertTrue(dashboardView.dashboardArea.getText().contains("Invalid date format"));
  }

  @Test
  void testEndDateBeforeStartDate() {
    dashboardView.startDateField.setText("2025-04-02");
    dashboardView.endDateField.setText("2025-04-01");
    dashboardView.generateDashboard();
    assertTrue(dashboardView.dashboardArea.getText().contains("End date cannot be before start date"));
  }
}