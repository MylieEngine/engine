module mylie.engine.core {
	requires mylie.engine.utils;
	requires org.slf4j;
	requires static lombok;
	exports mylie.engine;
	exports mylie.engine.components;
}
