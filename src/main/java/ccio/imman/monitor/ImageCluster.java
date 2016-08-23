package ccio.imman.monitor;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

public class ImageCluster {
	
	private String clusterName;
	private Integer sshKeyId;
	private String privateSshKey;
	private String publicSshKey;
	private List<ImageNode> imageNodes;
	private String widths;
	private String heights;
	private String s3Access;
	private String s3Secret;
	private String s3Bucket;
	private String secret;
	private String ccioSubDomain;
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public Integer getSshKeyId() {
		return sshKeyId;
	}
	public void setSshKeyId(Integer sshKeyId) {
		this.sshKeyId = sshKeyId;
	}
	public String getPrivateSshKey() {
		return privateSshKey;
	}
	public void setPrivateSshKey(String privateSshKey) {
		this.privateSshKey = privateSshKey;
	}
	public String getPublicSshKey() {
		return publicSshKey;
	}
	public void setPublicSshKey(String publicSshKey) {
		this.publicSshKey = publicSshKey;
	}
	public List<ImageNode> getImageNodes() {
		return imageNodes;
	}
	public void setImageNodes(List<ImageNode> imageNodes) {
		this.imageNodes = imageNodes;
	}
	public String getS3Access() {
		return s3Access;
	}
	public void setS3Access(String s3Access) {
		this.s3Access = s3Access;
	}
	public String getS3Secret() {
		return s3Secret;
	}
	public void setS3Secret(String s3Secret) {
		this.s3Secret = s3Secret;
	}
	public String getS3Bucket() {
		return s3Bucket;
	}
	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}
	public String getWidths() {
		return widths;
	}
	public void setWidths(String widths) {
		this.widths = widths;
	}
	public String getHeights() {
		return heights;
	}
	public void setHeights(String heights) {
		this.heights = heights;
	}
	public String getSecret() {
		if(secret == null){
			secret = RandomStringUtils.random(32, "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890");
		}
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getCcioSubDomain() {
		return ccioSubDomain;
	}
	public void setCcioSubDomain(String ccioSubDomain) {
		this.ccioSubDomain = ccioSubDomain;
	} 
}
