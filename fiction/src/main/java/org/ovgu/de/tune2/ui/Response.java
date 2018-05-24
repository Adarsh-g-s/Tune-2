/**
 * 
 */
package org.ovgu.de.tune2.ui;

import java.io.Serializable;

/**
 * @author Suhita Ghosh
 *
 */
public class Response implements Serializable{

	Question question;
	Answer answer;
	Long start;
	Long end;
	Long timetaken;

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public Long getTimetaken() {
		return timetaken;
	}

	public void setTimetaken(Long timetaken) {
		this.timetaken = timetaken;
	}

}
