package economy_simulation.agents;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;

import economy_simulation.transactions.Product;
import economy_simulation.transactions.Transaction;
import economy_simulation.transactions.TransactionType;
import economy_simulation.utils.Random;
import economy_simulation.utils.ValidatorException;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/**
 * Implementation of a bovine pulmonary artery endothelial (BPAE) Trader with 
 * contact-inhibited migration as described in
 * 
 *     Lee, Y., S. Kouvroukoglou, L. McIntire, and K. Zygourakis, "A Traderular
 *         Automata Model for the Proliferation of Migrating Contact-Inhibited 
 *         Traders," Biophysics Journal, 69, 1284-1298, 1995.
 * 
 * The Traders have a random walk across a 2D Grid and divide according to walk
 * and Trader phase parameters. 
 * 
 * @author Eric Tatara
 *
 */
public class Trader {

	/**
	 * The Trader movement counter.  When this counter reaches 0, the Trader may move
	 *  to an adjacent site.
	 */
	private int moveCounter;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	//private int proteinCounter = 0;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	//private int fatCount = 0;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	//private int suggarCount = 0;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	private int reproduceCounter;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	private int produceCounter;
	
	/**
	 * The time to move, set initially.
	 */
	private int divideTime;
	
	/**
	 * The time to divide, set initially.
	 */
	private int moveTime;
	
	/**
	 * The time of deposity latency.
	 */
	private int depositylatency;
	
	/**
	 * 
	 */
	private int maxFood;
	
	/**
	 * 
	 */
	private TransactionType type;

	/**
	 * 
	 */
	private int batch;

	private int rounds;
	
	private int totalTypes;
	
	private int initialTraders;

	private boolean memory;

	//private HashMap<Integer,Trader> nonHonests;

	//private HashMap<Integer, Trader> honests;

	//private boolean dontKnowTheValidator;

	private boolean typeAgnostic;

	private Boolean hasValidator;

	private Boolean securityDeposit;

	private Boolean hasFeedback;

	private int stepCount = 0;
	
	private int totalTransactions = 0;
	
	private int transactionFail = 0;
	
	private int notMatch = 0;

	private Float honestPercent = 0.0F;

	private Float feedbackPercent = 0.0F;

	private HashMap<TransactionType,Boolean> myHonest;

	private HashMap<TransactionType,List<Product>> balance;

	private HashMap<TransactionType,List<Product>> backup;

	//contem produtos que você já repassou
	private List<Product> allProducts;
	
	final static Logger logger = Logger.getLogger(Trader.class);

	public static final String DATA_OUTPUT = "./data.csv";

	public static final String TIMELINE_OUTPUT = "./timeline.csv";

	private Integer hash;

	private int AvoidedFailTransactions;
	private static Integer nextHash = 0;
	private static String run = null;
	
	public Trader(TransactionType type){
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int divideTimeMin = (Integer)p.getValue("divideTimeMin");
		int divideTimeMax = (Integer)p.getValue("divideTimeMax");
		int moveTimeMin = (Integer)p.getValue("moveTimeMin");
		int moveTimeMax = (Integer)p.getValue("moveTimeMax");
		maxFood = (Integer)p.getValue("foodEnouth");
		batch = (Integer)p.getValue("batch");
		rounds = (Integer)p.getValue("rounds");
		memory = (Boolean)p.getValue("memory");
		//dontKnowTheValidator = (Boolean)p.getValue("dontKnowTheValidator");
		typeAgnostic = (Boolean)p.getValue("typeAgnostic");
		hasValidator = (Boolean)p.getValue("hasValidator");
		honestPercent  = (Float)p.getValue("honestPercent");
		//myHonest = Math.random() <= honestPercent
		securityDeposit = (Boolean)p.getValue("securityDeposit");
		hasFeedback = (Boolean)p.getValue("hasFeedback");
		feedbackPercent  = (Float)p.getValue("feedbackPercent");
		totalTypes = (Integer)p.getValue("totalTypes");

		initialTraders = (Integer)p.getValue("initialTraders");
		
		//nonHonests = new LinkedHashMap<Integer,Trader>();
		//honests = new LinkedHashMap<Integer,Trader>();
		myHonest = new HashMap<TransactionType,Boolean>();
		for (Iterator<TransactionType> iterator = TransactionType.getTypes().iterator(); iterator.hasNext();) {
			myHonest.put(iterator.next(), Math.random() <= honestPercent);
		}
		balance = new HashMap<TransactionType,List<Product>>();
		backup = new HashMap<TransactionType,List<Product>>();
		allProducts = new ArrayList<Product>();
		// Set the initial move and divide times according to the specified parameters
		// and sampled from a Uniform distribution.
		divideTime = RandomHelper.nextIntFromTo(divideTimeMin, divideTimeMax);
		moveTime = RandomHelper.nextIntFromTo(moveTimeMin, moveTimeMax);
		//depositylatency serve para simular o tempo em que o dinheiro fica preso ao usar deposito caucao
		if(securityDeposit) {
			depositylatency = moveTime*3;
		}else {
			depositylatency = moveTime;
		}
		reproduceCounter = divideTime;
		moveCounter = moveTime;
		//nao ha pq ficar ocioso a nao ser que esteja em espera
		produceCounter = depositylatency;
		
		this.type = type;
		this.stepCount  = 0;
		this.totalTransactions = 0;
		this.transactionFail = 0;
		this.AvoidedFailTransactions = 0;
		this.notMatch = 0;
		this.setHash();
	}

