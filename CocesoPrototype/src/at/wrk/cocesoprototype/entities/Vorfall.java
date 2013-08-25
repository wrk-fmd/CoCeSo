package at.wrk.cocesoprototype.entities;


public class Vorfall {
	
	private int id = -1;
	private String start;
	private String end;
	private String typ;
	private String text;
	private String status;
	
	public Vorfall() {
		super();
	}
	
	public Vorfall(int id, String start, String end, String typ, String text,
			String status) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.typ = typ;
		this.text = text;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
