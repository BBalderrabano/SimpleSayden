package sayden;

import java.io.File;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapLoader {
	
	private int mapWidth = 0;
	private int mapHeight = 0;

	private String mapReference;
//	private LinkedList<Creature> npcs;
	
	private World world;
	private Tile[][] tiles;
	
	public World preBuild(String mapName, StuffFactory factory)
	{
//		npcs = new LinkedList<Creature>();
		
		if( checkFile(mapName, factory) ){
			String[] lines = mapReference.split("\n");
			
			//En caso de que el mapa sea mas chico que el mundo soportado
			mapHeight = lines.length < Constants.WORLD_HEIGHT ? Constants.WORLD_HEIGHT : lines.length;
			mapWidth = lines[0].length() < Constants.WORLD_HEIGHT ? Constants.WORLD_HEIGHT : lines[0].length();
			
			tiles = new Tile[mapWidth][mapHeight];
			
			for(int y = 0; y < lines.length; y++){
				for(int x = 0; x < lines[y].length(); x++){
					char stringIndex = lines[y].replace("\n", "").replace("\r", "").replace("\t", "").charAt(x);
					
					if(stringIndex == ">".charAt(0)){
						tiles[x][y] = Tile.STAIRS_DOWN;
					}else if(stringIndex == "<".charAt(0)){
						tiles[x][y] = Tile.STAIRS_UP;
					}else{
						if(!(stringIndex >= '0' && stringIndex <= '9')){
							//En caso de que arranquemos con letras
							//Este es un fix puesto que cada numero identifica un tile, lo que delimita cada mapa a usar
							//9 tiles, gracias a esto tambien se podran usar letras, siendo la a = 10
							stringIndex -= 87;	// -87 para que a = 10
							tiles[x][y] = Tile.getById((int)stringIndex);
						}else{
							//Los primeros números
							tiles[x][y] = Tile.getById(Integer.parseInt(stringIndex+""));
						}
					}
				}
			}
						
			world = new World(tiles);	//Creamos el mapa
//			world.add(npcs);	//Le añadimos a los npc YA POPULADOS en checkFile()
		}

		return world;
	}
	
	/** Esta funcion se encarga de leer el mapa y popular los objetos correspondientes
	 *  dentro del juego. Esto incluye npcs y los tiles usados en cada mapa.*/
	private boolean checkFile(String mapName, StuffFactory factory){
		 try {
			File file = new File("maps/"+mapName+".xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			NodeList nodeLst = doc.getElementsByTagName("version");
			
			//Aqui se cargan las diferentes versiones del mapa (para soporte de diferentes tiles de mapas)
			//TODO: Implementar z_index
			for (int s = 0; s < nodeLst.getLength(); s++) {
			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	
			    	Element firstPersonElement = (Element)fstNode;
			    	
	                //---------------MAP-----------------
			    	NodeList mapElementList = firstPersonElement.getElementsByTagName("map");
			    	Element mapElement;
			    	
			    	if(mapElementList.getLength() > 1){
			    		Random rand = new Random();
			    		int mapNro = rand.nextInt((mapElementList.getLength() - 1) + 1);
			    		
			    		mapElement = (Element)mapElementList.item(mapNro);
			    	}else{
				    	mapElement = (Element)mapElementList.item(0);
			    	}
			    	
	                NodeList textMNList = mapElement.getChildNodes();

	                mapReference = ((Node)textMNList.item(0)).getNodeValue().trim();
	                mapReference = mapReference.replace("\r", "").replace("\t", "");
			    }
			}
			
//			nodeLst = doc.getElementsByTagName("npcs");
//
//			for (int s1 = 0; s1 < nodeLst.getLength(); s1++) {
//			    Node scndNode = nodeLst.item(s1);
//			    
//			    if (scndNode.getNodeType() == Node.ELEMENT_NODE) {
//			    	
//			    	Element firstPersonElement = (Element)scndNode;
//			    	
//			    	//---------------NPCS----------------
//			    	NodeList npcElementList = firstPersonElement.getElementsByTagName("npc");
//			    				    	
//			    	for(int t = 0; t < npcElementList.getLength(); t++){
//			    		Element npcElement = (Element)npcElementList.item(t);
//			    		NodeList textWNList = npcElement.getChildNodes();
//			    		
//			    		String npcName = ((Node)textWNList.item(0)).getNodeValue().trim();
//
//			    		if(npcName.equals("BLACKSMITH")){
//			    			int startPosX = Integer.parseInt(npcElement.getAttribute("x"));
//			    			int startPosY = Integer.parseInt(npcElement.getAttribute("y"));
//			    			int startPosZ = Integer.parseInt(npcElement.getAttribute("z"));
//			    			
//			    			Creature blacksMith = factory.newBlacksmith(startPosZ, factory.player);
//			    			if(startPosY > 0) { blacksMith.y = startPosY; }
//			    			if(startPosX > 0) { blacksMith.x = startPosX; }
//
//			    			npcs.add(blacksMith);
//			    		}
//			       	}
//			    }
//			}				
		 } catch (Exception e) {
		    System.out.println("MAP LOADER ERROR: " + e.getLocalizedMessage());
		    e.printStackTrace();
		    return false;
		 }
		 
		 return true;
	}
}