	public Integer getHash() {
		return hash;
	}

	public String getRun() {
		return run;
	}

	private void resetHash() {
		nextHash = 0;
		run = null;
	}

	private void setHash() {
		if(nextHash == 0) {
			if(run == null) {
				run = UUID.randomUUID().toString();
			}
		}
		this.hash = nextHash;
		nextHash++;
	}
	
	@ScheduledMethod(start=1, interval=1)
	public void step(){
	  // If the the move counter reaches 0
		if (moveCounter == 0){
			// and if there is an empty adjacent site
			if (!findEmptySites().isEmpty())
				// move to a random empty adjacent site
				move();

			 
			// reset the move counter
			moveCounter = moveTime;
		}
		else {
			// continue waiting to move
			moveCounter--;
		}
		
		// if the reproduce counter reaches 0
		if (reproduceCounter == 0){
			if(fueled()) {//may not be in the clause above because it decrement the reproduceCounter less than zero
				// and if there is an empty adjacent site
				if (!findEmptySites().isEmpty()) {
					reproduce();
					this.balance = new HashMap<TransactionType,List<Product>>();
					this.allProducts = new ArrayList<Product>();
					
					//this.stepCount = 0;
					
					this.resetStep();
					this.totalTransactions = 0;
					this.transactionFail = 0;
					this.AvoidedFailTransactions = 0;
					this.notMatch = 0;
					
					// reset the reproduce counter
					reproduceCounter = divideTime;
				}
				 
				
			}
		}
		else {
			// continue waiting to reproduce

			reproduceCounter--;
		}
		
		// if the reproduce counter reaches 0
		if (produceCounter == 0){

			//this.stepCount++;
			this.stepAdd();
			workout(type);
			
			
			if(this.stepCount > maxLife()) {
				//System.out.println("death: " + this.stepCount + ", " + maxLife());
				killMe();
			}
			
			// reset the reproduce counter
			//nao ha pq ficar ocioso a nao ser que esteja em espera
			produceCounter = depositylatency;
		}
		else {
			// continue waiting to reproduce
			if(securityDeposit) {
				this.stepAdd();
			}

			produceCounter--;
		}
			
	}
	
	private void resetStep() {
		//this.stepCount = 0;
		this.setStep(0);
	}

	private void stepAdd() {
		this.setStep(this.stepCount+1);
		//this.stepCount++;
	}

	private void setStep(Integer step) {
		this.stepCount = step;
		//salvar linha em log
		

	}

	private Long maxLife() {
		return Math.round(maxFood * Math.pow(totalTypes, 4));
	}

	private boolean fueled() {
		List<TransactionType> types = TransactionType.getTypes();
		List<Product> count = null;
		for (Iterator<TransactionType> iterator = types.iterator(); iterator.hasNext();) {
			count = this.balance.get(iterator.next());
			if(count==null||count.size()<maxFood) {
				return false;
			}
		}
		//return proteinCounter > maxFood && fatCount > maxFood && suggarCount > maxFood;
		return true;
	}

