module mylie.engine.utils {
	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;
	requires org.slf4j;
	requires static lombok;
	exports mylie.engine.util;
	exports mylie.engine.util.async;
}
