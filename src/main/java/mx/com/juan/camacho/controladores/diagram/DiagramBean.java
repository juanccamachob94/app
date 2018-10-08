package mx.com.juan.camacho.controladores.diagram;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ManagedBean
@ViewScoped
public class DiagramBean {
	
	private static final long serialVersionUID = 1L;
	
    private DefaultDiagramModel model;
    private List<String> concepts;
    
    public List<String> getConcepts() {
    	return this.concepts;
    }
    private int getRandom(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    public void setConcepts(List<String> concepts) {
        this.model = new DefaultDiagramModel();
        this.model.setMaxConnections(-1);
        this.model.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        StraightConnector connector = new StraightConnector();
        connector.setPaintStyle("{strokeStyle:'#EE6E73', lineWidth:3}");
        connector.setHoverPaintStyle("{strokeStyle:'red'}");
        this.model.setDefaultConnector(connector);
    	this.concepts = concepts;
    	int c = concepts.size();
    	for(int i = 0; i < c; i++) this.agregarElemento(this.model, concepts.get(i),Integer.toString(getRandom(200,400)),Integer.toString(getRandom(200,400)), "#fff000");
    }
    
    private DotEndPoint createDotEndPoint(EndPointAnchor anchor, String color) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'"+color+"'}");
        endPoint.setHoverStyle("{fillStyle:'"+color+"'}");
         
        return endPoint;
    }
    
    private EndPoint createRectangleEndPoint(EndPointAnchor anchor, String color) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setTarget(true);
        endPoint.setStyle("{fillStyle:'"+color+"'}");
        endPoint.setHoverStyle("{fillStyle:'"+color+"'}");
         
        return endPoint;
    }
    
    
    public void agregarElemento(DefaultDiagramModel m, String concepto, String posX, String posY, String representacion) {
        Element element;
        element = new Element(concepto,posX,posY);
        element.addEndPoint(createDotEndPoint(EndPointAnchor.BOTTOM,representacion));
        element.addEndPoint(createRectangleEndPoint(EndPointAnchor.TOP,representacion));
        element.setDraggable(true);
        m.addElement(element);
    }
     
    public DiagramModel getModel() {
        return model;
    }
}
