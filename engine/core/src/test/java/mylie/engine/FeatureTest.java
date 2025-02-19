package mylie.engine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FeatureTest {

	@Feature.State(status = Feature.Status.STABLE, since = "1.0")
	static class StableFeature {
	}

	@Feature.State(status = Feature.Status.EXPERIMENTAL, since = "2.0")
	static class ExperimentalFeature {
	}

	@Feature.State(status = Feature.Status.DEPRECATED, since = "1.5")
	static class DeprecatedFeature {
	}

	@Test
	void shouldAllowStableFeatureWhenLevelIsHigherOrEqual() {
		Feature.Level level = new Feature.Level(false, Feature.Status.STABLE);
		boolean result = Feature.isAllowed(level, StableFeature.class);
		assertTrue(result);
	}

	@Test
	void shouldNotAllowStableFeatureWhenLevelIsLower() {
		Feature.Level level = new Feature.Level(false, Feature.Status.EXPERIMENTAL);
		boolean result = Feature.isAllowed(level, StableFeature.class);
		assertTrue(result);
	}

	@Test
	void shouldAllowExperimentalFeatureWhenLevelIsHigherOrEqual() {
		Feature.Level level = new Feature.Level(false, Feature.Status.RELEASE_CANDIDATE);
		boolean result = Feature.isAllowed(level, ExperimentalFeature.class);
		assertFalse(result);
	}

	@Test
	void shouldNotAllowExperimentalFeatureWhenLevelIsLower() {
		Feature.Level level = new Feature.Level(false, Feature.Status.EXPERIMENTAL);
		boolean result = Feature.isAllowed(level, ExperimentalFeature.class);
		assertTrue(result);
	}

	@Test
	void shouldAllowDeprecatedFeatureWhenDeprecatedFlagIsTrue() {
		Feature.Level level = new Feature.Level(true, Feature.Status.STABLE);
		boolean result = Feature.isAllowed(level, DeprecatedFeature.class);
		assertTrue(result);
	}

	@Test
	void shouldNotAllowDeprecatedFeatureWhenDeprecatedFlagIsFalse() {
		Feature.Level level = new Feature.Level(false, Feature.Status.STABLE);
		boolean result = Feature.isAllowed(level, DeprecatedFeature.class);
		assertFalse(result);
	}

	@Test
	void shouldAllowFeatureWithoutStateAnnotation() {
		class NoStateFeature {
		}

		Feature.Level level = new Feature.Level(false, Feature.Status.STABLE);
		boolean result = Feature.isAllowed(level, NoStateFeature.class);
		assertTrue(result);
	}

	@Test
	void shouldNotAllowDeprecatedLevelFeature() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new Feature.Level(true, Feature.Status.DEPRECATED));
		assertEquals("Deprecated cannot be used as a level", exception.getMessage());
	}
}
