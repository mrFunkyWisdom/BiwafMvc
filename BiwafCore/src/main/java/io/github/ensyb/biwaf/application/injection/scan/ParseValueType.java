package io.github.ensyb.biwaf.application.injection.scan;

final class ParseValueType {

	static @FunctionalInterface interface ParseStrategy {
		public Object parse(Object type);
	}
	
	private final ParseStrategy strategy;
	private final Object firstNode;
	private final Object secondNode;

	public ParseValueType(ParseStrategy strategy, Object node, Object node2) {
		this.strategy = strategy;
		this.firstNode = node;
		this.secondNode = node2;
	}

	public Object getFirstNode() {
		return firstNode;
	}

	public Object getSecondNode() {
		return secondNode;
	}

	public ParseStrategy getStrategy() {
		return strategy;
	}
	
}
