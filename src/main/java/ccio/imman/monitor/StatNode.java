package ccio.imman.monitor;

import java.util.Map;

public class StatNode {

	private Map<String, Object> node;
	private Map<String, Object> imagesMap;
	public Map<String, Object> getNode() {
		return node;
	}
	public void setNode(Map<String, Object> node) {
		this.node = node;
	}
	public Map<String, Object> getImagesMap() {
		return imagesMap;
	}
	public void setImagesMap(Map<String, Object> imagesMap) {
		this.imagesMap = imagesMap;
	}
}
