package com.shanebeestudios.hg.metrics;

import com.shanebeestudios.hg.HungerGames;

public class MetricsHandler {

	private Metrics metrics;

	public MetricsHandler(boolean isPremium) {
		this.metrics = HungerGames.getPlugin().getMetrics();
		addSimplePie("premium", isPremium);
	}

	private void addSimplePie(String id, boolean value) {
		addSimplePie(id, Boolean.toString(value));
	}

	private void addSimplePie(String id, String value) {
		metrics.addCustomChart(new Metrics.SimplePie(id, () -> value));
	}

}
