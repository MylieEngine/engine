package mylie.engine.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * A custom color converter for Logback that determines the text color
 * for log messages based on their logging levels. This converter is intended
 * to provide custom color schemes for various levels, improving log readability.
 */
public class LogbackColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

	/**
	 * Default constructor. This constructor is intentionally empty and
	 * does not contain any specific initialization logic.
	 */
	public LogbackColorConverter() {
		// intentional
	}

	/**
	 * Determines the ANSI foreground color code for the given logging event.
	 * The color is chosen based on the logging level of the event:
	 * - DEBUG: Cyan
	 * - ERROR: Bold Red
	 * - WARN: Red
	 * - INFO: Green
	 * - Default: Default color
	 *
	 * @param event The logging event whose color needs to be determined.
	 * @return The ANSI color code as a string.
	 */
	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		Level level = event.getLevel();
		return switch (level.toInt()) {
			case Level.DEBUG_INT -> ANSIConstants.CYAN_FG;
			case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG; // same as default color scheme
			case Level.WARN_INT -> ANSIConstants.RED_FG; // same as default color scheme
			case Level.INFO_INT -> ANSIConstants.GREEN_FG; // use CYAN instead of BLUE
			default -> ANSIConstants.DEFAULT_FG;
		};
	}
}
