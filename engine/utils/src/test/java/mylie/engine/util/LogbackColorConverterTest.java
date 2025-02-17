package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LogbackColorConverter class.
 * <p>
 * The getForegroundColorCode method is responsible for determining the ANSI color
 * code based on the logging level of the ILoggingEvent instance provided.
 */
class LogbackColorConverterTest {

	@Test
	void testGetForegroundColorCode_DebugLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		when(mockEvent.getLevel()).thenReturn(Level.DEBUG);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.CYAN_FG, colorCode);
	}

	@Test
	void testGetForegroundColorCode_ErrorLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		when(mockEvent.getLevel()).thenReturn(Level.ERROR);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.BOLD + ANSIConstants.RED_FG, colorCode);
	}

	@Test
	void testGetForegroundColorCode_WarnLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		when(mockEvent.getLevel()).thenReturn(Level.WARN);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.RED_FG, colorCode);
	}

	@Test
	void testGetForegroundColorCode_InfoLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		when(mockEvent.getLevel()).thenReturn(Level.INFO);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.GREEN_FG, colorCode);
	}

	@Test
	void testGetForegroundColorCode_TraceLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		when(mockEvent.getLevel()).thenReturn(Level.TRACE);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.DEFAULT_FG, colorCode);
	}

	@Test
	void testGetForegroundColorCode_UnknownLevel() {
		ILoggingEvent mockEvent = mock(ILoggingEvent.class);
		Level unknownLevel = mock(Level.class);
		when(unknownLevel.toInt()).thenReturn(999); // Non-standard level
		when(mockEvent.getLevel()).thenReturn(unknownLevel);

		LogbackColorConverter converter = new LogbackColorConverter();
		String colorCode = converter.getForegroundColorCode(mockEvent);

		assertEquals(ANSIConstants.DEFAULT_FG, colorCode);
	}
}
