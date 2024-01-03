package com.aionemu.gameserver.model.templates.panel_cp;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.*;

@XmlType(name = "panel_cp_skill")
@XmlAccessorType(XmlAccessType.FIELD)
public class PanelCpSkill {
    @XmlAttribute(name = "id")
    protected int id;
    @XmlAttribute(name = "race")
    protected Race race;
    @XmlAttribute(name = "isAdditional")
    protected int isAdditional;

    public int getSkillId() {
        return this.id;
    }

    public void setSkillId(int skillId) {
        this.id = skillId;
    }

    public Race getRace() {
        return this.race;
    }

    public void setRace(Race race){
        this.race = race;
    }

    public boolean isAdditionalSkill() {
        return this.isAdditional == 1;
    }
}