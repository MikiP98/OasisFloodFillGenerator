package io.github.mikip98.opg;

import io.github.mikip98.del.api.CacheAPI;
import io.github.mikip98.opg.generation.MainGenerator;
import io.github.mikip98.opg.io.DebugWriter;
import io.github.mikip98.opg.io.PropertiesReader;
import io.github.mikip98.opg.io.PropertiesWriter;
import io.github.mikip98.opg.objects.DotPropertiesInfo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OasisPropertyGeneratorClient implements ClientModInitializer {
	public static final String MOD_ID = "oasis-property-generator";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("Oasis Property Generator is coloring the world!");

		// Create the config directory if it doesn't exist
		Path configPath = FabricLoader.getInstance().getGameDir().resolve("config/oasis-property-generator");
		try {
			Files.createDirectories(configPath);
		} catch (IOException e) {
			LOGGER.error("Error while creating config directory", e);
		}

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(literal("oasis")
						.then(literal("generate_all").executes(context -> {
//							Thread thread = new Thread(
//                                    OasisPropertyGeneratorClient::generateAutoShaderSupport
//							);
//							thread.start();
							generateAutoShaderSupport();
							return 0;
						}))
//						.then(literal("SSS").executes(context -> {
//							Thread thread = new Thread();
//							thread.start();
//							return 0;
//						}))
//						.then(literal("FloodFill")
//								.then(literal("FloodFill").executes(context -> {
//									return 0;
//								}))
//						)
				)
		);
	}

	private static void generateAutoShaderSupport() {
		LOGGER.info("Generating Automatic Shader Support...");

		CacheAPI.cachePathsIfNotCached();

		DotPropertiesInfo dotPropertiesInfo = PropertiesReader.getDotPropertiesInfo();

		MainGenerator mainGenerator = new MainGenerator(dotPropertiesInfo);
		LOGGER.info("Natively supported blocks: {}", dotPropertiesInfo.nativelySupportedBlockstates);
		DebugWriter.saveNativelySupportedDataToFile(dotPropertiesInfo.nativelySupportedBlockstates);
		mainGenerator.run();

		PropertiesWriter writer = new PropertiesWriter(mainGenerator.getSupport());
		writer.writeToProperties();

		LOGGER.info("Done!");
	}
}