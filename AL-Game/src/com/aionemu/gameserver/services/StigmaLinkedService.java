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
package com.aionemu.gameserver.services;

import java.util.List;

import com.aionemu.gameserver.model.skill.SkillEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author DrNism
 */

public class StigmaLinkedService {
	private static final Logger log = LoggerFactory.getLogger(StigmaLinkedService.class);

	public static void onLogOut(Player player) {
		if (player != null) {
			if (player.getSkillList() != null) {
				player.getSkillList().removeLinkedSkills();
			}
		}

		player.setStigmaSet(0);
	}

	public static void checkEquipConditions(Player player, List<Integer> list, int level) {
		boolean check = false;
		for (Integer Stigma : list) {
			ItemTemplate it = DataManager.ITEM_DATA.getItemTemplate(Stigma);
			if (it.getName().contains("(Inert)")) {
				check = true;
			}
		}

		switch (player.getPlayerClass()) {
			case GLADIATOR:
				if (list.size() >= 6 && !check) {
					// 140001106 발목 베기
					// 140001119 회전 일격
					// 140001108 파멸의 맹타
					// 140001107 고갈의 파동
					// 140001118 흡혈의 검
					// 140001104 노련한 반격
					// 140001103 유린의 맹타
					// 140001105 예리한 일격
					if (list.contains(140001119) && list.contains(140001107) && list.contains(140001108)
							|| list.contains(140001119) && list.contains(140001107) && list.contains(140001106)
							|| list.contains(140001119) && list.contains(140001108) && list.contains(140001106)) {
						addLinkedSkill(player, 641, level, 55);
						addLinkedSkill(player, 642, level, 59);
						addLinkedSkill(player, 643, level, 63);
					} else if (list.contains(140001118) && list.contains(140001103) && list.contains(140001105)
							|| list.contains(140001118) && list.contains(140001103) && list.contains(140001104)
							|| list.contains(140001118) && list.contains(140001105) && list.contains(140001104)) {
						addLinkedSkill(player, 727, level, 55);
						addLinkedSkill(player, 729, level, 59);
						addLinkedSkill(player, 731, level, 63);
					} else {
						if (player.getRace() == Race.ELYOS) {
							addLinkedSkill(player, 658, level, 55);
							addLinkedSkill(player, 660, level, 59);
							addLinkedSkill(player, 662, level, 63);
						}
						else {
							addLinkedSkill(player, 657, level, 55);
							addLinkedSkill(player, 659, level, 59);
							addLinkedSkill(player, 661, level, 63);
						}
					}
				}
				return;
			case TEMPLAR:
				if (list.size() >= 6 && !check) {
					// 140001134 주신의 보호
					// 140001122 비호의 갑옷
					// 140001120 기사희생
					// 140001125 견고한 방패
					// 140001135 파멸의 방패
					// 140001123 정신파괴
					// 140001124 속박의 파동
					// 140001121 마력 강타
					if (list.contains(140001134) && list.contains(140001120) && list.contains(140001125)
							|| list.contains(140001134) && list.contains(140001120) && list.contains(140001122)
							|| list.contains(140001134) && list.contains(140001125) && list.contains(140001122)) {
						addLinkedSkill(player, 2919, level, 55);
						addLinkedSkill(player, 2920, level, 59);
						addLinkedSkill(player, 2921, level, 63);
					} else if (list.contains(140001135) && list.contains(140001124) && list.contains(140001123)
							|| list.contains(140001135) && list.contains(140001124) && list.contains(140001121)
							|| list.contains(140001135) && list.contains(140001123) && list.contains(140001121)) {
						addLinkedSkill(player, 2918, level, 55);
					} else {
						addLinkedSkill(player, 2915, level, 55);
						addLinkedSkill(player, 2916, level, 59);
						addLinkedSkill(player, 2917, level, 63);
					}
				}
				return;
			case ASSASSIN:
				if (list.size() >= 6 && !check) {
					// 140001152 비수의 서약
					// 140001138 육감 극대화
					// 140001139 도주자세
					// 140001141 번개베기
					// 140001151 혼절의 칼날
					// 140001136 폭약바르기
					// 140001140 기습베기
					// 140001137 연쇄 문양 폭발
					if (list.contains(140001151) && list.contains(140001136) && list.contains(140001140)
							|| list.contains(140001151) && list.contains(140001136) && list.contains(140001137)
							|| list.contains(140001151) && list.contains(140001140) && list.contains(140001137)) {
						addLinkedSkill(player, 3239, level, 55);
						addLinkedSkill(player, 3240, level, 59);
						addLinkedSkill(player, 3241, level, 63);
					} else if (list.contains(140001152) && list.contains(140001138) && list.contains(140001141)
							|| list.contains(140001152) && list.contains(140001138) && list.contains(140001139)
							|| list.contains(140001152) && list.contains(140001141) && list.contains(140001139)) {
						addLinkedSkill(player, 3236, level, 55);
						addLinkedSkill(player, 3237, level, 59);
						addLinkedSkill(player, 3238, level, 63);
					} else {
						addLinkedSkill(player, 3242, level, 55);
						addLinkedSkill(player, 3243, level, 59);
						addLinkedSkill(player, 3244, level, 63);
					}
				}
				return;
			case RANGER:
				if (list.size() >= 6 && !check) {
					// 140001172 고통의 화살
					// 140001155 사냥꾼의 결의
					// 140001157 폭발 화살
					// 140001153 광포의 화살
					// 140001173 번개 화살
					// 140001154 광풍 화살
					// 140001158 화살 강화
					// 140001156 질풍 화살
					if (list.contains(140001173) && list.contains(140001154) && list.contains(140001156)
							|| list.contains(140001173) && list.contains(140001154) && list.contains(140001158)
							|| list.contains(140001173) && list.contains(140001156) && list.contains(140001158)) {
						addLinkedSkill(player, 936, level, 55);
						addLinkedSkill(player, 937, level, 59);
						addLinkedSkill(player, 938, level, 63);
					} else if (list.contains(140001172) && list.contains(140001153) && list.contains(140001157)
							|| list.contains(140001172) && list.contains(140001153) && list.contains(140001155)
							|| list.contains(140001172) && list.contains(140001157) && list.contains(140001155)) {
						addLinkedSkill(player, 1006, level, 55);
						addLinkedSkill(player, 1007, level, 59);
						addLinkedSkill(player, 1008, level, 63);
					} else {
						if (player.getRace() == Race.ELYOS) {
							addLinkedSkill(player, 1061, level, 55);
							addLinkedSkill(player, 1063, level, 59);
							addLinkedSkill(player, 1065, level, 63);
						} else {
							addLinkedSkill(player, 1060, level, 55);
							addLinkedSkill(player, 1062, level, 59);
							addLinkedSkill(player, 1064, level, 63);
						}
					}
				}
				return;
			case SORCERER:
				if (list.size() >= 6 && !check) {
					// 140001191 빙설의 갑주
					// 140001174 수면 폭풍
					// 140001181 바람의 칼날
					// 140001178 환영의 회오리
					// 140001192 빙하 강타
					// 140001176 암석 소환
					// 140001177 화염 난무
					// 140001184 태풍 소환
					if (list.contains(140001191) && list.contains(140001178) && list.contains(140001174)
							|| list.contains(140001191) && list.contains(140001178) && list.contains(140001181)
							|| list.contains(140001191) && list.contains(140001174) && list.contains(140001181)) {
						addLinkedSkill(player, 1340, level, 55);
						addLinkedSkill(player, 1341, level, 59);
						addLinkedSkill(player, 1342, level, 63);
					} else if (list.contains(140001192) && list.contains(140001176) && list.contains(140001177)
							|| list.contains(140001192) && list.contains(140001176) && list.contains(140001184)
							|| list.contains(140001192) && list.contains(140001177) && list.contains(140001184)) {
						addLinkedSkill(player, 1540, level, 55);
						addLinkedSkill(player, 1541, level, 59);
						addLinkedSkill(player, 1542, level, 63);
					} else {
						addLinkedSkill(player, 1418, level, 55);
						addLinkedSkill(player, 1419, level, 59);
						addLinkedSkill(player, 1420, level, 63);
					}
				}
				return;
			case SPIRIT_MASTER:
				if (list.size() >= 6 && !check) {
					// 140001209 황천의 저주
					// 140001195 지옥의 고통
					// 140001193 둔화의 족쇄
					// 140001194 마법 역류
					// 140001210 강화의 갑주
					// 140001199 치유
					// 140001197 회오리 기운
					// 140001196 파멸의 공세
					if (list.contains(140001209) && list.contains(140001195) && list.contains(140001193)
							|| list.contains(140001209) && list.contains(140001195) && list.contains(140001194)
							|| list.contains(140001209) && list.contains(140001193) && list.contains(140001194)) {
						addLinkedSkill(player, 3541, level, 55);
						addLinkedSkill(player, 3542, level, 59);
						addLinkedSkill(player, 3543, level, 63);
					} else if (list.contains(140001210) && list.contains(140001199) && list.contains(140001197)
							|| list.contains(140001210) && list.contains(140001199) && list.contains(140001196)
							|| list.contains(140001210) && list.contains(140001197) && list.contains(140001196)) {
						addLinkedSkill(player, 3549, level, 55);
					} else {
						addLinkedSkill(player, 3849, level, 55);
						addLinkedSkill(player, 3850, level, 59);
						addLinkedSkill(player, 3851, level, 63);
					}
				}
				return;
			case CLERIC:
				if (list.size() >= 6 && !check) {
					// 140001246 생명의 권능
					// 140001234 치유의 기운
					// 140001232 재생의 광휘
					// 140001233 정화의 물결
					// 140001245 벽력
					// 140001229 고통의 연쇄
					// 140001228 악화의 낙인
					// 140001230 고결한 기운
					if (list.contains(140001246) && list.contains(140001234) && list.contains(140001233)
							|| list.contains(140001246) && list.contains(140001234) && list.contains(140001232)
							|| list.contains(140001246) && list.contains(140001233) && list.contains(140001232)
							|| list.contains(140001246) && list.contains(140001235) && list.contains(140001232)
							|| list.contains(140001246) && list.contains(140001235) && list.contains(140001233)) {
						addLinkedSkill(player, 3932, level, 55);
						addLinkedSkill(player, 3933, level, 59);
						addLinkedSkill(player, 3934, level, 63);
					} else if (list.contains(140001245) && list.contains(140001230) && list.contains(140001228)
							|| list.contains(140001245) && list.contains(140001231) && list.contains(140001228)
							|| list.contains(140001245) && list.contains(140001231) && list.contains(140001229)
							|| list.contains(140001245) && list.contains(140001230) && list.contains(140001229)
							|| list.contains(140001245) && list.contains(140001228) && list.contains(140001229)) {
						addLinkedSkill(player, 4167, level, 55);
						addLinkedSkill(player, 4168, level, 59);
						addLinkedSkill(player, 4169, level, 63);
					} else {
						if (player.getRace() == Race.ELYOS) {
							addLinkedSkill(player, 3906, level, 55);
							addLinkedSkill(player, 3907, level, 59);
							addLinkedSkill(player, 3908, level, 63);
						}
						else  {
							addLinkedSkill(player, 3909, level, 55);
							addLinkedSkill(player, 3910, level, 59);
							addLinkedSkill(player, 3911, level, 63);
						}
					}
				}
				return;
			case CHANTER:
				if (list.size() >= 6 && !check) {
					// 140001226 철벽의 주문
					// 140001212 쾌유의 손길
					// 140001213 수호의 주문
					// 140001211 수호의 축복
					// 140001227 충격쇄
					// 140001214 바람의 축복
					// 140001216 파산격
					// 140001215 파동격
					if (list.contains(140001227) && list.contains(140001216) && list.contains(140001215)
							|| list.contains(140001227) && list.contains(140001216) && list.contains(140001214)
							|| list.contains(140001227) && list.contains(140001215) && list.contains(140001214)) {
						addLinkedSkill(player, 1901, level, 55);
						addLinkedSkill(player, 1902, level, 59);
						addLinkedSkill(player, 1903, level, 63);
					} else if (list.contains(140001226) && list.contains(140001213) && list.contains(140001211)
							|| list.contains(140001226) && list.contains(140001213) && list.contains(140001212)
							|| list.contains(140001226) && list.contains(140001211) && list.contains(140001212)) {
						addLinkedSkill(player, 1907, level, 55);
						addLinkedSkill(player, 1908, level, 59);
						addLinkedSkill(player, 1909, level, 63);
					} else {
						addLinkedSkill(player, 1904, level, 55);
						addLinkedSkill(player, 1905, level, 59);
						addLinkedSkill(player, 1906, level, 63);
					}
				}
				return;
			case AETHERTECH:
				if (list.size() >= 6 && !check) {
					// 140001279 도약격파
					// 140001264 출력 극대화
					// 140001269 전기 속박
					// 140001265 폭풍 연타
					// 140001280 이드 보호막
					// 140001266 급소 찌르기
					// 140001268 마력 장막
					// 140001267 기동력 증폭
					if (list.contains(140001280) && list.contains(140001268) && list.contains(140001267)
							|| list.contains(140001280) && list.contains(140001268) && list.contains(140001266)
							|| list.contains(140001280) && list.contains(140001267) && list.contains(140001266)) {
						addLinkedSkill(player, 2861, level, 55);
						addLinkedSkill(player, 2862, level, 59);
						addLinkedSkill(player, 2863, level, 63);
					} else if (list.contains(140001279) && list.contains(140001269) && list.contains(140001265)
							|| list.contains(140001279) && list.contains(140001269) && list.contains(140001264)
							|| list.contains(140001279) && list.contains(140001265) && list.contains(140001264)) {
						addLinkedSkill(player, 2852, level, 55);
						addLinkedSkill(player, 2855, level, 59);
						addLinkedSkill(player, 2858, level, 63);
					} else {
						addLinkedSkill(player, 2849, level, 55);
						addLinkedSkill(player, 2850, level, 59);
						addLinkedSkill(player, 2851, level, 63);
					}
				}
				return;
			case GUNSLINGER:
				if (list.size() >= 6 && !check) {
					// 140001262 미간 정조준
					// 140001249 마력탄 충전
					// 140001247 급소 관통
					// 140001248 마력탄 강화
					// 140001263 절흔포
					// 140001251 속박의 포탄
					// 140001252 마력의 은혜
					// 140001250 균열의 포탄
					if (list.contains(140001263) && list.contains(140001251) && list.contains(140001252)
							|| list.contains(140001263) && list.contains(140001251) && list.contains(140001250)
							|| list.contains(140001263) && list.contains(140001252) && list.contains(140001250)) {
						addLinkedSkill(player, 2371, level, 55);
						addLinkedSkill(player, 2374, level, 59);
						addLinkedSkill(player, 2377, level, 63);
					} else if (list.contains(140001262) && list.contains(140001248) && list.contains(140001249)
							|| list.contains(140001262) && list.contains(140001248) && list.contains(140001247)
							|| list.contains(140001262) && list.contains(140001249) && list.contains(140001247)) {
						addLinkedSkill(player, 2368, level, 55);
						addLinkedSkill(player, 2369, level, 59);
						addLinkedSkill(player, 2370, level, 63);
					} else {
						addLinkedSkill(player, 2380, level, 55);
						addLinkedSkill(player, 2381, level, 59);
						addLinkedSkill(player, 2382, level, 63);
					}
				}
				return;
			case SONGWEAVER:
				if (list.size() >= 6 && !check) {
					// 140001297 면죄의 선율
					// 140001285 치유 변주곡
					// 140001283 치유 주법
					// 140001286 환희의 선율
					// 140001296 불협 화음
					// 140001281 마비의 메아리
					// 140001284 증폭 주법
					// 140001282 모스키 광사곡
					if (list.contains(140001297) && list.contains(140001286) && list.contains(140001285)
							|| list.contains(140001297) && list.contains(140001286) && list.contains(140001283)
							|| list.contains(140001297) && list.contains(140001285) && list.contains(140001283)) {
						addLinkedSkill(player, 4483, level, 55);
					} else if (list.contains(140001296) && list.contains(140001284) && list.contains(140001281)
							|| list.contains(140001296) && list.contains(140001284) && list.contains(140001282)
							|| list.contains(140001296) && list.contains(140001281) && list.contains(140001282)) {
						addLinkedSkill(player, 4474, level, 55);
						addLinkedSkill(player, 4477, level, 59);
						addLinkedSkill(player, 4480, level, 63);
					} else {
						addLinkedSkill(player, 4564, level, 55);
						addLinkedSkill(player, 4565, level, 59);
						addLinkedSkill(player, 4566, level, 63);
					}
				}
				return;
			default:
				break;
		}
		check = false;
	}

	private static void addLinkedSkill(Player player, int skillId, int skillLevel, int levelReq) {
		if (player.getLevel() >= levelReq) {
			 SkillEntry entry = player.getSkillList().getSkillEntry(skillId);

			if (entry != null) {
				entry.setSkillLvl(skillLevel);
				// Do not send skills to client, send it later.
			}
			else {
				player.getSkillList().addLinkedSkill(player, skillId, skillLevel);
			}
		}
	}
}