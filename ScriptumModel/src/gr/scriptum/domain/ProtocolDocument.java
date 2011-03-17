package gr.scriptum.domain;

// Generated Jan 29, 2011 1:29:09 PM by Hibernate Tools 3.4.0.Beta1

/**
 * ProtocolDocument generated by hbm2java
 */
public class ProtocolDocument extends ScriptumDocument  implements java.io.Serializable {

	private OutgoingProtocol outgoingProtocol;
	private IncomingProtocol incomingProtocol;

	public ProtocolDocument() {
		super();
	}

	
	public ProtocolDocument(OutgoingProtocol outgoingProtocol,
			IncomingProtocol incomingProtocol, DocumentType documentType) {
		super(documentType);
		this.outgoingProtocol = outgoingProtocol;
		this.incomingProtocol = incomingProtocol;
	}


	public ProtocolDocument(Integer id, byte[] content, Long documentSize, String documentMime,
			String documentName, Integer documentNumber,
			String documentKeywords, DocumentType documentType,
			
			OutgoingProtocol outgoingProtocol,
			IncomingProtocol incomingProtocol) {
		super(id, content, documentSize, documentMime,
			  documentName,documentNumber,
			  documentKeywords, documentType);
		
		this.outgoingProtocol = outgoingProtocol;
		this.incomingProtocol = incomingProtocol;
	}


	public OutgoingProtocol getOutgoingProtocol() {
		return this.outgoingProtocol;
	}

	public void setOutgoingProtocol(OutgoingProtocol outgoingProtocol) {
		this.outgoingProtocol = outgoingProtocol;
	}

	public IncomingProtocol getIncomingProtocol() {
		return this.incomingProtocol;
	}

	public void setIncomingProtocol(IncomingProtocol incomingProtocol) {
		this.incomingProtocol = incomingProtocol;
	}

}
