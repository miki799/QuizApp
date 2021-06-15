package org.quizapp;

public class Answer {

    private int id;
    private String question;
    private String answer;
    private String correctAnswer;

    public Answer(int questionId, String question, String answer, String correctAnswer) {
        this.id = questionId;
        this.question = question;
        this.answer = answer;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int questionId) {
        this.id = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String username) {
        this.question = username;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
