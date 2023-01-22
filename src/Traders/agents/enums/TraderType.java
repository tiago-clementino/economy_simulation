package Traders.agents.enums;

public enum TraderType {
	ProteinProducer(1),
	FatProducer(2),
	SuggarProducer(3);

	private int productType;
	private boolean amiHonest;

	TraderType(int productType) {
		this.setProductType(productType);
	}

	public int getProductType() {
		return productType;
	}

	private void setProductType(int productType) {
		this.productType = productType;
	}

	public boolean amiHonest() {
		return amiHonest;
	}

	public void iamHonest(boolean amiHonest) {
		this.amiHonest = amiHonest;
	}
	
	
	public TraderType cloneTarderType() {
		switch (productType) {
		case 1: {
			return ProteinProducer;
		}
		case 2: {
			return FatProducer;
		}
		case 3: {
			return SuggarProducer;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + productType);
		}
	}

}
