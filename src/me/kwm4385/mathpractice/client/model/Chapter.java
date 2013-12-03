package me.kwm4385.mathpractice.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chapter {

	//private ArrayList<ArrayList<Question>> sections = new ArrayList<ArrayList<Question>>(99);
	private Question[][] sections = new Question[99][99];
	
	public Question[][] getSections() {
		return sections;
	}

	public void setSection(int secNumber, ArrayList<Question> questions) {
		sections[secNumber] = questions.toArray(new Question[questions.size()]);
	}
	
	public List<Question> getSection(int secNumber) {
		return Arrays.asList(sections[secNumber]);
	}
}
