package com.challenge.got.persist.model;

import java.util.Arrays;

public enum ValueAdded {

	ONE(1), NEGATIVE_ONE(-1), ZERO(0);

	private int value;

	private ValueAdded(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static ValueAdded getEnumValue(int value) {
		return Arrays	.asList(ValueAdded.values())
						.stream()
						.filter(check -> check.value() == value)
						.findFirst()
						.orElse(null);
	}
}
