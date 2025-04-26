package backend.utils;

public enum PurchaseType {
	
	BUY("buy"),
	RENT("rent");

	private String type;

	PurchaseType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static PurchaseType fromString(String type) {
		for (PurchaseType purchaseType : PurchaseType.values()) {
			if (purchaseType.type.equalsIgnoreCase(type)) {
				return purchaseType;
			}
		}
		return null;
	}
}
