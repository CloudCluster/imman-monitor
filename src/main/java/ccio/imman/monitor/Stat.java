package ccio.imman.monitor;

import java.util.List;

public class Stat {

	private int size;
	private List<StatNode> members;
	private String state;
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public List<StatNode> getMembers() {
		return members;
	}
	public void setMembers(List<StatNode> members) {
		this.members = members;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
