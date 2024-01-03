package com.aionemu.gameserver.services.player.CreativityPanel;

public class CreativityEntry {
    private int id;
    private int point;

    public  CreativityEntry(int id, int point) {
        this.id = id;
        this.point = point;
    }

    public int getId() { return this.id; }
    public int getPoint() { return this.point; }
    public void setId(int id) { this.id = id; }
    public void setPoint(int point) { this.point = point; }
}