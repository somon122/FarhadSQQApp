package com.farhad.quiz_question;

public class Questions {


    public String mQuestions[] = {


            " _ is a girl.",
            " I__ English.",
            " Complete the missing letter D__g? ",
            " What is your___? ",
            " She will go__ Bangladesh?",
            " Complete the missing letter E__T?",
            " Green is __? ",
            " Do you __ rice? ",
            " Complete the missing letter M__M? ",
            " I go___ school by bus? ",
            " I____ books? ",
            " Complete the missing letter C___T?",
            " I'm drinking____?",
            " Complete the missing letter b____k?",
            " Complete the missing letter S____Y?",
            " He_my Friend.",
            " I am__cricket.",
            " Dog is __.",
            " My name__ Khan.",
            " I__ you."

    };

    private String mChoices[][] = {


            {"She", "He", "It", "I"},
            {"study", "take", "go", "come"},
            {"o", "s", "t", "p"},
            {"name", "mine", "nam", "none"},
            {"on", "at", "to", "none"},
            {"A", "B", "q", "c"},
            {"color", "Food", "foot", "red"},
            {"eat", "ate ", "eats", "none"},
            {"O", "A", "U", "T"},
            {"to", "on", "at", "none"},
            {"read", "see", "keep", "go"},
            {"A", "p", "E", "F"},
            {"Juice", "candy", "juice", "rice"},
            {"oo", "OO", "Uu", "ee"},
            {"A", "E", "R", "h"},
            {"is", "a ", "this ", "are"},
            {"player", "plays", "play", "playing"},
            {"animal", "bird", "tiger", "human"},
            {"Is", "a ", "it", "are"},
            {"Hate", "love", "live", "leave"}

    };

    private String mCarrectAnswer[] = {

            "She", "study", "o", "name", "to", "A", "color", "eat", "A", "to", "read", "A", "juice", "oo", "A", "is", "playing", "animal", "is", "love"};

    public String getQuestion(int a) {
        String question = mQuestions[a];
        return question;
    }

    public String getChoices1(int a) {
        String choice = mChoices[a][0];
        return choice;
    }

    public String getChoices2(int a) {
        String choice = mChoices[a][1];
        return choice;
    }

    public String getChoices3(int a) {
        String choice = mChoices[a][2];
        return choice;
    }

    public String getChoices4(int a) {
        String choice = mChoices[a][3];
        return choice;
    }

    public String getCarrectAnswer(int a) {

        String answer = mCarrectAnswer[a];
        return answer;
    }


}
