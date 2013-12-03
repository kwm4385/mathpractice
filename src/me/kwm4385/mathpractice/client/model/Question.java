package me.kwm4385.mathpractice.client.model;

public class Question {
	
	private String imageURL;
	private Answer answer;
	private int chapter;
	private int section;
	private int number;
	
	public Question(String imageURL, Answer answer, int chapter, int section, int number) {
		this.imageURL = imageURL;
		this.answer = answer;
		this.chapter = chapter;
		this.section = section;
		this.number = number;
	}

	public int getChapter() {
		return chapter;
	}

	public int getSection() {
		return section;
	}

	public int getNumber() {
		return number;
	}

	public String getImageURL() {
		return imageURL;
	}

	public Answer getAnswer() {
		return answer;
	}
	
	@Override
	public String toString() {
		return "Question: " + imageURL + " Answer: " + answer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + chapter;
		result = prime * result + ((imageURL == null) ? 0 : imageURL.hashCode());
		result = prime * result + number;
		result = prime * result + section;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (answer != other.answer)
			return false;
		if (chapter != other.chapter)
			return false;
		if (imageURL == null) {
			if (other.imageURL != null)
				return false;
		} else if (!imageURL.equals(other.imageURL))
			return false;
		if (number != other.number)
			return false;
		if (section != other.section)
			return false;
		return true;
	}
}