	private void workout(TransactionType type) {
		// if the reproduce counter reaches 0
		if (!isBalanced(type)){
			produce();
		}
		else {
			Transaction transaction = dealBestTransaction();
			if(transaction != null) {
				transfer(transaction);
			}else {
				//System.out.println("vai");
				transactionFail++;
				notMatch++;
			}
		}
		
		if(Math.random() * initialTraders <= 0.0001) {
			final Path path = Paths.get(TIMELINE_OUTPUT);
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	sb.append(run + ",");
		    	sb.append(hash + ",");
		    	sb.append(type.getHash() + ",");
		    	sb.append(notMatch + ",");
		    	
		    	
		    	
		    	sb.append((memory?1:0) + ",");
		    	sb.append((hasValidator?1:0) + ",");
		    	sb.append((typeAgnostic?1:0) + ",");
		    	sb.append((securityDeposit?1:0) + ",");
		    	sb.append((hasFeedback?1:0) + ",");
		    	sb.append(feedbackPercent + ",");
		    	sb.append(honestPercent + ",");
		    	sb.append(stepCount + ",");
		    	sb.append(totalTransactions + ",");
		    	sb.append(transactionFail + ",");
		    	sb.append(AvoidedFailTransactions);
		    	
				Files.write(path, Arrays.asList(sb), StandardCharsets.UTF_8,
				    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
			} catch (IOException e) {}
		}

		
	}

