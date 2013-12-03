package me.kwm4385.mathpractice.client;

import me.kwm4385.mathpractice.client.model.Answer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.reveregroup.gwt.imagepreloader.FitImage;

public class FeedbackPopupPanel extends DecoratedPopupPanel {
	
	public final static String BLUE = "#1E598C";
	public final static String GREEN = "#15AD2C";
	public final static String RED = "#D11F1F";
	
	private final int x;
	private final int y;
	private final Widget correct;
	private final Widget incorrect;
	
	private FitImage qImage1, qImage2;
	private Label usr, cor1, cor2 = new Label();
	
	public FeedbackPopupPanel(int x, int y) {
		super(true, false);
		this.x = x;
		this.y = y;
		this.setAnimationEnabled(true);
		this.setPopupPosition(x, y);
		qImage1 = new FitImage();
		qImage2 = new FitImage();
		qImage1.setMaxSize(550, 260);
		qImage2.setMaxSize(550, 260);
		
		// Correct
		correct = new VerticalPanel();
		correct.getElement().setAttribute("cellpadding", "5");
		((VerticalPanel) correct).setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		Label correctLab = new Label("Correct!");
		correctLab.setStylePrimaryName("greenlabel");
		correctLab.setStyleName("bold", true);
		((VerticalPanel) correct).add(correctLab);
		
		((VerticalPanel) correct).add(qImage1);
		
		cor1 = new Label("Correct answer: ");
		((VerticalPanel) correct).add(cor1);
		
		// Incorrect
		incorrect = new VerticalPanel();
		incorrect.getElement().setAttribute("cellpadding", "5");
		((VerticalPanel) incorrect).setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		Label incorrectLab = new Label("Incorrect");
		incorrectLab.setStylePrimaryName("redlabel");
		incorrectLab.setStyleName("bold", true);
		((VerticalPanel) incorrect).add(incorrectLab);
		
		((VerticalPanel) incorrect).add(qImage2);
		
		FlowPanel answers = new FlowPanel();
		usr = new Label("Your answer: ");
		answers.add(usr);
		answers.add(cor2);
		((VerticalPanel) incorrect).add(answers);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Document.get().getBody().getStyle().setBackgroundColor(BLUE);
			}
		});
	}
	
	public void showCorrect(String imageURL, Answer ans) {
		setWidget(correct);
		setPopupPosition(x, y);
		cor1.setText("Correct answer: " + ans);
		qImage1.setUrl(imageURL);
		setPopupPosition(x - (qImage1.getWidth() / 2)  + 30, y);
		Document.get().getBody().getStyle().setBackgroundColor(GREEN);
		show();
	}
	
	public void showIncorrect(Answer yours, Answer correct, String imageURL) {
		this.setWidget(incorrect);
		qImage2.setUrl(imageURL);
		usr.setText("Your answer: " + yours);
		cor2.setText("Correct answer: " + correct.toString());
		setPopupPosition(x - (qImage2.getWidth() / 2)  + 30, y);
		Document.get().getBody().getStyle().setBackgroundColor(RED);
		show();
	}
}
