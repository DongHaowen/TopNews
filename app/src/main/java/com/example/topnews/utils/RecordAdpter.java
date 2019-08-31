package com.example.topnews.utils;

public class RecordAdpter {
    int index;
    int step = 1;
    private RecordHandler handler;

    public RecordAdpter(RecordHandler handler){
        this.handler = handler;
    }

    public void setStep(final int step){
        this.step = step;
    }

    public void setStart(final int index){
        this.index = index;
    }

    public String next(){
        try {
            index += step;
            return handler.records.get(index - step);
        } catch (Exception e){
            return null;
        }
    }
}
