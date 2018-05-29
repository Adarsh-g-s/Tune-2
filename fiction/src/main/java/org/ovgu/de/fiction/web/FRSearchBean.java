package org.ovgu.de.fiction.web;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ovgu.de.fiction.utils.FRGeneralUtils;
import org.ovgu.de.tune2.ui.Question;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class FRSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final int RELAXATION_PERIOD = 10000; // 10s
	private static final int Q_A_PERIOD = 60000; // 10s

	private static final long serialVersionUID = 462006850003220169L;

	private static final String WEB_CONTEXT_PATH = "web.ctx.path";
	private static long TIME;
	private static long RELAX_TIME;
	final static Logger LOG = Logger.getLogger(FRSearchBean.class);

	private static int MAX_QUESTION_NO; // starts from 0
	private String question;
	private String id;
	private String image;
	private boolean imagePresent;
	List<Question> questions = new ArrayList<>();
	String buttonname;
	List<String> options = new ArrayList<>();
	private String responseOption;
	private String relaxationGif;
	private boolean showQuiz;

	private int counter;
	private int imgCounter;
	private StringBuffer QUIZ_LOG;
	private Object startDateTime;
	private Object endDateTime;

	/**
	 * The method generates the Genre dropdown on page load
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() {
		LOG.info("Init !!! ");
		showQuiz = true;
		QUIZ_LOG = new StringBuffer();
		TIME = System.currentTimeMillis();
		RELAX_TIME = System.currentTimeMillis();
		startDateTime = new Date();
		// init var
		counter = 0;
		imgCounter = 0;
		question = null;
		try {
			MAX_QUESTION_NO = Integer.parseInt(FRGeneralUtils.getPropertyValTune2("question.no"));
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i <= MAX_QUESTION_NO; i++) {
			String text = null;
			boolean isEasy = false;
			String imagePath = null;
			List<String> optionList = null;
			try {
				text = FRGeneralUtils.getPropertyValTune2(i + ".q.text");
				optionList = (List<String>) Arrays
						.asList(FRGeneralUtils.getPropertyValTune2(i + ".q.options").split("#"));
				String img = FRGeneralUtils.getPropertyValTune2(i + ".q.image");
				imagePath = img.trim().length() > 1 ? "images/" + img : "";
				isEasy = FRGeneralUtils.getPropertyValTune2(i + ".q.type") == "e" ? true : false;
			} catch (IOException e) {
				e.printStackTrace();
			}

			Question q = new Question(i, text, optionList, isEasy, imagePath);
			questions.add(q);
		}
		try {
			display();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void capture() {
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("myRadVal", responseOption);
		// System.out.println("radioVal: " + responseOption);
	}

	/**
	 * @throws IOException
	 * @throws Exception
	 */
	public void display() throws IOException {
		//endDateTime = startDateTime;
		//startDateTime = new Date();

		long prevTime;

		if (question != null) {
			// System.out.println("radioVal: " + responseOption);
			if (responseOption == null || responseOption.length() < 1) {
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
						"Please answer the question and then click on Next");
				FacesContext.getCurrentInstance().addMessage("", fm);
				// TIME = System.currentTimeMillis();
				return;
			}

			// time update
			prevTime = TIME;
			TIME = System.currentTimeMillis();
			endDateTime = startDateTime;
			startDateTime = new Date();
			
			System.out.println(FRGeneralUtils.getPropertyValTune2(id + ".q.type") + "," + id + "," + startDateTime + ","
					+ endDateTime + "," + (TIME - prevTime));
			QUIZ_LOG.append(FRGeneralUtils.getPropertyValTune2(id + ".q.type") + "," + id + "," + startDateTime + ","
					+ endDateTime + "," + (TIME - prevTime) + "\n");

			// start relaxation period
			if ((System.currentTimeMillis() - RELAX_TIME) >= Q_A_PERIOD) {
				try {
					Thread.sleep(RELAXATION_PERIOD);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// end relaxation period
				RELAX_TIME = System.currentTimeMillis();
				// time update
				prevTime = TIME;
				TIME = System.currentTimeMillis();
				endDateTime = startDateTime;
				startDateTime = new Date();
				System.out.println(
						"R," + id + "," + startDateTime + "," + new Date().toString() + "," + (TIME - prevTime));
				QUIZ_LOG.append(
						"R," + id + "," + startDateTime + "," + new Date().toString() + "," + (TIME - prevTime) + "\n");
			}

		}

		if (counter > MAX_QUESTION_NO) {
			counter = 0;
			image = "/images/thanks.jpg";
			showQuiz = false;
			writeToFile(QUIZ_LOG.toString());
		} else {
			//startDateTime = new Date();
			Question questionDTO = questions.get(counter);
			question = questionDTO.getText();
			id = String.valueOf(questionDTO.getId());
			relaxationGif = "2anim.gif";
			image = questionDTO.getImagePath();
			imagePresent = (image != null && image.trim().length() > 1) ? true : false;
			options = questionDTO.getOptions();
			counter++;
			buttonname = "Next";
		}

		// new question timer starts
		prevTime = TIME;
		TIME = System.currentTimeMillis();
		endDateTime = startDateTime;
		startDateTime = new Date();
		responseOption = null;

	}

	/**
	 * @param string
	 * @throws IOException
	 */
	private void writeToFile(String data) throws IOException {
		File file = new File(FRGeneralUtils.getPropertyValTune2("file.log.loc") + "LOG_" + System.currentTimeMillis());
		FileUtils.writeStringToFile(file, data);

	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public String getResponseOption() {
		return responseOption;
	}

	public void setResponseOption(String responseOption) {
		this.responseOption = responseOption;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public static void main(String[] args) throws IOException {
		FRSearchBean fr = new FRSearchBean();
		fr.init();
	}

	public boolean getImagePresent() {
		return imagePresent;
	}

	public void setImagePresent(boolean imagePresent) {
		this.imagePresent = imagePresent;
	}

	public String getButtonname() {
		return buttonname;
	}

	public void setButtonname(String buttonname) {
		this.buttonname = buttonname;
	}

	public String getRelaxationGif() {
		return relaxationGif;
	}

	public void setRelaxationGif(String relaxationGif) {
		this.relaxationGif = relaxationGif;
	}

	public boolean getShowQuiz() {
		return showQuiz;
	}

	public void setShowQuiz(boolean showQuiz) {
		this.showQuiz = showQuiz;
	}

}