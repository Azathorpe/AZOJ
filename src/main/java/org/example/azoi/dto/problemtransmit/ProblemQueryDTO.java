package org.example.azoi.dto.problemtransmit;

public class ProblemQueryDTO {
    private String title;
    private Byte difficulty;
    private String tag;
    private Boolean isVisible;
    private int page;
    private int size;

    public ProblemQueryDTO() {
    }

    public ProblemQueryDTO(String title, Byte difficulty, String tag, Boolean isVisible, int page, int size) {
        this.title = title;
        this.difficulty = difficulty;
        this.tag = tag;
        this.isVisible = isVisible;
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Byte difficulty) {
        this.difficulty = difficulty;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }
}
