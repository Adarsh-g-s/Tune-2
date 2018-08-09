package org.ovgu.de.fiction.web;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final String THANK_U_PAGE = "/fiction/faces/thanku.xhtml";
	private static final String MID_BREAK_JPG = "images/Taking-a-break.jpg";

	private static final long serialVersionUID = 462006850003220169L;

	private static final int RELAXATION_PERIOD = 10000; // 10s
	private static final int Q_A_PERIOD = 60000; // 60s
	private static long TIME;
	private static long RELAX_TIME;
	final static Logger LOG = Logger.getLogger(FRSearchBean.class);
	private static final int MID_BREAK_QUESTION_NO = 19;
	String relaxationPicFolder;
	private static int gifCounter;

	private static int MAX_QUESTION_NO; // starts from 0
	private String question;
	private String id;
	private String image;
	private boolean imagePresent;
	List<Question> questions;
	List<String> options;
	private String responseOption;
	private String relaxationGif;
	private boolean showQuiz;

	private int counter;
	private StringBuffer QUIZ_LOG;
	private Object startDateTime;
	private Object endDateTime;
	boolean midBreak;

	private static Logger log = Logger.getLogger(FRSearchBean.class);

	/**
	 * The method should be called at page load. Initializes questions and options
	 * and other necessary stuff
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() {
		{
			LOG.info("Loading Questions and options !!! ");
			showQuiz = true;
			try {
				relaxationPicFolder = FRGeneralUtils.getPropertyValPhase1("relax.gif.folder");
			} catch (IOException e2) {
				e2.printStackTrace();
				log.error("Relaxation gif not loaded!");
			}
			// init
			QUIZ_LOG = new StringBuffer();
			TIME = System.currentTimeMillis();
			RELAX_TIME = System.currentTimeMillis();
			startDateTime = new Date();
			questions = new ArrayList<>();
			counter = 0;
			midBreak = false;
			question = null;
			gifCounter = 1;

			try {
				MAX_QUESTION_NO = Integer.parseInt(FRGeneralUtils.getPropertyValPhase1("question.no"));
			} catch (NumberFormatException | IOException e1) {
				e1.printStackTrace();
				log.error("MAX_QUESTION_NO not parsed!");
			}

			for (int i = 0; i <= MAX_QUESTION_NO; i++) {
				String text = null;
				boolean isEasy = false;
				String imagePath = null;
				List<String> optionList = null;
				try {
					text = FRGeneralUtils.getPropertyValPhase1(i + ".q.text");
					optionList = (List<String>) Arrays
							.asList(FRGeneralUtils.getPropertyValPhase1(i + ".q.options").split("#"));
					String img = FRGeneralUtils.getPropertyValPhase1(i + ".q.image");
					imagePath = img.trim().length() > 1 ? "images/" + img : "";
					isEasy = FRGeneralUtils.getPropertyValPhase1(i + ".q.type") == "e" ? true : false;
				} catch (Exception e) {
					e.printStackTrace();
					log.error("questions could not be loaded");
				}

				Question q = new Question(i, text, optionList, isEasy, imagePath);
				questions.add(q);
			}
			try {
				display();
			} catch (IOException e) {
				e.printStackTrace();
				log.error("exception at display()");
			}
		}
	}

	/**
	 * @throws IOException
	 * @throws Exception
	 *             The method contains the logic for navigation and all UI related
	 *             stuff
	 */
	public void display() throws IOException {

		if (question != null) {
			// check if user has answered question
			if (responseOption == null || responseOption.length() < 1) {
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
						"Please answer the question and then click on Next");
				FacesContext.getCurrentInstance().addMessage("", fm);
				return;
			}

			/**
			 * check if mid-break question has been reached and mid-break not given. Then
			 * give break
			 */
			if (counter == MID_BREAK_QUESTION_NO && !midBreak) {
				// show only break pic
				imagePresent = true;
				image = MID_BREAK_JPG;
				midBreak = true;
				question = "";
				options = null;
				// log for question ans b4 mid-break
				updateTime(false);
				return;

			}

			/**
			 * check if mid-break question has been reached and mid-break given. Then
			 * proceed to next q
			 */

			if (counter == MID_BREAK_QUESTION_NO && midBreak) {
				// log mid-break time and update time var
				updateTime(true);
				// to prevent relaxation phase starting again!
				RELAX_TIME = System.currentTimeMillis();
			} else {
				// log for Q
				updateTime(false);
			}

			// start relaxation period
			if ((System.currentTimeMillis() - RELAX_TIME) >= Q_A_PERIOD && (counter <= MAX_QUESTION_NO)) {
				provideRelaxation();
			}
		}

		// if max no of questions reached write to file and display thanku page
		if (counter > MAX_QUESTION_NO) {
			counter = 0;
			writeToFile(QUIZ_LOG.toString());
			FacesContext.getCurrentInstance().getExternalContext().redirect(THANK_U_PAGE);
			showQuiz = false;

		} else {
			// init the next question and options
			Question questionDTO = questions.get(counter);
			question = questionDTO.getText();
			id = String.valueOf(questionDTO.getId());
			image = questionDTO.getImagePath();
			imagePresent = (image != null && image.trim().length() > 1) ? true : false;
			options = questionDTO.getOptions();
			counter++;
		}

		// new question timer starts
		TIME = System.currentTimeMillis();
		endDateTime = startDateTime;
		startDateTime = new Date();
		responseOption = null;

	}

	private void updateTime(boolean relaxation) throws IOException {
		long prevTime = TIME;
		TIME = System.currentTimeMillis();
		endDateTime = startDateTime;
		startDateTime = new Date();

		String qType = relaxation ? "R" : FRGeneralUtils.getPropertyValPhase1(id + ".q.type");
		System.out.println(qType + "," + id + "," + startDateTime + "," + endDateTime + "," + (TIME - prevTime));
		QUIZ_LOG.append(qType + "," + id + "," + startDateTime + "," + endDateTime + "," + (TIME - prevTime) + "\n");
	}

	/**
	 * @param string
	 * @throws IOException
	 * 
	 *             The method writes neccesary info for TS segmentation to csv file
	 */
	private void writeToFile(String data) throws IOException {
		if (!(data.endsWith("/") || data.endsWith("\\"))) {
			data.concat("/");
		}
		File file = new File(FRGeneralUtils.getPropertyValPhase1("file.log.loc") + "LOG_" + System.currentTimeMillis());
		FileUtils.writeStringToFile(file, data);

	}

	private void provideRelaxation() throws IOException {
		int size = Integer.parseInt(FRGeneralUtils.getPropertyValPhase1("relax.gif.no"));

		System.out.println("Relaxation GIF is :" + relaxationGif);
		try {
			Thread.sleep(RELAXATION_PERIOD);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RELAX_TIME = System.currentTimeMillis();
		// time update for relaxation
		updateTime(true);
		gifCounter = gifCounter > (size - 1) ? 1 : ++gifCounter;
	}

	/**
	 * The method captures the response given by the user
	 */
	@SuppressWarnings("deprecation")
	public void capture() {
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("myRadVal", responseOption);
		relaxationGif = relaxationPicFolder + "/" + gifCounter + ".gif";
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
