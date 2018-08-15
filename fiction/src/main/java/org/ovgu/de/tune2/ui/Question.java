package org.ovgu.de.tune2.ui;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */

/**
 * @author Suhita Ghosh
 *
 */
public class Question implements Serializable {

	private static final long serialVersionUID = 8504104824817499398L;
	private int id;
	private String text;
	private List<String> options;
	private boolean isEasy;
	private String imagePath;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public boolean isEasy() {
		return isEasy;
	}

	public void setEasy(boolean isEasy) {
		this.isEasy = isEasy;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Question(int id, String text, List<String> options, boolean isEasy, String imagePath) {
		super();
		this.id = id;
		this.text = text;
		this.options = options;
		this.isEasy = isEasy;
		this.imagePath = imagePath;
	}

}
