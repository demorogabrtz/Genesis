package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.equipment.GenesisEquipmentAssets;
import net.minecraft.client.data.EquipmentAssetProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GenesisEquipmentAssetProvider extends EquipmentAssetProvider {
    protected final DataOutput.PathResolver pathResolver;

    public GenesisEquipmentAssetProvider(DataOutput output) {
        super(output);
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "equipment");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Map<RegistryKey<EquipmentAsset>, EquipmentModel> equipmentMap = new HashMap<>();

        GenesisEquipmentAssets.bootstrap((key, model) -> {
            if (equipmentMap.putIfAbsent(key, model) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + key);
            }
        });

        return DataProvider.writeAllToPath(writer, EquipmentModel.CODEC, pathResolver::resolveJson, equipmentMap);
    }
}
