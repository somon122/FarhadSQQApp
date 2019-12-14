package com.farhad.quiz_question;

public class Questions {


    public String mQuestions [] = {

          /*  "What is 10 * 10 =?",
            "What is 100 /10 =?",
            "What is 50-50 =?",
            "What is 0*0 =?",
            "What is 5+5 =?",
            "What is 10+5 =?",
            "What is 100+5 =?",
            "What is 10+50 =?",
            "What is 100+50 =?",
            "What is 100 * 100 =?",
            "What is 15+10+5 =?",
            "What is 50-{(10+5)*2} =?",
            "What is 10+5+0+10 =?",
            "What is 100*10% =?",
            "What is 999+1 =?",*/

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

private String mChoices [][]={

       /* {"200","300","100","10"},
        {"10","100","20","50"},
        {"50","0","100","nothing"},
        {"0","100","10","00"},
        {"12","15","20","10"},
        {"12","15","20","10"},
        {"105","1005","120","110"},
        {"60","50","40","0"},
        {"100","1500","200","150"},
        {"100000","1000","10000","100"},
        {"30","20","35","25"},
        {"50","20","100","200"},
        {"30","15","25","10"},
        {"Ans 12","Ans 15","20","10"},
        {"1200","1500","200","1000"},*/

        {"She","He","It","I"},
        {"study","take","go","come"},
        {"o","s","t","p"},
        {"name","mine","nam","none"},
        {"on","at","to","none"},
        {"A","B","q","c"},
        {"color","Food","foot","red"},
        {"eat","ate ","eats","none"},
        {"O","A","U","T"},
        {"to","on","at","none"},
        {"read","see","keep","go"},
        {"A","p","E","F"},
        {"Juice","candy","juice","rice"},
        {"oo","OO","Uu","ee"},
        {"A","E","R","h"},
        {"is","a ","this ","are"},
        {"player","plays","play","playing"},
        {"animal","bird","tiger","human"},
        {"Is","a ","it","are"},
        {"Hate","love","live","leave"}

};

private String mCarrectAnswer []={

      /* "100","10","0","Ans 0","10","15","105","60","150","10000","30","20","25","10","1000",*/

        "She","study","o","name","to","A","color","eat","A","to","read","A","juice","oo","A","is","playing", "animal","is","love"};

public String getQuestion (int a)
{
    String question = mQuestions[a] ;
    return question;
}

public String getChoices1 (int a){
    String choice = mChoices[a][0];
    return choice;
}
public String getChoices2 (int a){
    String choice = mChoices[a][1];
    return choice;
}
public String getChoices3 (int a){
    String choice = mChoices[a][2];
    return choice;
}
public String getChoices4 (int a){
    String choice = mChoices[a][3];
    return choice;
}

public String getCarrectAnswer (int a){

    String answer = mCarrectAnswer[a];
    return answer;
}



}
