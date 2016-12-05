import java.math.BigDecimal;


/*
* sample input class
* */
public class Dto {
	private String nme;
	String policyDet;
	private BigDecimal id;	
	String vehicleType;
	String polNum;	
	String drivrDetails;	
	String zei;
	public String getNme() {
		return nme;
	}
	public void setNme(String nme) {
		this.nme = nme;
	}
	public String getPolicyDet() {
		return policyDet;
	}
	public void setPolicyDet(String policyDet) {
		this.policyDet = policyDet;
	}
	
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getPolNum() {
		return polNum;
	}
	public void setPolNum(String polNum) {
		this.polNum = polNum;
	}
	public String getDrivrDetails() {
		return drivrDetails;
	}
	public void setDrivrDetails(String drivrDetails) {
		this.drivrDetails = drivrDetails;
	}
	public String getZei() {
		return zei;
	}
	public void setZei(String zei) {
		this.zei = zei;
	}
	
}
