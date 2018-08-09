package org.ovgu.de.tune2.web;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.ovgu.de.tune2.ui.Tweet;
import org.ovgu.de.tune2.utils.FRGeneralUtils;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class Phase2Bean implements Serializable {

	private static final long serialVersionUID = 462006850003220169L;

	private static final String NO = "no";
	private static final String YES = "yes";
	private static final String NEGATIVE = "Negative";
	private static final String POSITIVE = "Positive";
	private static final String NON_FACTUAL_OPT = "Non-factual";
	private static final String FACTUAL_OPT = "Factual";
	private static final String NON_RELEVANT_OPT = "Non-Relevant";
	private static final String RELEVANT_OPT = "Relevant";

	private static final String THANK_U_PAGE = "/fiction/faces/thanku.xhtml";
	private static final String MID_BREAK_JPG = "images/Taking-a-break.jpg";

	private static final int RELAXATION_PERIOD = 10000; // 10s
	private static final int Q_A_PERIOD = 60000; // 60s
	private static long TIME;
	private static long RELAX_TIME;
	final static Logger LOG = Logger.getLogger(Phase2Bean.class);
	private static final int MID_BREAK_TWEET_NO = 300;
	String relaxationPicFolder;
	private static int gifCounter;

	private static int MAX_TWEET_NO; // starts from 0
	private String tweetId;
	private String tweetText;
	private String relevant;
	private String factual;
	private String sentiment;
	private Long relAnnotateTime;
	private Long factAnnotateTime;

	/*
	 * 
	 * private boolean imagePresent;
	 * 
	 * List<String> options; private String responseOption;
	 */
	private String image;
	List<Tweet> tweetList;
	List<Tweet> annotatedTweetList;
	List<String> relevantOptions = new ArrayList<>();
	List<String> factualOptions = new ArrayList<>();
	List<String> sentiOptions = new ArrayList<>();

	private String relaxationGif;
	private boolean showQuiz;

	private int counter;
	private StringBuffer QUIZ_LOG;
	private Object startDateTime;
	private Object endDateTime;
	boolean midBreak;

	private int twtCtr;

	private static Logger log = Logger.getLogger(Phase2Bean.class);

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
				relaxationPicFolder = FRGeneralUtils.getPropertyValPhase2("relax.gif.folder");
			} catch (IOException e2) {
				e2.printStackTrace();
				log.error("Relaxation gif not loaded!");
			}
			// init
			QUIZ_LOG = new StringBuffer();
			TIME = System.currentTimeMillis();
			RELAX_TIME = System.currentTimeMillis();
			startDateTime = new Date();
			tweetList = new ArrayList<>();
			annotatedTweetList = new ArrayList<>();
			counter = 0;
			midBreak = false;
			gifCounter = 1;
			clearcurrentTweetInfo();
			relAnnotateTime = null;
			factAnnotateTime = null;
			File tfile = null;
			String twtCtrFileName = null;
			// create file for tweet counter
			try {
				{
					twtCtrFileName = FRGeneralUtils.getPropertyValPhase2("file.log.loc") + "twtCtr";
					tfile = new File(twtCtrFileName);
					tfile.createNewFile();
				}
			} catch (IOException e2) {
				LOG.error("Tweet counter File could not be created");
			}
			try {
				String content = new String(Files.readAllBytes(Paths.get(twtCtrFileName)));
				if (content != null && !content.trim().equals("")) {
					try {
						twtCtr = Integer.parseInt(content);
						if (twtCtr == 9)
							twtCtr = 0;
					} catch (Exception e) {
						LOG.error("tweet counter content not valid and not an integer!" + e.getMessage());
					}
				} else {
					twtCtr = 0;
				}
				MAX_TWEET_NO = Integer.parseInt(FRGeneralUtils.getPropertyValPhase2("tweet.no"));
			} catch (NumberFormatException | IOException e1) {
				e1.printStackTrace();
				log.error("MAX_TWEET_NO not parsed!");
			}

			int start = twtCtr * MAX_TWEET_NO;
			int end = start + MAX_TWEET_NO;
			for (int i = start; i < end; i++) {
				StringBuffer text = null;
				StringBuffer id = null;
				// String imagePath = null;
				try {
					text = new StringBuffer(FRGeneralUtils.getPropertyValPhase2(i + ".twt.text"));
					id = new StringBuffer(FRGeneralUtils.getPropertyValPhase2(i + ".twt.id"));
				} catch (Exception e) {
					e.printStackTrace();
					log.error("tweets could not be loaded");
				}
				System.out.println(id);
				Tweet twt = new Tweet(id.toString(), StringEscapeUtils.unescapeHtml4(text.toString()));
				tweetList.add(twt);
			}
			System.out.println("Total tweets-" + tweetList.size());
			// relevant options
			relevantOptions.add(RELEVANT_OPT);
			relevantOptions.add(NON_RELEVANT_OPT);

			// factual options
			factualOptions.add(FACTUAL_OPT);
			factualOptions.add(NON_FACTUAL_OPT);

			// sentiment options
			sentiOptions.add(POSITIVE);
			sentiOptions.add(NEGATIVE);
			this.setTweetText(tweetList.get(0).getText());
			this.setTweetId(tweetList.get(0).getId().toString());
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
		System.out.println(relevant + factual + sentiment+counter);

		if (relevant == null && counter > 0) {
			FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					"Please mark the tweet as \"Relevant\" or \"Irrelevant\" then click on Next");
			FacesContext.getCurrentInstance().addMessage("", fm);
			return;
		}

		if (relevant != null) {

			if (relevant.equals(RELEVANT_OPT) && factual == null) {
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
						"Please mark the tweet as \"Factual\" or \"Non-Factual\" then click on Next");
				FacesContext.getCurrentInstance().addMessage("", fm);
				return;
			}

			if (relevant.equals(RELEVANT_OPT) && (NON_FACTUAL_OPT).equals(factual) && sentiment == null) {
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
						"Please mark the tweet's sentiment as \"Positive\" or \"Negative\" then click on Next");
				FacesContext.getCurrentInstance().addMessage("", fm);
				return;
			}

			Tweet annotatedTwt = new Tweet(tweetId, tweetText);
			annotatedTwt.setRelevant(relevant.equals(RELEVANT_OPT) ? YES : NO);
			if (relevant.equals(RELEVANT_OPT))
				annotatedTwt.setFactual(FACTUAL_OPT.equals(factual) ? YES : NO);
			if ((NON_FACTUAL_OPT).equals(factual))
				annotatedTwt.setSentiment(sentiment);

			annotatedTweetList.add(annotatedTwt);
			System.out.println(annotatedTwt.toString());

			/**
			 * check if mid-break question has been reached and mid-break not given. Then
			 * give break
			 */
			if (counter == MID_BREAK_TWEET_NO && !midBreak) {
				// show only break pic
				// imagePresent = true;
				image = MID_BREAK_JPG;
				midBreak = true;
				tweetId = "";
				// log for question ans b4 mid-break
				updateTime(false);
				return;

			}

			/**
			 * check if mid-break question has been reached and mid-break given. Then
			 * proceed to next q
			 */

			if (counter == MID_BREAK_TWEET_NO && midBreak) {
				// log mid-break time and update time var
				updateTime(true);
				// to prevent relaxation phase starting again!
				RELAX_TIME = System.currentTimeMillis();
			} else {
				// log for Q
				updateTime(false);
			}

			// start relaxation period
			if ((System.currentTimeMillis() - RELAX_TIME) >= Q_A_PERIOD && (counter < MAX_TWEET_NO)) {
				provideRelaxation();
			}
		}

		// if max no of questions reached write to file and display thanku page
		if (counter == MAX_TWEET_NO) {
			counter = 0;
			writeToFile(QUIZ_LOG.toString());
			FacesContext.getCurrentInstance().getExternalContext().redirect(THANK_U_PAGE);
			showQuiz = false;
			twtCtr++;
			String twtCtrFileName = FRGeneralUtils.getPropertyValPhase2("file.log.loc") + "twtCtr";

			Files.write(Paths.get(twtCtrFileName), String.valueOf(twtCtr).getBytes());

		} else {
			// init the next question and options
			clearcurrentTweetInfo();
			Tweet tweetDTO = tweetList.get(counter);
			tweetId = String.valueOf(tweetDTO.getId());
			tweetText = tweetDTO.getText();
			counter++;
		}

		// new question timer starts
		TIME = System.currentTimeMillis();
		endDateTime = startDateTime;
		startDateTime = new Date();
		System.out.println(counter);

	}

	private void clearcurrentTweetInfo() {
		tweetId = null;
		tweetText = null;
		relevant = null;
		factual = null;
		sentiment = null;
	}

	private void updateTime(boolean relaxation) throws IOException {
		System.out.println(relAnnotateTime + " " + factAnnotateTime + " " + TIME);
		long start = TIME;
		TIME = System.currentTimeMillis();
		endDateTime = startDateTime;
		startDateTime = new Date();

		String qType = relaxation ? "R" : "T";
		Long relTime = relAnnotateTime == null ? null : (relAnnotateTime - start);
		Long factTime = (factAnnotateTime != null && relAnnotateTime != null) ? (factAnnotateTime - relAnnotateTime)
				: null;
		Long sentiTime = factAnnotateTime == null ? null : (TIME - factAnnotateTime);

		System.out.println(qType + "," + tweetId + "," + startDateTime + "," + endDateTime + "," + relevant + ","
				+ relTime + "," + factual + "," + factTime + "," + sentiment + "," + sentiTime);
		QUIZ_LOG.append(qType + "," + tweetId + "," + startDateTime + "," + endDateTime + "," + relevant + "," + relTime
				+ "," + factual + "," + factTime + "," + sentiment + "," + sentiTime + "\n");

		relAnnotateTime = null;
		factAnnotateTime = null;

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
		File file = new File(
				FRGeneralUtils.getPropertyValPhase2("file.log.loc") + "LOG_P2_" + System.currentTimeMillis());
		FileUtils.writeStringToFile(file, data);

	}

	private void provideRelaxation() throws IOException {
		int size = Integer.parseInt(FRGeneralUtils.getPropertyValPhase2("relax.gif.no"));

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
	 * The method captures the relevance response given by the user
	 */
	@SuppressWarnings("deprecation")
	public void captureRelevance() {
		RequestContext context = RequestContext.getCurrentInstance();
		// context.addCallbackParam("myRadVal", responseOption);
		relaxationGif = relaxationPicFolder + "/" + gifCounter + ".gif";
		relAnnotateTime = System.currentTimeMillis();
		System.out.println(relevant);
	}

	/**
	 * The method captures the sentiment response given by the user
	 */
	@SuppressWarnings("deprecation")
	public void captureSentiment() {
		RequestContext context = RequestContext.getCurrentInstance();
		// context.addCallbackParam("myRadVal", responseOption);
		relaxationGif = relaxationPicFolder + "/" + gifCounter + ".gif";
		System.out.println(sentiment);
	}

	/**
	 * The method captures the factual response given by the user
	 */
	@SuppressWarnings("deprecation")
	public void captureFactual() {
		RequestContext context = RequestContext.getCurrentInstance();
		// context.addCallbackParam("myRadVal", responseOption);
		relaxationGif = relaxationPicFolder + "/" + gifCounter + ".gif";
		factAnnotateTime = System.currentTimeMillis();
		System.out.println(factual);
	}

	public String getRelaxationPicFolder() {
		return relaxationPicFolder;
	}

	public void setRelaxationPicFolder(String relaxationPicFolder) {
		this.relaxationPicFolder = relaxationPicFolder;
	}

	public static int getGifCounter() {
		return gifCounter;
	}

	public static void setGifCounter(int gifCounter) {
		Phase2Bean.gifCounter = gifCounter;
	}

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public String getRelevant() {
		return relevant;
	}

	public void setRelevant(String relevant) {
		this.relevant = relevant;
	}

	public List<String> getFactualOptions() {
		return factualOptions;
	}

	public void setFactualOptions(List<String> factualOptions) {
		this.factualOptions = factualOptions;
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

	public String getRelaxationGif() {
		return relaxationGif;
	}

	public void setRelaxationGif(String relaxationGif) {
		this.relaxationGif = relaxationGif;
	}

	public Object getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Object startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Object getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Object endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isMidBreak() {
		return midBreak;
	}

	public void setMidBreak(boolean midBreak) {
		this.midBreak = midBreak;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean getShowQuiz() {
		return showQuiz;
	}

	public void setShowQuiz(boolean showQuiz) {
		this.showQuiz = showQuiz;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Tweet> getTweetList() {
		return tweetList;
	}

	public void setTweetList(List<Tweet> tweetList) {
		this.tweetList = tweetList;
	}

	public List<String> getRelevantOptions() {
		return relevantOptions;
	}

	public void setRelevantOptions(List<String> relevantOptions) {
		this.relevantOptions = relevantOptions;
	}

	public List<String> getSentiOptions() {
		return sentiOptions;
	}

	public void setSentiOptions(List<String> sentiOptions) {
		this.sentiOptions = sentiOptions;
	}

}
