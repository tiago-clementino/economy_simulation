package economy_simulation.agents;

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
