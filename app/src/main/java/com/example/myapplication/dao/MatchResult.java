package com.example.myapplication.dao;

import com.example.myapplication.utils.ImgUtils;

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

    //点击该坐标
    public void tap(){
        ImgUtils.tap(this.x,this.y);
    }

    public void tap(String name){
        ImgUtils.tap(this.x,this.y,name);
    }

    /**
     * 普遍情况相似度大于0.9视为存在
     */
    public boolean isExist(){
        return this.similarity > 0.9;
    }

}
