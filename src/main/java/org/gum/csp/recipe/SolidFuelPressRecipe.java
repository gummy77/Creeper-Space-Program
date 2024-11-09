package org.gum.csp.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.gum.csp.CspMain;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.item.RocketPartItem;
import org.spongepowered.include.com.google.gson.Gson;

public class SolidFuelPressRecipe implements Recipe<CraftingInventory> {

    private final Ingredient rocketPart;
    private final Ingredient modifier;
    private final float powerModifier;
    private final float burnTimeModifier;
    private final Identifier id;

    public SolidFuelPressRecipe(Identifier id, Ingredient rocketPart, Ingredient modifier, float powerModifier, float burnTimeModifier) {
        this.id = id;
        this.rocketPart = rocketPart;
        this.modifier = modifier;
        this.powerModifier = powerModifier;
        this.burnTimeModifier = burnTimeModifier;
    }
    @Override
    public boolean matches(CraftingInventory inv, World world) {

        boolean checkA = this.rocketPart.test(inv.getStack(0));
        boolean checkB = this.modifier.test(inv.getStack(1));

        return checkA && checkB;
    }
    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack stack = inv.getStack(0).copy();

        RocketPart part = RocketPart.fromNbt(stack.getSubNbt("BlockEntityTag").getCompound("rocketPart"));

        if(part.fuelComponent != null) {
            part.fuelComponent.burnPower *= this.powerModifier;
            part.fuelComponent.burnSpeed *= this.burnTimeModifier;
        }

        NbtCompound nbt = new NbtCompound();
        nbt.put("rocketPart", part.toNbt());
        stack.setSubNbt("BlockEntityTag", nbt);

        ItemStack newStack = inv.getStack(0);
        newStack.writeNbt(stack.getNbt());

        return newStack;
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    public Ingredient getRocketPart() {
        return rocketPart;
    }
    public Ingredient getModifier() {
        return modifier;
    }
    public float getPowerModifier() {
        return powerModifier;
    }
    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }
    @Override
    public Identifier getId() {
        return this.id;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }
    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SolidFuelPressRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "solid_fuel_press_recipe_type";
    }

    public static class Serializer implements RecipeSerializer<SolidFuelPressRecipe> {

        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(CspMain.MODID, "solid_fuel_press_recipe");

        @Override
        public SolidFuelPressRecipe read(Identifier id, JsonObject json) {

            Ingredient rocketPart = Ingredient.fromJson(JsonHelper.getObject(json, "rocketPart"));
            Ingredient modifier = Ingredient.fromJson(JsonHelper.getObject(json, "modifier"));
            float powerModifier = JsonHelper.getFloat(json, "powerModifier");
            float burnTimeModifier = JsonHelper.getFloat(json, "burnTimeModifier");

            return new SolidFuelPressRecipe(id, rocketPart, modifier, powerModifier, burnTimeModifier);
        }

        @Override
        public void write(PacketByteBuf buf, SolidFuelPressRecipe recipe) {
            recipe.getRocketPart().write(buf);
            recipe.getModifier().write(buf);
            buf.writeFloat(recipe.powerModifier);
            buf.writeFloat(recipe.burnTimeModifier);
        }

        @Override
        public SolidFuelPressRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient rocketPart = Ingredient.fromPacket(buf);
            Ingredient modifier = Ingredient.fromPacket(buf);
            float powerModifier = buf.readFloat();
            float burnTimeModifier = buf.readFloat();

            return new SolidFuelPressRecipe(id, rocketPart, modifier, powerModifier, burnTimeModifier);
        }
    }
}
