package tech.nmhillusion.n2mix.model;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-09-11
 */
public class StoryBook extends Book {
    private String chapterNo;

    public String getChapterNo() {
        return chapterNo;
    }

    public StoryBook setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
        return this;
    }
}
