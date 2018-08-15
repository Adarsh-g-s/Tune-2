package org.ovgu.de.tune2.ui;
/**
 * 
 */

import java.io.Serializable;
import java.util.List;

/**
 * @author Suhita Ghosh
 *
 */
public class Answer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3279988271961764680L;
	private int id;
	private List<String> options;

	
	public Answer(int id, List<String> options) {
		super();
		this.id = id;
		this.options = options;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}


}
