package me.kwm4385.mathpractice.client;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import me.kwm4385.mathpractice.client.model.Answer;
import me.kwm4385.mathpractice.client.model.Question;
import me.kwm4385.mathpractice.client.model.QuestionLoader;
import me.kwm4385.mathpractice.client.model.Result;
import me.kwm4385.mathpractice.client.model.Util;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widget.client.TextButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.reveregroup.gwt.imagepreloader.FitImage;
import com.reveregroup.gwt.imagepreloader.ImagePreloader;

public class MathPractice implements EntryPoint {
	
	// MD5 encoded password hash to match
	public static final String ACCESS_PASSWORD_HASH = "2dc98ca9b8a93b07ebe3bd51cea364b3";
	
	public final RootPanel rootPanel = RootPanel.get("mathPractice");
	
	private FeedbackPopupPanel popup;
	private QuestionLoader ql = new QuestionLoader();
	private Set<RadioButton> opts = new HashSet<RadioButton>();
	private TextButton nextButton;
	private AbsolutePanel mainPanel;
	private HorizontalPanel loginPanel;
	private VerticalPanel selectionPanel;
	private int questionsDone = 1;
	private Label progress;

	/**
	 * Entry point method.
	 */
	@Override
	public void onModuleLoad() {
		loadLoginPanel();
		loginPanel.setVisible(true);
		
		// Confirm closing
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
            	if(mainPanel.isVisible()) {
            		event.setMessage("Leaving now will reset your progress.");
            	}  
            }
        });	
	}
	
	private void loadLoginPanel() {
		loginPanel = new HorizontalPanel();
		loginPanel.setVisible(false);
		rootPanel.add(loginPanel, 130, 10);
		loginPanel.setSpacing(5);
		
		Label label = new Label("Enter password: ");
		label.setStylePrimaryName("padded");
		loginPanel.add(label);
		
		final Label incorrectpw = new Label("Incorrect password");
		incorrectpw.setVisible(false);
		incorrectpw.setStylePrimaryName("redlabel");
		incorrectpw.setStyleName("padded", true);
		
		final PasswordTextBox pwbox = new PasswordTextBox();
		loginPanel.add(pwbox);
		final ClickHandler cl = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String pw = pwbox.getText();
				try {
					pw = Util.bytesToHex(MessageDigest.getInstance("MD5").digest(pw.getBytes("UTF-8")));
				} catch (Exception e) {
					Window.alert("An error occurred: " + e.getMessage());
				}
				if(pw.equalsIgnoreCase(ACCESS_PASSWORD_HASH)) {
					incorrectpw.setVisible(false);
					loadSelectionPanel();
					loginPanel.setVisible(false);
					selectionPanel.setVisible(true);
				} else {
					incorrectpw.setVisible(true);
					pwbox.setText("");
				}
			}		
		};
		pwbox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getUnicodeCharCode() == 13) {
					cl.onClick(null);
				}
			}	
		});
		
		Button submit = new Button("Log in");
		submit.setStylePrimaryName("padded-label");
		loginPanel.add(submit);
		submit.addClickHandler(cl);
		
		loginPanel.add(incorrectpw);
		
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	            pwbox.setFocus(true);
	        }
	    });
	}
	
	private void loadSelectionPanel() {
		selectionPanel = new VerticalPanel();
		selectionPanel.setVisible(false);
		selectionPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		selectionPanel.setSpacing(20);
		rootPanel.add(selectionPanel, 190, 5);
		
		Label title = new Label("Select sections to quiz from:");
		selectionPanel.add(title);
		
		Tree selectionTree = new Tree();
		selectionTree.setAnimationEnabled(true);
		final HashMap<String, CheckBox> sections = new HashMap<String, CheckBox>();
		for(int c = 0; c < QuestionLoader.chapterCount(); c++) {
			TreeItem root = new TreeItem(new Label("Chapter " + c));
			selectionTree.addItem(root);
			for(int s = 0; s < QuestionLoader.sectionCount(c); s++) {
				CheckBox cb = new CheckBox("Section " + new Integer(s + 1).toString());
				cb.setValue(false);
				sections.put(c + " " + new Integer(s + 1), cb);
				root.addItem(new TreeItem(cb));
			}
			root.setState(false);
		}
		selectionPanel.add(selectionTree);
		
		Button start = new Button("Start");
		start.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ArrayList<Question> questions = new ArrayList<Question>();
				for(String s : sections.keySet()) {
					if(sections.get(s).getValue()) {
						questions.addAll(ql.getChapter(Integer.parseInt(s.split(" ")[0])).getSection(Integer.parseInt(s.split(" ")[1])));
					}
				}
				if(questions.isEmpty()) {
					Window.alert("Please choose at least one section to quiz from.");
				} else {
					Random r = new Random();
					for(int index = 0; index < questions.size(); index += 1) {  
					    Collections.swap(questions, index, r.nextInt(questions.size()));  
					} 
					for(Question q : questions) {
						ImagePreloader.load(q.getImageURL(), null);
					}
					loadMainPanel(questions);
					selectionPanel.setVisible(false);
					mainPanel.setVisible(true);
				}
			}
		});
		selectionPanel.add(start);
		selectionPanel.setCellHorizontalAlignment(start, VerticalPanel.ALIGN_RIGHT);
	}
	
	private void loadMainPanel(Collection<Question> questions) {	
		final int totalQuestions = questions.size();
		final LinkedList<Question> qQueue = new LinkedList<Question>(questions);
		final Question[] current = { qQueue.remove() };
		final Result result = new Result();
		final Label correctLabel;
		final Label incorrectLabel;
		
		mainPanel = new AbsolutePanel();
		mainPanel.setVisible(false);
		rootPanel.add(mainPanel, 10, 10);
		mainPanel.setSize("600px", "400px");
		
		OptionChangeListener lstn = new OptionChangeListener();
		
		RadioButton rdbtnA = new RadioButton("opt", "A");
		rdbtnA.setSize("10px", "10px");
		rdbtnA.setStylePrimaryName("scorelabel");
		rdbtnA.addValueChangeHandler(lstn);
		rdbtnA.setHTML("A");
		mainPanel.add(rdbtnA, 210, 300);
		rdbtnA.setSize("37px", "19px");
		opts.add(rdbtnA);
		
		RadioButton rdbtnB = new RadioButton("opt", "B");
		rdbtnB.setSize("10px", "10px");
		rdbtnB.setStylePrimaryName("scorelabel");
		rdbtnB.addValueChangeHandler(lstn);
		rdbtnB.setHTML("B");
		mainPanel.add(rdbtnB, 253, 300);
		rdbtnB.setSize("37px", "19px");
		opts.add(rdbtnB);
		
		RadioButton rdbtnC = new RadioButton("opt", "C");
		rdbtnC.setSize("10px", "10px");
		rdbtnC.setStylePrimaryName("scorelabel");
		rdbtnC.addValueChangeHandler(lstn);
		rdbtnC.setHTML("C");
		mainPanel.add(rdbtnC, 296, 300);
		rdbtnC.setSize("39px", "19px");
		opts.add(rdbtnC);
		
		RadioButton rdbtnD = new RadioButton("opt", "D");
		rdbtnD.setSize("10px", "10px");
		rdbtnD.setStylePrimaryName("scorelabel");
		rdbtnD.addValueChangeHandler(lstn);
		rdbtnD.setHTML("D");
		mainPanel.add(rdbtnD, 341, 300);
		rdbtnD.setSize("37px", "19px");
		opts.add(rdbtnD);
		
		RadioButton rdbtnE = new RadioButton("opt", "E");
		rdbtnE.setSize("10px", "10px");
		rdbtnE.setStylePrimaryName("scorelabel");
		rdbtnE.addValueChangeHandler(lstn);
		rdbtnE.setHTML("E");
		mainPanel.add(rdbtnE, 384, 300);
		rdbtnE.setSize("37px", "19px");
		opts.add(rdbtnE);
		
		final FitImage image = new FitImage();
		mainPanel.add(image, 40, 10);
		image.setMaxSize(550, 260);
		image.setUrl(current[0].getImageURL());
		
		progress = new Label("Question: " + questionsDone + "/" + totalQuestions);
		mainPanel.add(progress, 265, 360);
		
		correctLabel = new Label("Correct: 0");
		incorrectLabel = new Label("Incorrect: 0");
		mainPanel.add(correctLabel, 245, 377);
		mainPanel.add(incorrectLabel, 320, 377);
		
		
		final TextButton finishButton = new TextButton("Finish Now");
		finishButton.setEnabled(false);
		finishButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(((TextButton) event.getSource()).isEnabled()) {
					finish(result);
				}	
			}		
		});
		mainPanel.add(finishButton, 10, 362);
		
		popup = new FeedbackPopupPanel(rootPanel.getAbsoluteLeft() + 275, rootPanel.getAbsoluteTop() - 20);
		nextButton = new TextButton("Next");
		nextButton.setEnabled(false);
		nextButton.addClickHandler(new ClickHandler() {
			
			int correct = 0;
			int incorrect = 0;
			
			@Override
			public void onClick(ClickEvent event) {
				if(!nextButton.isEnabled()) {
					return;
				}
				finishButton.setEnabled(true);
				Answer ans = null;
				for(RadioButton b : opts) {
					if(b.getValue()) {
						ans = Answer.valueOf(b.getText());
						b.setValue(false);
						nextButton.setEnabled(false);
						break;
					}
				}
				
				if(current[0].getAnswer() == ans) {	
					popup.showCorrect(current[0].getImageURL(), current[0].getAnswer());
					correctLabel.setText("Correct: " + ++correct);
				} else {
					popup.showIncorrect(ans, current[0].getAnswer(), current[0].getImageURL());
					incorrectLabel.setText("Incorrect: " + ++incorrect);
				}
				
				result.addQuestionResult(current[0], ans);
				updateProgress(totalQuestions);
				if(!qQueue.isEmpty()) {
					current[0] = qQueue.remove();
					image.setUrl(current[0].getImageURL());
				} else {
					finish(result);
					image.setUrl("");
				}
			}
		});
		mainPanel.add(nextButton, 544, 362);
	}
	
	private void finish(Result result) {
		 DockPanel resRoot = new DockPanel();
		 resRoot.setSpacing(5);
		 rootPanel.add(resRoot, 5, 5);
		 mainPanel.setVisible(false);
		 resRoot.setVisible(true);
		 
		 Label score = new Label("Score: " + (result.totalQuestions() - result.getWrongQuestions().size()) + 
				 " / " + result.totalQuestions() + " = " + NumberFormat.getPercentFormat().format(result.getScore()));
		 score.setStylePrimaryName("scorelabel");
		 resRoot.add(score, DockPanel.NORTH);
		
		 TabPanel resultsPanel = new TabPanel();
		 resultsPanel.setAnimationEnabled(true);
		 resultsPanel.addStyleName("resultsTab");
		 resRoot.add(resultsPanel, DockPanel.CENTER);
		 resultsPanel.setAnimationEnabled(true);
		 
		 QuestionBrowserPanel wrongQuestions = new QuestionBrowserPanel(result);
		 resultsPanel.add(wrongQuestions, "Incorrect Questions");
		 resultsPanel.getTabBar().selectTab(0, true);
		 
		 WrongBySectionChart wbs = new WrongBySectionChart(result);
		 resultsPanel.add(wbs, "Incorrect by Section");
		 
		 DockPanel restartPanel = new DockPanel();
		 resRoot.add(restartPanel, DockPanel.SOUTH);
		 TextButton restart = new TextButton("Restart Quiz");
		 restart.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.Location.reload();
			}	 
		 });
		 restartPanel.add(restart, DockPanel.EAST);
		 restartPanel.setCellHorizontalAlignment(restart, DockPanel.ALIGN_RIGHT);
	}
	
	private void updateProgress(int totalQuestions) {
		questionsDone++;
		progress.setText("Question: " + questionsDone + "/" + totalQuestions);
	}
	
	/**
	 * Listener class for selecting answer choices.
	 * @author Kevin Moses (kwm4385@rit.edu)
	 */
	protected class OptionChangeListener implements ValueChangeHandler<Boolean> {
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			nextButton.setEnabled(((RadioButton) event.getSource()).getValue());
		}
	}
}
