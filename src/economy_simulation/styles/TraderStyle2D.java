package economy_simulation.styles;

import java.awt.Color;

import economy_simulation.agents.Trader;
import economy_simulation.transactions.TransactionType;
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
			Integer size = TransactionType.getTypeNumberOf(((Trader)agent).getType());
			Float allCollors = (size*200F*3)/TransactionType.getTypes().size();
			int red = Math.round(allCollors>200F?0:allCollors);
			allCollors = allCollors>200F?allCollors-200F:0;
			int green = Math.round(allCollors>200F?0:allCollors);
			allCollors = allCollors>200F?allCollors-200F:0;
			int blue = Math.round(allCollors>200F?0:allCollors);
			return new Color(red,green,blue);
		}
		return new Color(0,0,0);
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(15, 15);
		}
		return spatial;
	}
}
