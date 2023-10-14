package com.example.myapplication.dao;

/**
 * 图片匹配结果类，包含坐标以及相似度
 */
public class MatchResult {

    public MatchResult() {
    }

    public MatchResult(int x, int y, Double similarity) {
        this.x = x;
        this.y = y;
        this.similarity = similarity;
    }

    private int x;
    private int y;

    private Double similarity;

    private String scene;

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }
}
