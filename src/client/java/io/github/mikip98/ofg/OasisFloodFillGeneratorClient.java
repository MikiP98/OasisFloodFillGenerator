package io.github.mikip98.ofg;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.del.api.CacheAPI;
import io.github.mikip98.del.structures.BlockstateWrapper;
import io.github.mikip98.del.structures.SimplifiedProperty;
import io.github.mikip98.ofg.automation.FloodFill;
import io.github.mikip98.ofg.automation.PropertiesReader;
import io.github.mikip98.ofg.automation.PropertiesWriter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OasisFloodFillGeneratorClient implements ClientModInitializer {
	public static final String MOD_ID = "oasis-floodfill-generator";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("Oasis FloodFill Generator is coloring the world!");

		// Create the config directory if it doesn't exist
		Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-floodfill-generator");
		try {
			Files.createDirectories(configPath);
		} catch (IOException e) {
			LOGGER.error("Error while creating config directory", e);
		}

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(literal("generate_floodfill")
						.executes(context -> {
							generateFloodFill();
							return 0;
						})
				)
		);
	}

	@SuppressWarnings("rawtypes")
	private static void generateFloodFill() {
		LOGGER.info("Generating flood fill");
		CacheAPI.cachePathsIfNotCached();

		// ModId -> BlockstateId -> Light level -> Set of Property value pairs
		Map<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData = BlockstatesAPI.getLightEmittingBlocksData();

		// ModId -> List of BlockstateIds
		Map<String, List<String>> translucentBlocksData = BlockstatesAPI.getTranslucentBlockNames();

		// ModId -> BlockstateId -> block volume
		Map<String, Map<String, Double>> nonFullBlocksData = BlockstatesAPI.getNonFullBlocks();

		FloodFill floodFill = new FloodFill(getAlreadySupportedBlockstates(getKnownPropertyMap(lightEmittingBlocksData)));

		floodFill.generateFloodfillForLightEmittingBlocks(lightEmittingBlocksData);
		floodFill.generateFloodfillForTranslucentBlocks(translucentBlocksData);
		floodFill.generateFloodfillForNonFullBlocks(nonFullBlocksData);

		PropertiesWriter.writeToProperties(
				floodFill.floodFillEmissiveFormat1BlockEntries,
				floodFill.floodFillEmissiveFormat1ItemEntries,
				floodFill.floodFillTranslucentEntries,
				floodFill.floodFillIgnoreEntries
		);
	}

	@SuppressWarnings("rawtypes")
	private static Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> getAlreadySupportedBlockstates(Map<String, SimplifiedProperty> propertyMap) {
		Map<String, Map<String, Set<Map<String, Comparable>>>> alreadySupportedBlockstatesWIds = PropertiesReader.getAlreadySupportedBlockstatesWIds(false);

		Map<String, Map<String, Set<Map<SimplifiedProperty, Comparable>>>> alreadySupportedBlockstates = new HashMap<>();

		// Create alreadySupportedBlockstates by getting the blockstates from alreadySupportedBlockstatesWIds and merging them with lightEmittingBlocksData by getting the SimplifiedProperty from lightEmittingBlocksData
		for (Map.Entry<String, Map<String, Set<Map<String, Comparable>>>> modEntry : alreadySupportedBlockstatesWIds.entrySet()) {
			String modId = modEntry.getKey();
			Map<String, Set<Map<String, Comparable>>> blockstates = modEntry.getValue();
			alreadySupportedBlockstates.put(modId, new HashMap<>());

			for (Map.Entry<String, Set<Map<String, Comparable>>> blockstateEntry : blockstates.entrySet()) {
				String blockstateId = blockstateEntry.getKey();
				Set<Map<String, Comparable>> propertySets = blockstateEntry.getValue();
				Set<Map<SimplifiedProperty, Comparable>> simplifiedPropertySets = new HashSet<>();

				for (Map<String, Comparable> propertySet : propertySets) {
					Map<SimplifiedProperty, Comparable> simplifiedPropertySet = new HashMap<>();
					for (Map.Entry<String, Comparable> propertyEntry : propertySet.entrySet()) {
						String propertyName = propertyEntry.getKey();
						Comparable propertyValue = propertyEntry.getValue();
						if (propertyMap.containsKey(propertyName)) {
							simplifiedPropertySet.put(propertyMap.get(propertyName), propertyValue);
						} else {
							simplifiedPropertySet.put(new SimplifiedProperty(propertyName, null), propertyValue);
						}
					}
					simplifiedPropertySets.add(simplifiedPropertySet);
				}
				alreadySupportedBlockstates.get(modId).put(blockstateId, simplifiedPropertySets);
			}
		}

		return alreadySupportedBlockstates;
	}

	// TODO: Replace with fullPropertyMap, by iterating through every block's default blockstate automation set
	@SuppressWarnings("rawtypes")
	private static @NotNull Map<String, SimplifiedProperty> getKnownPropertyMap(Map<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> lightEmittingBlocksData) {
		Map<String, SimplifiedProperty> propertyMap = new HashMap<>();

		for (Map.Entry<String, Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>>> modEntry : lightEmittingBlocksData.entrySet()) {
			Map<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>> blockstates = modEntry.getValue();

			for (Map.Entry<BlockstateWrapper, Map<Byte, Set<Map<SimplifiedProperty, Comparable>>>> blockstateEntry : blockstates.entrySet()) {
				Map<Byte, Set<Map<SimplifiedProperty, Comparable>>> lightLevels = blockstateEntry.getValue();

				for (Map.Entry<Byte, Set<Map<SimplifiedProperty, Comparable>>> lightLevelEntry : lightLevels.entrySet()) {
					Set<Map<SimplifiedProperty, Comparable>> propertySets = lightLevelEntry.getValue();

					for (Map.Entry<SimplifiedProperty, Comparable> propertyEntry : propertySets.iterator().next().entrySet()) {
						SimplifiedProperty property = propertyEntry.getKey();

						if (!propertyMap.containsKey(property.name)) {
							propertyMap.put(property.name, property);
						}
					}
				}
			}
		}

		return propertyMap;
	}
}