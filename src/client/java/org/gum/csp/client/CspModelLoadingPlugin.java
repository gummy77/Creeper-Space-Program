package org.gum.csp.client;

import net.fabricmc.fabric.api.client.model.*;
import net.fabricmc.fabric.impl.client.model.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class CspModelLoadingPlugin {
    public static final ModelIdentifier TEST_MODEL = new ModelIdentifier("csp", "Test");

}
