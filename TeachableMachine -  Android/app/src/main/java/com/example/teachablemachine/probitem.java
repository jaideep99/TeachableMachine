package com.example.teachablemachine;

public class probitem {

    String classname;
    float prob;

    probitem(String classname,float prob)
    {
        this.classname = classname;
        this.prob = prob;
    }

    public float getProb() {
        return prob;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setProb(float prob) {
        this.prob = prob;
    }
}
