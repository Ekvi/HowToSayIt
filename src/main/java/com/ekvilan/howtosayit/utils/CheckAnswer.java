package com.ekvilan.howtosayit.utils;



public class CheckAnswer {
    private String regExp = "\\.|,|!|\\?|(|)|-|:|;";
    private String correctAnswer;

    public boolean isCorrect(String answer) {
        String cleanAnswer = answer.replaceAll(regExp, "");
        boolean isCorrect = false;

        if(cleanAnswer.length() <= correctAnswer.length()) {
            isCorrect = cleanAnswer.equalsIgnoreCase(correctAnswer.substring(0, cleanAnswer.length()));
        }
        return isCorrect;
    }

    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.replaceAll(regExp, ""));
    }

    public void setCorrect(String englishPhrase) {
        correctAnswer = englishPhrase.replaceAll(regExp, "");
    }
}
