package Traders.transactions;

import Traders.agents.Trader;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Transaction {

	/**
	 * Aggresor trader
	 */
	private Trader trader1;
	
	/**
	 * Passive trader
	 */
	private Trader trader2;

	/**
	 * Passive trader
	 */
	private Trader validator;

	private boolean trader2Paid = false;

	private boolean trader1Paid = false;

	private boolean dontKnowTheValidator;

	private Boolean hasValidator;

	public Transaction(Trader trader1, Trader trader2, Trader validator) {
		Parameters p = RunEnvironment.getInstance().getParameters();
		this.trader1 = trader1;
		this.trader2 = trader2;
		this.validator = validator;
		dontKnowTheValidator = (Boolean)p.getValue("dontKnowTheValidator");
		hasValidator = (Boolean)p.getValue("hasValidator");
	}


	public void pay(Trader payer) {
		if(payer.equals(trader1)) {
			//trader2.addProduct(payer.getType(),payer.takeBatch());
			//trader2Paid  = true;

			
			
			if(!trader2Paid && 
					(payer.getType().amiHonest() || 
							(validator != null && (!dontKnowTheValidator || validator.getType().amiHonest())) 
							)
					) {
				trader2.addProduct(payer.getType(),payer.takeBatch());
				trader2Paid = true;
				if(validator != null) {
					validator.addProduct(trader2.getType(),trader2.takeUnit());
					if(dontKnowTheValidator && validator.getType().amiHonest())
						trader2.setHonest(validator);
					if (payer.getType().amiHonest()) {
						trader2.setHonest(payer);
					}
				}else {
					trader2.setHonest(payer);
				}
			}else if(!payer.getType().amiHonest()){

				trader2.setNonHonest(payer);
			}
			
			
			
			
			if(!trader1Paid && 
					(trader2.getType().amiHonest() || 
							(validator != null && (!dontKnowTheValidator || validator.getType().amiHonest())) 
							)
					) {
				payer.addProduct(trader2.getType(),trader2.takeBatch());
				trader1Paid = true;
				if(validator != null) {
					validator.addProduct(payer.getType(),payer.takeUnit());
					if(dontKnowTheValidator && validator.getType().amiHonest())
						payer.setHonest(validator);
					if (trader2.getType().amiHonest()) {
						payer.setHonest(trader2);
					}
				}else {
					payer.setHonest(trader2);
				}
			}else if(!trader2.getType().amiHonest()){

				payer.setNonHonest(trader2);
			}
		} /*
			 * else if(payer.equals(trader2)) {
			 * trader1.addProduct(payer.getType(),payer.takeBatch()); trader1Paid = true;
			 * if(!trader2Paid && (trader1.getType().amiHonest() || (validator != null)// &&
			 * (!dontKnowTheValidator || validator.getType().amiHonest())) ) ) {
			 * payer.addProduct(trader1.getType(),trader1.takeBatch()); trader2Paid = true;
			 * if(validator != null) {
			 * validator.addProduct(payer.getType(),payer.takeUnit());
			 * if(dontKnowTheValidator && !trader1.getType().amiHonest())
			 * payer.setHonest(validator); }else { payer.setHonest(trader1); } } }else
			 * if(!trader1.getType().amiHonest()){
			 * 
			 * payer.setNonHonest(trader1); }
			 */
	}


}
