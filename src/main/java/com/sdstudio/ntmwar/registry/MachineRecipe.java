package com.sdstudio.ntmwar.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Рецепт машины с сеткой 3×3 (Ammo Press HBM) в нейтральном виде, без классов HBM: описание живёт в
 * {@link ModRecipes}, а перевод во внутренний формат HBM — в {@link com.sdstudio.ntmwar.compat.HbmCompat}.
 * <p>
 * Ячейка — {@code null} (пусто) или {@link Input}: имя в OreDictionary либо конкретный ItemStack, с количеством.
 */
public final class MachineRecipe {

    public static final int GRID_SIZE = 9;

    private final ItemStack output;
    private final Input[] grid;

    /** @param grid ровно 9 ячеек, построчно слева направо, сверху вниз; {@code null} — пустая ячейка */
    public MachineRecipe(ItemStack output, Input... grid) {
        if (output == null) throw new IllegalArgumentException("Recipe output must not be null");
        if (grid.length != GRID_SIZE) {
            throw new IllegalArgumentException("Machine recipe grid must have " + GRID_SIZE + " cells");
        }
        this.output = output.copy();
        this.grid = grid.clone();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    /** Ячейки сетки (копия массива). */
    public Input[] getGrid() {
        return grid.clone();
    }

    /** Все непустые ингредиенты с учётом количества (для запасного рецепта на верстаке). */
    public List<Object> flattenIngredients() {
        List<Object> result = new ArrayList<Object>();
        for (Input input : grid) {
            if (input == null) continue;
            for (int i = 0; i < input.getCount(); i++) {
                result.add(input.getIngredient());
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        return "MachineRecipe{" + output + " <- " + Arrays.toString(grid) + "}";
    }

    /** Ингредиент ячейки: имя OreDictionary ({@link String}) или {@link ItemStack}, плюс количество. */
    public static final class Input {

        private final Object ingredient;
        private final int count;

        private Input(Object ingredient, int count) {
            if (count < 1) throw new IllegalArgumentException("Ingredient count must be positive");
            this.ingredient = ingredient;
            this.count = count;
        }

        public static Input ore(String oreName, int count) {
            if (oreName == null || oreName.isEmpty()) throw new IllegalArgumentException("Ore name is empty");
            return new Input(oreName, count);
        }

        public static Input ore(String oreName) {
            return ore(oreName, 1);
        }

        public static Input stack(ItemStack stack, int count) {
            if (stack == null || stack.getItem() == null) throw new IllegalArgumentException("Stack is empty");
            return new Input(stack.copy(), count);
        }

        public static Input stack(ItemStack stack) {
            return stack(stack, 1);
        }

        /** {@link String} (OreDictionary) или копия {@link ItemStack}. */
        public Object getIngredient() {
            return ingredient instanceof ItemStack ? ((ItemStack) ingredient).copy() : ingredient;
        }

        public boolean isOre() {
            return ingredient instanceof String;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return count + "x" + ingredient;
        }
    }
}
