package se.gustavkarlsson.snap.domain;

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import se.gustavkarlsson.snap.resources.Strings;

public abstract class Node implements Cloneable {
	
	protected String name;
	protected FolderNode parent = null;

	public Node(String name) {
		if (name == null) {
			throw new IllegalArgumentException(Strings.ARGUMENT_IS_NULL
					+ ": name");
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean hasParent() {
		return (parent != null);
	}

	public FolderNode getParent() {
		return parent;
	}

	protected void setParent(FolderNode parent) {
		this.parent = parent;
	}

	protected boolean isAncestor(Node node) {
		if (node == null) {
			return false;
		}
		if (node == this) {
			return true;
		}
		return isAncestor(node.getParent());
	}
	
	public List<String> getNodePath(List<String> path) {
		if (parent != null) {
			path.add(0, name);
			parent.getNodePath(path);
		}
		return path;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(17, 31);
		builder.append(name);
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Node node = (Node) obj;
		boolean nameEquals = name == node.getName()
				|| (name != null && name.equals(node.getName()));
		return nameEquals;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Node clone() {
		Node clone = null;
		try {
			clone = (Node) super.clone();
			clone.name = name;
		} catch (CloneNotSupportedException e) {
			// TODO fatal? error message
			e.printStackTrace();
		}
		return clone;
	}
}
