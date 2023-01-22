package Traders.styles;

import java.awt.Color;

import Traders.agents.Trader;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class TraderStyle2D extends DefaultStyleOGL2D{

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
	}
	
	@Override
	public Color getColor(Object agent) {
		if(agent instanceof Trader) {
			switch (((Trader)agent).getType()) {
			case ProteinProducer: {
				return new Color(0,168,0);//green
				}
			case FatProducer: {
				return new Color(0,0,168);//green
				}
			case SuggarProducer: {
				return new Color(168,0,0);//green
				}
			}
		}
		return new Color(168,0,0); // dark red
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(15, 15);
		}
		return spatial;
	}
}
