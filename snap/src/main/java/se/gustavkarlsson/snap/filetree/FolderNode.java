package se.gustavkarlsson.snap.filetree;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.swt.graphics.Image;

import se.gustavkarlsson.snap.resources.Images;
import se.gustavkarlsson.snap.resources.Strings;

@Entity(name = "Folders")
public class FolderNode extends Node implements Label {

	@SuppressWarnings("unused")
	@Column(name = "FolderID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Node> children = new HashSet<Node>();

	public FolderNode() {
	}

	public FolderNode(String name) {
		super(name);
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public Node[] listChildren() {
		Node[] childArray = new Node[children.size()];
		return children.toArray(childArray);
	}

	public boolean addChild(Node child) {
		if (child == null) {
			throw new IllegalArgumentException(Strings.ARGUMENT_IS_NULL
					+ ": child");
		}

		boolean added = children.add(child);
		if (added) {
			child.setParent(this);
		}

		return added;
	}

	public boolean removeChild(Node child) {
		return children.remove(child);
	}

	public void removeAllChildren() {
		children.clear();
	}

	@Override
	public Image getImage() {
		return Images.FOLDER;
	}
}
