package daniel.mutzafi.playandlearngeora;

public class TestStatistic {
    private String testName;
    private int correctAnswers;
    private int totalQuestions;
    private String date;

    public TestStatistic(String testName, int correctAnswers, int totalQuestions, String date) {
        this.testName = testName;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.date = date;
    }

    public String getTestName() {
        return testName;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public String getDate() {
        return date;
    }
}
