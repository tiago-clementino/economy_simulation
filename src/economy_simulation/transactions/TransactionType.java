package economy_simulation.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TransactionType {
	
	private Integer hash;
	private static Integer nextHash = 0;
	
	private static HashMap<TransactionType,Integer> allTypes;
	private static List<TransactionType> listOfTypes;

	public TransactionType() {
		//this.setHash(UUID.randomUUID().toString());
		this.setHash();
		setType(this);
	}

	public static void reset() {
		nextHash = 0;
		allTypes = new HashMap<TransactionType,Integer>();
		listOfTypes = new ArrayList<TransactionType>();
	}

	public Integer getHash() {
		return hash;
	}

	private void setHash() {
		this.hash = nextHash;
		nextHash++;
	}

	public static void setType(TransactionType type) {
		getMapOfTypes().put(type, getTypes().size());
		getTypes().add(type);
	}

	public static TransactionType getType(Integer i) {
		return getTypes().get(i);
	}

	public static List<TransactionType> getTypes() {
		if(listOfTypes == null) {
			listOfTypes = new ArrayList<TransactionType>();
		}
		return listOfTypes;
	}

	public static HashMap<TransactionType,Integer> getMapOfTypes() {
		if(allTypes == null) {
			allTypes = new HashMap<TransactionType,Integer>();
		}
		return allTypes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hash);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionType other = (TransactionType) obj;
		return Objects.equals(hash, other.hash);
	}

	public static Integer getTypeNumberOf(TransactionType type) {
		return getMapOfTypes().get(type);
	}

	@Override
	public String toString() {
		return "TransactionType [hash=" + hash + "]";
	}
}
