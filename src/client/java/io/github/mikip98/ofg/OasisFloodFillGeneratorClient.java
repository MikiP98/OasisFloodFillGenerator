package io.github.mikip98.ofg;

import io.github.mikip98.del.api.BlockstatesAPI;
import io.github.mikip98.ofg.iProperties.FloodFill;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		// ModId -> BlockstateId -> Light level -> Set of Property value pairs
		Map<String, Map<String, Map<Byte, Set<Map<String, Comparable>>>>> lightEmittingBlocksData = BlockstatesAPI.getLightEmittingBlocksData();

		// ModId -> List of BlockstateIds
		Map<String, List<String>> translucentBlocksData = BlockstatesAPI.getTranslucentBlockNames();

		// ModId -> BlockstateId -> block volume
		Map<String, Map<String, Double>> nonFullBlocksData = BlockstatesAPI.getNonFullBlocks();

		FloodFill floodFill = new FloodFill();

		floodFill.generateFloodfillForLightEmittingBlocks(lightEmittingBlocksData);
	}


}