	private void produce() {
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < this.batch; i++) {
			products.add(new Product(this,type));
			
			//this.getBalanceByType(type).add(product);
			//this.getBackupByType(type).add(product);
			//this.allProducts.add(product);
		}
		this.addProduct(type, products);
		
//		switch (type) {
//		case ProteinProducer:
//			proteinCounter += 3;
//			return;
//		case FatProducer:
//			fatCount += 3;
//			return;
//		case SuggarProducer:
//			suggarCount += 3;
//			return;
//		}
	}

	private List<Product> getBackupByType(TransactionType type2) {
		List<Product> products = this.backup.get(type2);
		if(products == null) {
			products = new ArrayList<Product>();
			this.backup.put(type2, products);
		}
		return products;
	}

	private List<Product> getBalanceByType(TransactionType type2) {
		List<Product> products = this.balance.get(type2);
		if(products == null) {
			products = new ArrayList<Product>();
			this.balance.put(type2, products);
		}
		return products;
	}

	private void transfer(Transaction transaction) {
		try {
			if(!transaction.pay(this)) {
				transactionFail++;
			}else {
				totalTransactions++;
			}
		} catch (ValidatorException e) {
			AvoidedFailTransactions++;
		}
	}

	/**
	 * Soh retorn que está balanciado se esse type for o mais ou um dos mais abundantes
	 * @param type
	 * @return
	 */
	private boolean isBalanced(TransactionType type) {
		
		List<Product> products = this.balance.get(type);
		Integer size = 0;
		if(products != null) {
			size = products.size();
		}else {
			return false;
		}
//		System.out.println("isBalanced: " + size);
//		System.out.print("isBalanced: ");
//		for (Iterator<List<Product>> iterator = this.balance.values().iterator(); iterator.hasNext();) {
//			System.out.print(((List<Product>) iterator.next()).size() + ", ");
//			
//		}
//		System.out.println();
		for (Iterator<TransactionType> iterator = this.balance.keySet().iterator(); iterator.hasNext();) {
			TransactionType tt = iterator.next();
			if(tt != type && size <= this.balance.get(tt).size() + this.batch) {
				return false;
			}
			
		}
		return true;
		
//		switch (type) {
//		case ProteinProducer:
//			return !(proteinCounter < fatCount || proteinCounter < suggarCount);
//		case FatProducer:
//			return !(fatCount < proteinCounter || fatCount < suggarCount);
//		case SuggarProducer:
//			return !(suggarCount < proteinCounter || suggarCount < fatCount);
//		}
//		return true;
	}

	/**
	 * Find a trader and a validator if fitted
	 * @return
	 */
	private Transaction dealBestTransaction() {
		
		//acho que isso é desnecessário
		TransactionType biggerStock = biggerStock();
		if(biggerStock != null) {

			Context context = ContextUtils.getContext(this);
			Grid grid = (Grid)context.getProjection("Grid");
			GridPoint pt = grid.getLocation(this);
			
			Transaction transaction = null;
			try {
				for (int i = 0; i < rounds; i++) {
		
					//checar se é null, se for, tentar o próximo (não retornar direto
					//checar se isso aqui funciona colocando logs em cada if
					//talvez procurar em todos fazendo hasnext e verificando se é null seria melhor
					if (grid.getObjectsAt(pt.getX()-1,pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()+1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX(),pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX(),pt.getY()+1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()+1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()-1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX(),pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX(),pt.getY()-1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX()-1,pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()-1).iterator().next(),biggerStock);
					if (grid.getObjectsAt(pt.getX()-1,pt.getY()).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()).iterator().next(),biggerStock);

				}
				
			} catch (NoSuchElementException e) {}
		}
		return null;
		
	}

	private Transaction dealTransaction(Object trader, TransactionType biggerStock) {
		if(trader instanceof Trader && biggerStock != null) {// && !trader.equals(this)) {
			//if(((Trader) trader).getType().equals(lowerStock()) && ((Trader) trader).hasStock()) {
			TransactionType biggerPassiveStock = ((Trader) trader).biggerStock();//biggerStock);
			//if(((Trader) trader).hasStock() && !((Trader) trader).getType().equals(type)) {
			if(biggerPassiveStock != null && biggerStock != biggerPassiveStock) {
				if(!memory || isThisHonest(((Trader) trader),biggerPassiveStock)){
					//System.out.println("Sucesso");
					 //cria com esse
					return new Transaction(this,((Trader) trader),biggerStock,biggerPassiveStock);
				}else if(hasValidator){
					
					Trader validator = takeBestValidator(((Trader) trader),biggerPassiveStock);//take the best or someone random
					if(validator != null) {
						//System.out.println("quase");
						return new Transaction(this,((Trader) trader),biggerStock,biggerPassiveStock,validator);
					}else {
						//System.out.println("quase quase");
					}
				} if (securityDeposit) {
					return new Transaction(this,((Trader) trader),biggerStock,biggerPassiveStock);
				}
			}else {

				//System.out.println("fake passive 0: " + ((Trader) trader).balance.keySet().size() + " " + biggerPassiveStock + "     " + (biggerStock != biggerPassiveStock) + "     " + this.balance.keySet().size() + " " + biggerStock);
			}
		}
		return null;
	}

	/**
	 * Verifica se algum dos produtos em estoque já foi possuído por trader. Se sim, isso significa que trader honrra seus contratos, se não, significa que não posso afirmar nada sobre trader
	 * @param trader
	 * @param hash
	 * @return true se trader é honesto, false caso sua honestidade seja desconhecida
	 */
	public boolean isThisHonest(Trader trader, TransactionType hash) {
		List<Product> products = null;
		if(this.typeAgnostic) {
			products = this.allProducts;
		}else {
			products = this.backup.get(hash);
		}
		if(products != null && !products.isEmpty()) {
			Product product = null;
			for (Iterator<Product> iterator = products.iterator(); iterator.hasNext();) {
				product = iterator.next();
				if(product.inWichOrderItWasOwnedBy(trader) > 0) {
					//se tem feedback tem uma chance de nao ter tido acesso a esse depoimento a respeito da honestidade em questao
					if(hasFeedback) {
						if(Math.random() <= feedbackPercent) {
							return true;
						}else {
							return false;
						}
					}else {
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	public boolean amiHonest(TransactionType hash) {
		List<Product> products = this.balance.get(hash);
		Boolean honest = this.getMyHonest(hash);
		if(honest) {
			//quanto mais tempo de vida, maior a chance de agir honestamente
			//quanto mais do produto negociado, maior a chance de agir honestamente
			return  ((this.stepCount/maxLife())+1-(products.size()/maxFood))/2 <= Math.random();
		}
		return false;
	}

	private Boolean getMyHonest(TransactionType hash) {
		Boolean myH = this.myHonest.get(hash);
		if(myH == null) {
			myH = Math.random() <= honestPercent;
			this.myHonest.put(hash, myH);
		}
		return myH;
	}

//	public boolean isSheHonestForMe(Trader trader, TransactionType hash) {
//		
//		//agora deve percorrer todos os caminhos
//		List<Product> products = this.backup.get(hash);
//		Product product = null;
//		for (Iterator<Product> iterator = products.iterator(); iterator.hasNext();) {
//			product = iterator.next();
//			if(product.inWichOrderItWasOwnedBy(trader) > 1) {
//				return true;
//			}
//		}
//		return false;
//		
//		// verifica em uma lista
//		//return !nonHonests.containsKey(Integer.valueOf(trader.hashCode()));
//	}

	//tem um custo e pode ser desonesto tb
	//procure um que seja honesto ao validar aquela transação (caso tenha um) ou null, caso contrário
	private Trader takeBestValidator(Trader trader, TransactionType hash) {
		if(!hasValidator) {
			return null;
		}
		
		return trader.takeRandomHonest(hash);
		/*
		 * Context context = ContextUtils.getContext(this); Grid grid =
		 * (Grid)context.getProjection("Grid");
		 * 
		 * for (int i = 0; i < 20; i++) { //faltou o hasnext Object trader =
		 * grid.getObjectsAt( Random.fraction(grid.getDimensions().getWidth()),
		 * Random.fraction(grid.getDimensions().getHeight())).iterator().next();
		 * 
		 * if(trader instanceof Trader && !trader.equals(this)) {
		 * if(!dontKnowTheValidator || isThisHonest((Trader) trader)) { return (Trader)
		 * trader; } } } return null;
		 */
	}

	private Trader takeRandomHonest(TransactionType type) {
		if(!typeAgnostic) {
			List<Product> products = this.balance.get(type);
			if(products != null && !products.isEmpty()) {
				float size = hasFeedback?(products.size()-1)*feedbackPercent:(products.size()-1);
				int position = Random.fraction(size);
				return products.get(position).takeRandomOwner();
			}
		}else {
			if(this != null && !this.allProducts.isEmpty()) {
				float size = hasFeedback?(this.allProducts.size()-1)*feedbackPercent:(this.allProducts.size()-1);
				int position = Random.fraction(size);
				return this.allProducts.get(position).takeRandomOwner();
			}
		}
		
		return null;
		
		
//		int end = Random.fraction(honests.size());
//		int count = 0;
//		for (Iterator<Integer> iterator = honests.keySet().iterator(); iterator.hasNext();) {
//			Integer i = iterator.next();
//			count++;
//			if(count > end) {
//				return honests.get(i);
//			}
//		}		
//		return null;
	}

//	public void setNonHonest(Trader validator) {
//		honests.remove(Integer.valueOf(validator.hashCode()));
//		nonHonests.put(Integer.valueOf(validator.hashCode()), validator);
//	}
//
//	public void setHonest(Trader validator) {
//		nonHonests.remove(Integer.valueOf(validator.hashCode()));
//		honests.put(Integer.valueOf(validator.hashCode()), validator);
//	}

//	private TraderType lowerStock() {
//		switch (type) {
//		case ProteinProducer:
//			if(fatCount < suggarCount)
//				return TraderType.FatProducer;
//			else
//				return TraderType.SuggarProducer;
//		case FatProducer:
//			if(proteinCounter < suggarCount)
//				return TraderType.ProteinProducer;
//			else
//				return TraderType.SuggarProducer;
//		case SuggarProducer:
//			if(proteinCounter < fatCount)
//				return TraderType.ProteinProducer;
//			else
//				return TraderType.FatProducer;
//		}
//		return type;
//	}

	private TransactionType biggerStock() {		
		return biggerStock(null);
	}

	private TransactionType biggerStock(TransactionType except) {		
		Integer size = 0;
		TransactionType result = null;
		for (Iterator<TransactionType> iterator = this.balance.keySet().iterator(); iterator.hasNext();) {
			TransactionType tt = iterator.next();
			Integer newSize = this.balance.get(tt).size();
			if(size < newSize && (except == null || except.getHash() != tt.getHash())) {
				size = newSize;
				result = tt;
			}
			else if(size == newSize && Math.random() <= 0.5) { //não faz muito sentido, mas essa cláusula funciona melhor que a anterior pars evitar matches iguais
				size = newSize; 
				result = tt;
			}
			
		}
		return result;
	}

	private boolean hasStock() {		
		return isBalanced(type);
	}

	/**
	 * Move the Trader to a random empty adjacent site.
	 */
	private void move(){
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid)context.getProjection("Grid");
	
		List<GridPoint> emptySites = findEmptySites();
		
		// TODO add Grid.moveTo(Object o, GridPoint pt) to Repast API
		if (emptySites.size() > 0) grid.moveTo(this, emptySites.get(0).getX(), emptySites.get(0).getY());
	}

	private void killMe() {
		Context context = ContextUtils.getContext(this);
		context.remove(this);
		
		if(context.size() <= 0) {


			System.out.println("falha ");
			System.out.println("Lembrar dos traders desonestos: " + memory);
			System.out.println("Tem validador: " + hasValidator);
			System.out.println("Tem categorias: " + !typeAgnostic);
			System.out.println("Tem depósito caução: " + securityDeposit);
			System.out.println("Tem feedback: " + hasFeedback);
			if(hasFeedback) {
				System.out.println("Taxa de feedback: " + (feedbackPercent*100) + "%");
			}
			System.out.println("Percentual de honestidade: " + (honestPercent*100) + "%");
			System.out.println("Passos (amostra): " + this.stepCount);
			System.out.println("total de transacoes (amostra): " + this.totalTransactions);
			System.out.println("total de transacoes mal sucedidas (amostra): " + this.transactionFail);
			System.out.println("total de transacoes evitadas (amostra): " + this.AvoidedFailTransactions);
			System.out.println("nao deu match (amostra): " + this.notMatch);
			
			double terminate = (System.currentTimeMillis() - TraderContextBuilder.timeIn) / 1000;
			System.out.println("Tempo total: " + terminate);
			System.out.println();
			System.out.println();
			System.out.println();
			
			final Path path = Paths.get(DATA_OUTPUT);
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	sb.append("0,");
		    	sb.append((memory?1:0) + ",");
		    	sb.append((hasValidator?1:0) + ",");
		    	sb.append((typeAgnostic?1:0) + ",");
		    	sb.append((securityDeposit?1:0) + ",");
		    	sb.append((hasFeedback?1:0) + ",");
		    	sb.append(feedbackPercent + ",");
		    	sb.append(honestPercent + ",");
		    	sb.append(stepCount + ",");
		    	sb.append(totalTransactions + ",");
		    	sb.append(transactionFail + ",");
		    	sb.append(AvoidedFailTransactions + ",");
		    	sb.append(terminate );
				Files.write(path, Arrays.asList(sb), StandardCharsets.UTF_8,
				    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
				RunEnvironment.getInstance().endRun();
				//RunEnvironment.getInstance().resumeRun();
			    //TraderContextBuilder.rebuild(context);
				
			} catch (IOException e) {}
		    

		}
	}
	
	/**
	 * Produce a daughter Trader and move it to an empty adjacent site.
	 */
	private void reproduce(){		
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid)context.getProjection("Grid");
		
		TransactionType type = this.type;
		//type.iamHonest(Math.random() <= honestPercent);
		Trader trader = new Trader(this.type);	
		context.add(trader);
		
		if(context.size() >= 1000) {

			System.out.println("ok ");
			System.out.println("Lembrar dos traders desonestos: " + memory);
			System.out.println("Tem validador: " + hasValidator);
			System.out.println("Tem categorias: " + !typeAgnostic);
			System.out.println("Tem depósito caução: " + securityDeposit);
			System.out.println("Tem feedback: " + hasFeedback);
			if(hasFeedback) {
				System.out.println("Taxa de feedback: " + (feedbackPercent*100) + "%");
			}
			System.out.println("Percentual de honestidade: " + (honestPercent*100) + "%");
			System.out.println("Passos (amostra): " + this.stepCount);
			System.out.println("total de transacoes (amostra): " + this.totalTransactions);
			System.out.println("total de transacoes mal sucedidas (amostra): " + this.transactionFail);
			System.out.println("total de transacoes evitadas (amostra): " + this.AvoidedFailTransactions);
			System.out.println("nao deu match (amostra): " + this.notMatch);
			
			double terminate = (System.currentTimeMillis() - TraderContextBuilder.timeIn) / 1000;
			System.out.println("Tempo total: " + terminate);
			System.out.println();
			System.out.println();
			System.out.println();
			
			final Path path = Paths.get(DATA_OUTPUT);
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	sb.append("1,");
		    	sb.append((memory?1:0) + ",");
		    	sb.append((hasValidator?1:0) + ",");
		    	sb.append((typeAgnostic?1:0) + ",");
		    	sb.append((securityDeposit?1:0) + ",");
		    	sb.append((hasFeedback?1:0) + ",");
		    	sb.append(feedbackPercent + ",");
		    	sb.append(honestPercent + ",");
		    	sb.append(stepCount + ",");
		    	sb.append(totalTransactions + ",");
		    	sb.append(transactionFail + ",");
		    	sb.append(AvoidedFailTransactions + ",");
		    	sb.append(terminate );
				Files.write(path, Arrays.asList(sb), StandardCharsets.UTF_8,
				    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
				Collection items = new ArrayList<Object>();
				for (Iterator iterator = context.iterator(); iterator.hasNext();) {
					items.add(iterator.next());		
					
				}
				this.resetHash();
				TransactionType.reset();
				if(!context.removeAll(items)) {
					throw new RuntimeException("ERRADO");
				}
				RunEnvironment.getInstance().endRun();
				//RunEnvironment.getInstance().resumeRun();
			    //TraderContextBuilder.rebuild(context);
			    
//				for (int i = 0; i < context.size(); i++) {
//					context.remove(context.getRandomObject());
//				}
				
				return;
			} catch (IOException e) {}
			
			
		}
		
		List<GridPoint> emptySites = findEmptySites();
		
		// TODO add Grid.moveTo(Object o, GridPoint pt) to Repast API
		if (emptySites.size() > 0) grid.moveTo(trader, emptySites.get(0).getX(), emptySites.get(0).getY());
		
	}
	
	/**
	 * Provides a list of adjacent (unoccupied) sites in the Trader's Moore 
	 * neighborhood.  The list of sites is shuffled.
	 * 
	 * @return the list of adjacent sites.
	 */
	private List<GridPoint> findEmptySites(){
		List<GridPoint> emptySites = new ArrayList<GridPoint>();
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid)context.getProjection("Grid");
		GridPoint pt = grid.getLocation(this);
		
		// Find Empty Moore neighbors
		// TODO automate via Repast API
		if (!grid.getObjectsAt(pt.getX()-1,pt.getY()+1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()-1,pt.getY()+1));
		if (!grid.getObjectsAt(pt.getX(),pt.getY()+1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX(),pt.getY()+1));
		if (!grid.getObjectsAt(pt.getX()+1,pt.getY()+1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()+1,pt.getY()+1));
		if (!grid.getObjectsAt(pt.getX()+1,pt.getY()).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()+1,pt.getY()));
		if (!grid.getObjectsAt(pt.getX()+1,pt.getY()-1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()+1,pt.getY()-1));
		if (!grid.getObjectsAt(pt.getX(),pt.getY()-1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX(),pt.getY()-1));
		if (!grid.getObjectsAt(pt.getX()-1,pt.getY()-1).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()-1,pt.getY()-1));
		if (!grid.getObjectsAt(pt.getX()-1,pt.getY()).iterator().hasNext())
			emptySites.add(new GridPoint(pt.getX()-1,pt.getY()));
		
		Collections.shuffle(emptySites);
		
		return emptySites;
	}

	public TransactionType getType() {
		return type;
	}

	public void addProduct(TransactionType type, List<Product> productsAux) {
		//TraderType type2 = lowerStock();
		if(productsAux != null) {
		
			for (Iterator<Product> iterator = productsAux.iterator(); iterator.hasNext();) {
				Product product = iterator.next();
				product.newOwner(this);
				this.getBalanceByType(type).add(product);
				this.getBackupByType(type).add(product);
				this.allProducts.add(product);
			}
			
			

			
			
			
			//List<Product> products = this.balance.get(type);
			//products.addAll(productsAux);
			//this.allProducts.addAll(productsAux);
		}
		
//		switch (type) {
//		case ProteinProducer:
//			proteinCounter += count;
//			return;
//		case FatProducer:
//			fatCount += count;
//			return;
//		case SuggarProducer:
//			suggarCount += count;
//			return;
//		}
	}

	public List<Product> takeBatch() {
		return takeUnit(batch);
	}

	public List<Product> takeUnit() {
		return takeUnit(1);
	}

	public List<Product> takeUnit(int unit) {
		
		List<Product> products = this.balance.get(type);
		List<Product> returnProducts = new ArrayList<Product>();
		int length = products.size();
		int unitAux = length>=unit?unit:length;
		for (int i = length-1; i >= length-unitAux; i--) {
			returnProducts.add(products.remove(products.size()-1));
		}
		return returnProducts;
		
//		switch (type) {
//		case ProteinProducer:
//			if(proteinCounter >= unit)
//				proteinCounter -= unit;
//				return unit;
//		case FatProducer:
//			if(fatCount >= unit)
//				fatCount -= unit;
//				return unit;
//		case SuggarProducer:
//			if(suggarCount >= unit)
//				suggarCount -= unit;
//				return unit;
//		}
//		return 0;
	}
}
