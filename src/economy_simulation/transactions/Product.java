package economy_simulation.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import economy_simulation.agents.Trader;
import economy_simulation.utils.Random;

public class Product {

	//hashmap com todos que já possíram
	//trader lincado a sua posicao entre os possuidores. 0 foi o fabricante, 2 foi o segundo a comprar e por isso, o terceiro possuidor e assim por diante
	private HashMap<Trader,Integer> owners;
	private List<Trader> list;
	private TransactionType type;
	
	public Product(Trader firstOwner, TransactionType type) {
		owners = new HashMap<Trader,Integer>();
		list = new ArrayList<Trader>();
		this.type = type;
		newOwner(firstOwner);
	}

	public TransactionType getType() {
		return type;
	}

	public boolean hasAlreadyOwnedBy(Trader trader) {
		return this.inWichOrderItWasOwnedBy(trader) > 0;
	}

	public void newOwner(Trader owner) {
		Integer count = owners.get(owner);
		if(count == null) {
			owners.put(owner, list.size());
		}
		list.add(owner);
		
	}
	
	public Integer inWichOrderItWasOwnedBy(Trader trader) {
		Integer position = this.owners.get(trader);
		if(position == null) {
			return -1;
		}
		return position;
	}

	public HashMap<Trader,Integer> getOwners() {
		return owners;
	}

	public Trader takeRandomOwner() {
		int position = Random.fraction(list.size()-1);
		return list.get(position);
	}

	public boolean isThisAnOwner(Trader trader) {
		return this.owners.get(trader) != null;
	}

	@Override
	public String toString() {
		return "Product [owners=" + owners + ", list=" + list + ", type=" + type + "]";
	}
}
