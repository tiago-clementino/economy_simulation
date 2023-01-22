package Traders.agents;

import java.io.IOException;
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

import org.apache.log4j.Logger;

import Traders.agents.enums.TraderType;
import Traders.transactions.Transaction;
import Traders.utils.Random;
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
	private int proteinCounter = 0;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	private int fatCount = 0;
	
	/**
	 * The Trader reproduction counter.  When this counter reaches 0, the Trader may
	 *  divide if an adjacent site is available.
	 */
	private int suggarCount = 0;
	
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
	 * 
	 */
	private int maxFood;
	
	/**
	 * 
	 */
	private TraderType type;

	/**
	 * 
	 */
	private int batch;

	private int rounds;

	private boolean memory;

	private HashMap<Integer,Trader> nonHonests;

	private HashMap<Integer, Trader> honests;

	private boolean dontKnowTheValidator;

	private Boolean hasValidator;

	private int stepCount = 0;

	private Double honestPercent = 0.0;
	
	final static Logger logger = Logger.getLogger(Trader.class);
	
	public Trader(TraderType type){
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int divideTimeMin = (Integer)p.getValue("divideTimeMin");
		int divideTimeMax = (Integer)p.getValue("divideTimeMax");
		int moveTimeMin = (Integer)p.getValue("moveTimeMin");
		int moveTimeMax = (Integer)p.getValue("moveTimeMax");
		maxFood = (Integer)p.getValue("foodEnouth");
		batch = (Integer)p.getValue("batch");
		rounds = (Integer)p.getValue("rounds");
		memory = (Boolean)p.getValue("memory");
		dontKnowTheValidator = (Boolean)p.getValue("dontKnowTheValidator");
		hasValidator = (Boolean)p.getValue("hasValidator");
		honestPercent  = (Double)p.getValue("honestPercent");
		
		nonHonests = new LinkedHashMap<Integer,Trader>();
		honests = new LinkedHashMap<Integer,Trader>();
		
		// Set the initial move and divide times according to the specified parameters
		// and sampled from a Uniform distribution.
		divideTime = RandomHelper.nextIntFromTo(divideTimeMin, divideTimeMax);
		moveTime = RandomHelper.nextIntFromTo(moveTimeMin, moveTimeMax);
		
		reproduceCounter = divideTime;
		moveCounter = moveTime;
		produceCounter = moveTime;
		
		this.type = type;
		this.stepCount  = 0;
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
		else // continue waiting to move
			moveCounter--;
		
		// if the reproduce counter reaches 0
		if (reproduceCounter == 0){
			if(fueled()) {//may not be in the clause above because it decrement the reproduceCounter less than zero
				// and if there is an empty adjacent site
				if (!findEmptySites().isEmpty()) {
					reproduce();
					fatCount = 0;
					proteinCounter = 0;
					suggarCount = 0;
					stepCount = 0;
				}
				 
				// reset the reproduce counter
				reproduceCounter = divideTime;
			}
		}
		else // continue waiting to reproduce
			reproduceCounter--;
		
		// if the reproduce counter reaches 0
		if (produceCounter == 0){

			stepCount++;
			workout(type);
			
			if(stepCount > maxFood * 100) {
				killMe();
			}
			
			// reset the reproduce counter
			produceCounter = divideTime;
		}
		else // continue waiting to reproduce
			produceCounter--;
	}

	private boolean fueled() {
		return proteinCounter > maxFood && fatCount > maxFood && suggarCount > maxFood;
	}

	private void workout(TraderType type) {
		// if the reproduce counter reaches 0
		if (!isBalanced(type)){
			produce();
		}
		else {
			Transaction transaction = dealBestTransaction();
			if(transaction != null) {
				transfer(transaction);
			}
		}
	}

	private void produce() {
		switch (type) {
		case ProteinProducer:
			proteinCounter += 3;
			return;
		case FatProducer:
			fatCount += 3;
			return;
		case SuggarProducer:
			suggarCount += 3;
			return;
		}
		//
	}

	private void transfer(Transaction transaction) {
		transaction.pay(this);
	}

	private boolean isBalanced(TraderType type) {
		switch (type) {
		case ProteinProducer:
			return !(proteinCounter < fatCount || proteinCounter < suggarCount);
		case FatProducer:
			return !(fatCount < proteinCounter || fatCount < suggarCount);
		case SuggarProducer:
			return !(suggarCount < proteinCounter || suggarCount < fatCount);
		}
		return true;
	}

	/**
	 * Find a trader and a validator if fitted
	 * @return
	 */
	private Transaction dealBestTransaction() {
		
		if(hasStock()) {

			Context context = ContextUtils.getContext(this);
			Grid grid = (Grid)context.getProjection("Grid");
			GridPoint pt = grid.getLocation(this);
			
			Transaction transaction = null;
			try {
				for (int i = 0; i < rounds; i++) {
//					if((transaction = 
//							dealTransaction(
//									grid.getObjectsAt(
//											pt.getX()-Random.fraction(pt.getX()),
//											pt.getY()+Random.fraction(grid.getDimensions().getHeight()-pt.getY())).iterator().next())) != null)
//						return transaction;

					//faltou o hasnext					

					if (grid.getObjectsAt(pt.getX()-1,pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()+1).iterator().next());
					if (grid.getObjectsAt(pt.getX(),pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX(),pt.getY()+1).iterator().next());
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()+1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()+1).iterator().next());
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()).iterator().next());
					if (grid.getObjectsAt(pt.getX()+1,pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()+1,pt.getY()-1).iterator().next());
					if (grid.getObjectsAt(pt.getX(),pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX(),pt.getY()-1).iterator().next());
					if (grid.getObjectsAt(pt.getX()-1,pt.getY()-1).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()-1).iterator().next());
					if (grid.getObjectsAt(pt.getX()-1,pt.getY()).iterator().hasNext())
						return dealTransaction(grid.getObjectsAt(pt.getX()-1,pt.getY()).iterator().next());
					
					
