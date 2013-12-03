package me.kwm4385.mathpractice.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
	
	private Map<Question, Answer> questions = new HashMap<Question, Answer>();
	private List<Question> wrong = new ArrayList<Question>();
	private double score = 1.0;
	
	public void addQuestionResult(Question question, Answer answer) {
		questions.put(question, answer);
		if(question.getAnswer() != answer) {
			wrong.add(question);
		}
		score = calculateScore();
	}
	
	public void removeQuestionResult(Question question) {
		questions.remove(question);
		if(wrong.contains(question)) {
			wrong.remove(question);
		}
		score = calculateScore();
	}
	
	public Answer getQuestionResult(Question q) {
		return questions.get(q);
	}
	
	public double getScore() {
		return score;
	}
	
	public int totalQuestions() {
		return questions.keySet().size();
	}
	
	public List<Question> getWrongQuestions() {
		List<Question> res = new ArrayList<Question>();
		Collections.copy(res, wrong);
		return res;
	}

	private double calculateScore() {
		if(questions.isEmpty()) {
			return 1.0;
		} else {
			return (double) (questions.size() - wrong.size()) / (double) questions.size();
		}
	}
}