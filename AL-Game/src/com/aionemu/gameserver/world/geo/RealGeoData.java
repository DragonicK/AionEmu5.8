/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.aionemu.gameserver.world.geo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.GeoWorldLoader;
import com.aionemu.gameserver.geoEngine.models.GeoMap;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.utils.Util;

import gnu.trove.map.hash.TIntObjectHashMap;

public class RealGeoData implements GeoData {
	private static final Logger log = LoggerFactory.getLogger(RealGeoData.class);
	private TIntObjectHashMap<GeoMap> geoMaps = new TIntObjectHashMap();

	@Override
	public void loadGeoMaps() {
		Map<String, Spatial> models = this.loadMeshes();
		this.loadWorldMaps(models);
		models.clear();
		models = null;
		log.info("Geodata: " + this.geoMaps.size() + " geo maps loaded!");
	}

	protected void loadWorldMaps(Map<String, Spatial> models) {
		log.info("Loading geo maps..");
		Util.printProgressBarHeader(DataManager.WORLD_MAPS_DATA.size());
		ArrayList<Integer> mapsWithErrors = new ArrayList<Integer>();
		for (WorldMapTemplate map : DataManager.WORLD_MAPS_DATA) {
			GeoMap geoMap = new GeoMap(Integer.toString(map.getMapId()), map.getWorldSize());
			try {
				if (GeoWorldLoader.loadWorld(map.getMapId(), models, geoMap)) {
					geoMaps.put(map.getMapId(), geoMap);
				}
			} catch (Throwable t) {
				mapsWithErrors.add(map.getMapId());
				geoMaps.put(map.getMapId(), DummyGeoData.DUMMY_MAP);
			}
			Util.printCurrentProgress();
		}
		Util.printEndProgress();
		if (mapsWithErrors.size() > 0) {
			log.warn("Some maps were not loaded correctly and reverted to dummy implementation: ");
			log.warn(((Object) mapsWithErrors).toString());
		}
	}

	protected Map<String, Spatial> loadMeshes() {
		log.info("Loading meshes..");
		Map<String, Spatial> models = null;
		try {
			models = GeoWorldLoader.loadMeshs("data/geo/meshs.geo");
		} catch (IOException e) {
			throw new IllegalStateException("Problem loading meshes", e);
		}
		return models;
	}

	@Override
	public GeoMap getMap(int worldId) {
		GeoMap geoMap = (GeoMap) this.geoMaps.get(worldId);
		return geoMap != null ? geoMap : DummyGeoData.DUMMY_MAP;
	}
}