//					if((transaction = 
//							dealTransaction(
//									grid.getObjectsAt(
//											Random.fraction(grid.getDimensions().getWidth()),
//											Random.fraction(grid.getDimensions().getHeight())).iterator().next())) != null)
//						return transaction;

				}
				
			} catch (NoSuchElementException e) {}
		}
		return null;
		
	}

	private Transaction dealTransaction(Object trader) {
		if(trader instanceof Trader) {// && !trader.equals(this)) {
			//if(((Trader) trader).getType().equals(lowerStock()) && ((Trader) trader).hasStock()) {
			if(((Trader) trader).hasStock()) {
				if(!memory || isThisHonest(((Trader) trader))){
					 //cria com esse
					Trader validator = takeBestValidator();//take the best or someone random
					return new Transaction(this,((Trader) trader),validator);
				}
			}
		}
		return null;
	}

	private Trader takeBestValidator() {
		if(!hasValidator) {
			return null;
		}
		
		return takeRandomHonest();
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

	private Trader takeRandomHonest() {
		int end = Random.fraction(honests.size());
		int count = 0;
		for (Iterator<Integer> iterator = honests.keySet().iterator(); iterator.hasNext();) {
			Integer i = iterator.next();
			count++;
			if(count > end) {
				return honests.get(i);
			}
		}		
		return null;
	}

	private boolean isThisHonest(Trader trader) {
		// verifica em uma lista
		return !nonHonests.containsKey(Integer.valueOf(trader.hashCode()));
	}

	public void setNonHonest(Trader validator) {
		honests.remove(Integer.valueOf(validator.hashCode()));
		nonHonests.put(Integer.valueOf(validator.hashCode()), validator);
	}

	public void setHonest(Trader validator) {
		nonHonests.remove(Integer.valueOf(validator.hashCode()));
		honests.put(Integer.valueOf(validator.hashCode()), validator);
	}

	private TraderType lowerStock() {
		switch (type) {
		case ProteinProducer:
			if(fatCount < suggarCount)
				return TraderType.FatProducer;
			else
				return TraderType.SuggarProducer;
		case FatProducer:
			if(proteinCounter < suggarCount)
				return TraderType.ProteinProducer;
			else
				return TraderType.SuggarProducer;
		case SuggarProducer:
			if(proteinCounter < fatCount)
				return TraderType.ProteinProducer;
			else
				return TraderType.FatProducer;
		}
		return type;
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
			//System.out.println("Validador tem que ser honesto: " + (Boolean)p.getValue("dontKnowTheValidator"));
			System.out.println("Percentual de honestidade: " + (honestPercent*100) + "%");
			
			double terminate = (System.currentTimeMillis() - TraderContextBuilder.timeIn) / 1000;
			System.out.println("Tempo total: " + terminate);
			System.out.println();
			System.out.println();
			System.out.println();
			
			final Path path = Paths.get("./data.csv");
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	sb.append("0,");
		    	sb.append((memory?1:0) + ",");
		    	sb.append((hasValidator?1:0) + ",");
		    	sb.append((dontKnowTheValidator?1:0) + ",");
		    	sb.append(honestPercent + ",");
		    	sb.append(terminate );
				Files.write(path, Arrays.asList(sb), StandardCharsets.UTF_8,
				    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);

			    TraderContextBuilder.rebuild(context);
				
			} catch (IOException e) {}
		    

		}
	}
	
	/**
	 * Produce a daughter Trader and move it to an empty adjacent site.
	 */
	private void reproduce(){		
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid)context.getProjection("Grid");
		
		TraderType type = this.type.cloneTarderType();
		type.iamHonest(Math.random() <= honestPercent);
		Trader trader = new Trader(this.type);	
		context.add(trader);
		
		if(context.size() >= 1000) {

			System.out.println("ok ");
			System.out.println("Lembrar dos traders desonestos: " + memory);
			System.out.println("Tem validador: " + hasValidator);
			//System.out.println("Validador tem que ser honesto: " + (Boolean)p.getValue("dontKnowTheValidator"));
			System.out.println("Percentual de honestidade: " + (honestPercent*100) + "%");
			
			double terminate = (System.currentTimeMillis() - TraderContextBuilder.timeIn) / 1000;
			System.out.println("Tempo total: " + terminate);
			System.out.println();
			System.out.println();
			System.out.println();
			
			final Path path = Paths.get("./data.csv");
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	sb.append("1,");
		    	sb.append((memory?1:0) + ",");
		    	sb.append((hasValidator?1:0) + ",");
		    	sb.append((dontKnowTheValidator?1:0) + ",");
		    	sb.append(honestPercent + ",");
		    	sb.append(terminate );
				Files.write(path, Arrays.asList(sb), StandardCharsets.UTF_8,
				    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
				Collection items = new ArrayList<Object>();
				for (Iterator iterator = context.iterator(); iterator.hasNext();) {
					items.add(iterator.next());					
				}
				context.removeAll(items);

			    TraderContextBuilder.rebuild(context);
			    
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

	public TraderType getType() {
		return type;
	}

	public void addProduct(TraderType type, int count) {
		//TraderType type2 = lowerStock();
		switch (type) {
		case ProteinProducer:
			proteinCounter += count;
			return;
		case FatProducer:
			fatCount += count;
			return;
		case SuggarProducer:
			suggarCount += count;
			return;
		}
	}

	public int takeBatch() {
		return takeUnit(batch);
	}

	public int takeUnit() {
		return takeUnit(1);
	}

	public int takeUnit(int unit) {
		switch (type) {
		case ProteinProducer:
			if(proteinCounter >= unit)
				proteinCounter -= unit;
				return unit;
		case FatProducer:
			if(fatCount >= unit)
				fatCount -= unit;
				return unit;
		case SuggarProducer:
			if(suggarCount >= unit)
				suggarCount -= unit;
				return unit;
		}
		return 0;
	}
}
