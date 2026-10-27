package com.example.crosshairmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class CrosshairColorMod implements ClientModInitializer {

    private static final int RED  = 0xFFFF2020; // تقدر تضرب
    private static final int CYAN = 0xFF20D0FF; // الهدف رافع شيلد (وقت الاكس ستن)

    @Override
    public void onInitializeClient() {
        // نستبدل الكروس هير: الابيض = الاصلي، الاحمر/الازرق = حقنا
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (ctx, tickCounter) -> {
            int color = getColor(MinecraftClient.getInstance());
            if (color == 0) {
                original.render(ctx, tickCounter);
            } else {
                drawCross(ctx, color);
            }
        });
    }

    private static int getColor(MinecraftClient mc) {
        if (mc.player == null || mc.currentScreen != null) return 0;
        if (!mc.options.getPerspective().isFirstPerson()) return 0;

        HitResult hit = mc.crosshairTarget;
        if (hit instanceof EntityHitResult ehr) {
            Entity e = ehr.getEntity();
            if (e.isAttackable() && e.isAlive()) {
                if (e instanceof LivingEntity le && le.isBlocking()) return CYAN;
                return RED;
            }
        }
        return 0;
    }

    private static void drawCross(DrawContext ctx, int color) {
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        // حدود سوداء
        ctx.fill(cx - 6, cy - 1, cx + 7, cy + 2, 0xFF000000);
        ctx.fill(cx - 1, cy - 6, cx + 2, cy + 7, 0xFF000000);
        // الصليب الملون
        ctx.fill(cx - 5, cy, cx + 6, cy + 1, color);
        ctx.fill(cx, cy - 5, cx + 1, cy + 6, color);
    }
}
