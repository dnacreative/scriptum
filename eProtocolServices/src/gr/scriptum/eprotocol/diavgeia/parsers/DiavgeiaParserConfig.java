package gr.scriptum.eprotocol.diavgeia.parsers;

/**
 * Class that configures the DiavgeiaParser. Contains the mission critical parsing tokens
 * @author Mike Mountrakis mountrakis@uit.gr
 */
public class DiavgeiaParserConfig {
	String elementStart = "ns3:tag";
	String attributeUid = "uid";
	String elementDescritpion = "ns3:label";

	public DiavgeiaParserConfig() {

	}

	public String getElementStart() {
		return elementStart;
	}

	public void setElementStart(String elementStart) {
		this.elementStart = elementStart;
	}

	public String getAttributeUid() {
		return attributeUid;
	}

	public void setAttributeUid(String attributeUid) {
		this.attributeUid = attributeUid;
	}

	public String getElementDescritpion() {
		return elementDescritpion;
	}

	public void setElementDescritpion(String elementDescritpion) {
		this.elementDescritpion = elementDescritpion;
	}

}