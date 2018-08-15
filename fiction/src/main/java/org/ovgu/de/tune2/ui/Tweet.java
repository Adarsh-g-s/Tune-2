/**
 * 
 */
package org.ovgu.de.tune2.ui;

import java.io.Serializable;

/**
 * @author Suhita Ghosh
 *
 */
public class Tweet implements Serializable {

	private static final long serialVersionUID = 2275154356020676474L;
	private String id;
	private String text;
	private String relevant;
	private String factual;
	private String sentiment;

	public Tweet(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRelevant() {
		return relevant;
	}

	public void setRelevant(String relevant) {
		this.relevant = relevant;
	}

	public String getFactual() {
		return factual;
	}

	public void setFactual(String factual) {
		this.factual = factual;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", text=" + text + ", relevant=" + relevant + ", factual=" + factual + ", sentiment="
				+ sentiment + "]";
	}

}
