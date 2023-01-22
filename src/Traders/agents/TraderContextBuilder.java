package Traders.agents;

import java.util.Iterator;

import Traders.agents.enums.TraderType;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
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
		double honestPercent = (Double)p.getValue("honestPercent");
		
		GridFactoryFinder.createGridFactory(null).createGrid("Grid",
				context, GridBuilderParameters.singleOccupancy2D(new RandomGridAdder(),
								new WrapAroundBorders(), gridWidth, gridHeight));

		for (int i=0; i<initialTraders; i+=3){
			TraderType type = TraderType.ProteinProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}

		for (int i=1; i<initialTraders; i+=3){
			TraderType type = TraderType.SuggarProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}
		
		for (int i=2; i<initialTraders; i+=3){
			TraderType type = TraderType.FatProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}
		
		timeIn  = System.currentTimeMillis();

		return context;
	}

	public static void rebuild(Context context) {
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int gridWidth = (Integer)p.getValue("gridWidth");
		int gridHeight = (Integer)p.getValue("gridHeight");
		int initialTraders = (Integer)p.getValue("initialTraders");
		double honestPercent = (Double)p.getValue("honestPercent");

		for (int i=0; i<initialTraders; i+=3){
			TraderType type = TraderType.ProteinProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}

		for (int i=1; i<initialTraders; i+=3){
			TraderType type = TraderType.SuggarProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}
		
		for (int i=2; i<initialTraders; i+=3){
			TraderType type = TraderType.FatProducer;
			type.iamHonest(Math.random() <= honestPercent);
			context.add(new Trader(type));
		}
		
		timeIn  = System.currentTimeMillis();

	}

}
