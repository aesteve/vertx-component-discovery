package io.vertx.componentdiscovery.utils;

public enum EbAddresses {
	PAGE_GENERATOR("page-generator"),
	DISCOVERY_SERVICE("discovery-service"),
	SOCKET_NOTIFIER("socket-notifier");

	private String address;

	private EbAddresses(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return address;
	}
}
