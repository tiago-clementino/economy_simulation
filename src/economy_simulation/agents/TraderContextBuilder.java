package economy_simulation.agents;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import economy_simulation.transactions.TransactionType;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * Implementation of a bovine pulmonary artery endothelial (BPAE) Trader model 
 * with contact-inhibited migration as described in
 * 
 *     Lee, Y., S. Kouvroukoglou, L. McIntire, and K. Zygourakis, "A Traderular
 *         Automata Model for the Proliferation of Migrating Contact-Inhibited 
 *         Traders," Biophysics Journal, 69, 1284-1298, 1995.
 * 
 * @author Eric Tatara
 *
 */
public class TraderContextBuilder implements ContextBuilder {

	public static long timeIn = 0;
	public static boolean firstRun = true;

	@Override
	public Context build(Context context) {
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int gridWidth = (Integer)p.getValue("gridWidth");
		int gridHeight = (Integer)p.getValue("gridHeight");
		int initialTraders = (Integer)p.getValue("initialTraders");
		int totalTypes = (Integer)p.getValue("totalTypes");

		GridFactoryFinder.createGridFactory(null).createGrid("Grid",
				context, GridBuilderParameters.singleOccupancy2D(new RandomGridAdder(),
								new WrapAroundBorders(), gridWidth, gridHeight));

		TransactionType.reset();
		
		for (int k=0; k<totalTypes; k++){
			new TransactionType();
		}
		
		for (int i=0; i<initialTraders; i++){
			context.add(new Trader(TransactionType.getType(i%totalTypes)));
		}
		
//		for (int j = 0; j < totalTypes; j++) {
//			TransactionType type = new TransactionType();
//			for (int i=j; i<initialTraders; i+=totalTypes){
//				context.add(new Trader(type));
//			}
//		}
		
		final Path timelinePath = Paths.get(Trader.TIMELINE_OUTPUT);
		final Path dataPath = Paths.get(Trader.DATA_OUTPUT);
	    try {
	    	if(firstRun) {
	    		StringBuilder data = new StringBuilder();
		    	data.append("sucesso,memoria,tem_validador,typeAgnostic,securityDeposit,hasFeedback,feedbackPercent,");
				data.append("honestidade,stepCount,totalTransactions,transactionFail,AvoidedFailTransactions,uniqueValidators,terminate");
				Files.write(dataPath, Arrays.asList(data), StandardCharsets.UTF_8,
					    Files.exists(dataPath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
				StringBuilder timeline = new StringBuilder();
		    	timeline.append("run,hash,type,notMatch,memory,hasValidator,typeAgnostic,securityDeposit,hasFeedback,");
		    	timeline.append("feedbackPercent,honestPercent,stepCount,totalTransactions,transactionFail,AvoidedFailTransactions");	    	
				Files.write(timelinePath, Arrays.asList(timeline), StandardCharsets.UTF_8,
				    Files.exists(timelinePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
				
				firstRun = false;
	    	}
		} catch (IOException e) {}
	    
	    
		
		timeIn  = System.currentTimeMillis();

		return context;
	}

	public static void rebuild(Context context) {
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int gridWidth = (Integer)p.getValue("gridWidth");
		int gridHeight = (Integer)p.getValue("gridHeight");
		int initialTraders = (Integer)p.getValue("initialTraders");
		int totalTypes = (Integer)p.getValue("totalTypes");

		TransactionType.reset();
		
		for (int k=0; k<totalTypes; k++){
			new TransactionType();
		}
		
		for (int i=0; i<initialTraders; i++){
			context.add(new Trader(TransactionType.getType(i%totalTypes)));
		}

//		for (int j = 0; j < totalTypes; j++) {
//			TransactionType type = new TransactionType();
//			for (int i=j; i<initialTraders; i+=totalTypes){
//				context.add(new Trader(type));
//			}
//		}
		
		timeIn  = System.currentTimeMillis();

	}

}
