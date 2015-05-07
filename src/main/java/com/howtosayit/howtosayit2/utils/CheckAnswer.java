package com.howtosayit.howtosayit2.utils;



public class CheckAnswer {
    private String regExp = "\\.|,|!|\\?|(|)|-";
    private String correctAnswer;

    public boolean isCorrect(String answer) {
        String cleanAnswer = answer.replaceAll(regExp, "");
        return cleanAnswer.equalsIgnoreCase(correctAnswer.substring(0, cleanAnswer.length()));
    }

    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.replaceAll(regExp, ""));
    }

    public void setCorrect(String englishPhrase) {
        correctAnswer = englishPhrase.replaceAll(regExp, "");
    }
}
