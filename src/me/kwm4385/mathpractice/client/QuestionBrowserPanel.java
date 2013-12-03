package me.kwm4385.mathpractice.client;

import java.util.HashMap;

import me.kwm4385.mathpractice.client.model.Question;
import me.kwm4385.mathpractice.client.model.Result;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.reveregroup.gwt.imagepreloader.FitImage;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class QuestionBrowserPanel extends VerticalPanel {
	
	private Result result;
	private FitImage qImage;
	private Label yourAns;
	private Label correctAns;
	
	public QuestionBrowserPanel(Result result) {
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.result = result;
		setSpacing(15);
		
		HorizontalPanel topPanel = new HorizontalPanel();
		add(topPanel);
		
		Label lblSelectQuestion = new Label("Select Question:");
		lblSelectQuestion.setStyleName("padded-label");
		topPanel.add(lblSelectQuestion);
		lblSelectQuestion.setWidth("100px");
		
		final ListBox questionSelector = new ListBox();
		final HashMap<String, Question> questions = new HashMap<String, Question>();
		for(Question q : result.getWrongQuestions()) {
			String qString = new StringBuilder().append(
					 q.getChapter()).append(".").append(q.getSection()).append(".").append(q.getNumber()).toString();
			questions.put(qString, q);
			questionSelector.addItem(qString);
		}
		questionSelector.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateQuestion(questions.get(questionSelector.getItemText(((ListBox) event.getSource()).getSelectedIndex())));
			}
		});
		questionSelector.setWidth("150px");
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	        	updateQuestion(questions.get(questionSelector.getItemText(questionSelector.getSelectedIndex())));
	        }
	    });
		topPanel.add(questionSelector);
		
		AbsolutePanel imagePanel = new AbsolutePanel();
		imagePanel.setPixelSize(550, 260);
		add(imagePanel);
		
		qImage = new FitImage();
		qImage.setMaxSize(550, 260);
		imagePanel.add(qImage, 0, 0);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		add(horizontalPanel);
		
		Label lblYourAnswer = new Label("Your Answer: ");
		lblYourAnswer.setStyleName("padded-label");
		horizontalPanel.add(lblYourAnswer);
		lblYourAnswer.setWidth("85px");
		
		yourAns = new Label();
		yourAns.setStyleName("scorelabel");
		horizontalPanel.add(yourAns);
		yourAns.setWidth("50px");
		
		Label lblCorrectAnswer = new Label("Correct Answer: ");
		lblCorrectAnswer.setStyleName("padded-label");
		horizontalPanel.add(lblCorrectAnswer);
		lblCorrectAnswer.setWidth("100px");
		
		correctAns = new Label();
		correctAns.setStyleName("scorelabel");
		horizontalPanel.add(correctAns);
		correctAns.setWidth("50");
	}

	private void updateQuestion(Question q) {
		qImage.setUrl(q.getImageURL());
		yourAns.setText(result.getQuestionResult(q).toString());
		correctAns.setText(q.getAnswer().toString());
	}
}
