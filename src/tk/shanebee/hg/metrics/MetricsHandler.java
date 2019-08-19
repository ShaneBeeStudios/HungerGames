package tk.shanebee.hg.metrics;

import tk.shanebee.hg.HG;

public class MetricsHandler {

	private Metrics metrics;

	public MetricsHandler(boolean isPremium) {
		addSimplePie("premium", isPremium);
		this.metrics = HG.getPlugin().getMetrics();
	}

	private void addSimplePie(String id, boolean value) {
		addSimplePie(id, Boolean.toString(value));
	}

	private void addSimplePie(String id, String value) {
		metrics.addCustomChart(new Metrics.SimplePie(id, () -> value));
	}

}
