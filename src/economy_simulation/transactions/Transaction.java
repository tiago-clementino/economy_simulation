package economy_simulation.transactions;

import economy_simulation.agents.Trader;
import economy_simulation.utils.ValidatorException;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Transaction {

	/**
	 * Aggresor trader
	 */
	private Trader active;
	private TransactionType activeTransactionType;
	
	/**
	 * Passive trader
	 */
	private Trader passive;
	private TransactionType passiveTransactionType;

	/**
	 * Passive trader
	 */
	private Trader validator = null;

	private boolean paid = false;

	private Boolean hasValidator;

	private Boolean securityDeposit = false;

	private Float securityDepositSucess = 0.0F;

	
	public Transaction(Trader active, Trader passive, TransactionType activeTransactionType, TransactionType passiveTransactionType) {//tanto o ativo quanto o passivo pagam com o que eles tem em maior abundância
		Parameters p = RunEnvironment.getInstance().getParameters();
		this.active = active;
		this.activeTransactionType = activeTransactionType;
		this.passive = passive;
		this.passiveTransactionType = passiveTransactionType;
		hasValidator = (Boolean)p.getValue("hasValidator");
		securityDeposit = (Boolean)p.getValue("securityDeposit");
		securityDepositSucess = (Float)p.getValue("securityDepositSucess");
	}

	
	public Transaction(Trader active, Trader passive, TransactionType activeTransactionType, TransactionType passiveTransactionType, Trader validator) {//tanto o ativo quanto o passivo pagam com o que eles tem em maior abundância
		this(active, passive, activeTransactionType, passiveTransactionType);
		this.validator = validator;
	}


	public void transfer(Trader payer,Trader receiver,TransactionType type, boolean batch) {
		if(batch) {
			receiver.addProduct(type,payer.takeBatch());
		}else {
			receiver.addProduct(type,payer.takeUnit());
		}
	}

	public boolean pay(Trader payer) throws ValidatorException {
		if(payer.equals(this.active)) {//this.active estava trader1 antes
			if(!paid) {
				if(this.active.amiHonest(this.activeTransactionType) && this.passive.amiHonest(this.passiveTransactionType)) {
					
					transfer(this.active,this.passive,this.activeTransactionType,true);
					transfer(this.passive,this.active,this.passiveTransactionType,true);
					
					return true;
//					if(hasFeedback) {
//						if(Math.random() <= feedbackPercent) {
//							this.active.setFeedback(this.passive, true);
//						}
//						if(Math.random() <= feedbackPercent) {
//							this.passive.setFeedback(this.active, true);
//						}
//					}
				}else if(this.active.amiHonest(this.activeTransactionType) && !this.passive.amiHonest(this.passiveTransactionType)) {
				
					if(hasValidator && validator != null) {
						transfer(this.active,this.validator,this.activeTransactionType,false);
						throw new ValidatorException();
					}else if(!securityDeposit){
						transfer(this.active,this.passive,this.activeTransactionType,true);
					}else if(securityDeposit && Math.random() > securityDepositSucess) {//o deposito caucao nao garante a transacao de forma perfeita, por uma das operacoes eh n validavel
						//transfer(this.passive,this.active,this.passiveTransactionType,true);//achei sem sentido
						transfer(this.active,this.passive,this.activeTransactionType,true);
					}else {
						throw new ValidatorException();
					}
					
//					if(hasFeedback && Math.random() <= feedbackPercent) {
//						this.passive.setFeedback(this.active, false);
//					}
				}else if(!this.active.amiHonest(this.activeTransactionType) && this.passive.amiHonest(this.passiveTransactionType)) {
					
					if(hasValidator && validator != null) {
						transfer(this.passive,this.validator,this.passiveTransactionType,false);
						throw new ValidatorException();
					}else if(!securityDeposit){
						transfer(this.passive,this.active,this.passiveTransactionType,true);
					}else if(securityDeposit && Math.random() > securityDepositSucess) {//o deposito caucao nao garante a transacao de forma perfeita, por uma das operacoes eh n validavel
						//transfer(this.active,this.passive,this.activeTransactionType,true);//achei sem sentido
						transfer(this.passive,this.active,this.passiveTransactionType,true);
					}else {
						throw new ValidatorException();
					}
//					if(hasFeedback && Math.random() <= feedbackPercent) {
//						this.active.setFeedback(this.passive, false);
//					}
				}
				paid = true;
			}
		}
		return false;
	}


}
