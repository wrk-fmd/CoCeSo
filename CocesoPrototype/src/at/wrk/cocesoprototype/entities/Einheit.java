package at.wrk.cocesoprototype.entities;

public class Einheit {
	
	private int id = -1;
	private int vorfallId = -1;
	private String name;
	private String typ;
	private String status;
	
	public Einheit() {
		super();
	}
	
	public Einheit(int id, int vorfallId, String name, String typ, String status) {
		super();
		this.id = id;
		this.vorfallId = vorfallId;
		this.name = name;
		this.typ = typ;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVorfallId() {
		return vorfallId;
	}

	public void setVorfallId(int vorfallId) {
		this.vorfallId = vorfallId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
