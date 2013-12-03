package me.kwm4385.mathpractice.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.Window;


public class QuestionLoader {
	
	/**
	 * 2D array to represent the numbers of chapters, sections, and questions to load.
	 * Each sub-array represents one chapter (numbered by index) and contains values for each section
	 * indicating the number of questions.
	 */
	public final static int[][] CHAPTERS_DESCRIPTOR = { {26, 29}, {31, 30, 30, 30, 27}, {30, 30, 36}, {28, 29, 28, 29, 35}, {24, 25, 25}, {16, 12, 18, 10, 23} };
	
	private ArrayList<Chapter> chapters = new ArrayList<Chapter>();
	
	public QuestionLoader() {
		loadQuestions();	
	}
	
	public static int chapterCount() {
		return CHAPTERS_DESCRIPTOR.length;
	}
	
	public static int sectionCount(int chapter) {
		if(chapter < CHAPTERS_DESCRIPTOR.length) {
			return CHAPTERS_DESCRIPTOR[chapter].length;
		} else {
			throw new IllegalArgumentException("Chapter " + chapter + " doesn't exist.");
		}
	}
	
	private void loadQuestions() {
		for(int c = 0; c < CHAPTERS_DESCRIPTOR.length; c++) {
			final Chapter chapter = new Chapter();
			for(int s = 1; s <= CHAPTERS_DESCRIPTOR[c].length; s++) {
				
				final ArrayList<Question> section = new ArrayList<Question>();
				final Map<Integer, Answer> answers = new HashMap<Integer, Answer>();
				final int sec = s;
				final int ch = c;
				
				try {
					new RequestBuilder(RequestBuilder.GET, "questions/c" + c + "/s" + s + "/ans.txt").sendRequest("", new RequestCallback() {
					    @Override
					    public void onResponseReceived(Request req, Response resp) {
						    String ans = resp.getText();
							for(String line : ans.split("[\\r\\n]+")) {
								String[] l = line.split(" ");
								answers.put(Integer.parseInt(l[0]), Answer.valueOf(l[1].toUpperCase()));
						    }
							for(int q = 1; q <= CHAPTERS_DESCRIPTOR[ch][sec-1]; q++) {
								Question question = new Question("questions/c" + ch + "/s" + sec + "/q" + q + ".png", answers.get(q), ch, sec, q);
								section.add(question);
							}
							chapter.setSection(sec, section);
					    }
					    @Override
					    public void onError(Request res, Throwable throwable) {
						    Window.alert("An error occurred loading questions. (RequestBuilder onError)");
						    throwable.printStackTrace();
					    }
					});
				} catch (RequestException e) {
					Window.alert("An error occurred loading questions. (RequestBuilder exception)");
					e.printStackTrace();
				}		
			}
			chapters.add(chapter);
		}
	}
	
	public Chapter getChapter(int num) {
		return chapters.get(num);
	}
}