import java.math.BigDecimal;

/*
* Sample input java class
* */
public class EusDao {
	private int id;
	private String name;
	String vehType;
	String policyNumber;		
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVehType() {
		return vehType;
	}
	public void setVehType(String vehType) {
		this.vehType = vehType;
	}
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}
	public String getDriverDetails() {
		return driverDetails;
	}
	public void setDriverDetails(String driverDetails) {
		this.driverDetails = driverDetails;
	}
	public String getPolicyDetails() {
		return policyDetails;
	}
	public void setPolicyDetails(String policyDetails) {
		this.policyDetails = policyDetails;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	String driverDetails;
	String policyDetails;
	String account;
		
}
