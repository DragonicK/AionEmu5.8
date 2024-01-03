/*

 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.panel_cp;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlType(name = "panel_cp")
@XmlAccessorType(XmlAccessType.FIELD)
public class PanelCp {
	@XmlAttribute
	protected int id;
	@XmlAttribute
	protected String name;
	@XmlAttribute(name = "panelType", required = true)
	private PanelCpType panelCpType;
	@XmlAttribute
	protected int statsId;
	@XmlAttribute
	protected int statValue;
	@XmlAttribute
	protected int cost;
	@XmlAttribute
	protected int countMax;
	@XmlAttribute
	protected int costAdj;
	@XmlAttribute
	protected int preCondId;
	@XmlAttribute
	protected int preEnchantCount;
	@XmlAttribute
	protected int minLevel;
	@XmlElement(name = "panel_cp_skill")
	private List<PanelCpSkill> skills;

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public PanelCpType getPanelCpType() {
		return panelCpType;
	}

	public int getStatsId() {
		return statsId;
	}

	public int getStatValue() {
		return statValue;
	}

	public int getCost() {
		return cost;
	}

	public int getCountMax() {
		return countMax;
	}

	public int getCostAdj() {
		return costAdj;
	}

	public int getPreCondId() {
		return preCondId;
	}

	public int getPreEnchantCount() {
		return preEnchantCount;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public List<PanelCpSkill> getSkills() {
		return this.skills;
	}
}