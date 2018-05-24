package org.ovgu.de.fiction.web;

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

	private static final long serialVersionUID = 462006850003220169L;

	private static final String WEB_CONTEXT_PATH = "web.ctx.path";
	private static long TIME;
	final static Logger LOG = Logger.getLogger(FRSearchBean.class);

	private static final int MAX_QUESTION_NO = 3; // starts from 0
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

	/**
	 * The method generates the Genre dropdown on page load
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() {
		LOG.info("Init !!! ");
		showQuiz = true;
		TIME = System.currentTimeMillis();
		// init var
		counter = 0;
		question = null;
		/*id = null;
		image = null;
		imagePresent = false;
		buttonname = null;
		options = null;
		responseOption = null;
		relaxationGif = null;
		*/

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
			System.out.println(q);
			questions.add(q);
		}
		display();
	}

	public void capture() {
		RequestContext context = RequestContext.getCurrentInstance();  
	    context.addCallbackParam("myRadVal", responseOption);
	    System.out.println("radioVal: "+responseOption);
	}
	/**
	 * @throws Exception
	 */
	public void display() {
		System.out.println(responseOption);
		if (question != null) {
			System.out.println("radioVal: "+responseOption);
			if (responseOption == null || responseOption.length() < 1) {
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please answer the question and then click on Next");
				FacesContext.getCurrentInstance().addMessage("", fm);
				//TIME = System.currentTimeMillis();
				return;
			}

			long prevTime = TIME;
			TIME = System.currentTimeMillis();
			System.out
					.println("Time taken to answer question " + id + " :" + (TIME - prevTime) + new Date().toString());

			// start relaxation period
			try {
				Thread.sleep(RELAXATION_PERIOD);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// end relaxation period
			prevTime = TIME;
			TIME = System.currentTimeMillis();
			System.out.println("Time taken for relaxation :" + (TIME - prevTime) + new Date().toString());
		}

		// Random r = new Random(questions.size() - 1);
		// int n = r.nextInt(counter--);

		// TIME = System.currentTimeMillis();

		if (counter > MAX_QUESTION_NO) {
			counter = 0;
			//buttonname = "Finish";
			image = "/images/thanks.jpg";
			showQuiz = false;
		} else {
			Question questionDTO = questions.get(counter);
			question = questionDTO.getText();
			id = String.valueOf(questionDTO.getId());
			relaxationGif = "2anim.gif";
			image = questionDTO.getImagePath();
			imagePresent = (image != null && image.trim().length() > 1) ? true : false;
			options = questionDTO.getOptions();
			System.out.println(counter);
			System.out.println(questionDTO.toString());
			counter++;
			buttonname = "Next";
		}

		System.out.println(showQuiz);
		// new question timer starts
		TIME = System.currentTimeMillis();
		responseOption=null;

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
