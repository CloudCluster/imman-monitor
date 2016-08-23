package ccio.imman.monitor;

public class ImageNode {

	private Integer dropletId;
	private String dropletName;
	private String privateIp;
	private String publicIp;
	private String cloudFlareDnsId;
	
	public ImageNode(){
		super();
	}
	
	public ImageNode(Integer dropletId, String dropletName, String privateIp, String publicIp) {
		super();
		this.dropletId = dropletId;
		this.dropletName = dropletName;
		this.privateIp = privateIp;
		this.publicIp = publicIp;
	}
	
	public String getPrivateIp() {
		return privateIp;
	}
	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public Integer getDropletId() {
		return dropletId;
	}
	public void setDropletId(Integer dropletId) {
		this.dropletId = dropletId;
	}
	public String getDropletName() {
		return dropletName;
	}
	public void setDropletName(String dropletName) {
		this.dropletName = dropletName;
	}
	public String getCloudFlareDnsId() {
		return cloudFlareDnsId;
	}
	public void setCloudFlareDnsId(String cloudFlarDnsId) {
		this.cloudFlareDnsId = cloudFlarDnsId;
	}

	@Override
	public String toString() {
		return "ImageNode [dropletId=" + dropletId + ", dropletName=" + dropletName + ", privateIp=" + privateIp
				+ ", publicIp=" + publicIp + ", cloudFlareDnsId=" + cloudFlareDnsId + "]";
	}
}
