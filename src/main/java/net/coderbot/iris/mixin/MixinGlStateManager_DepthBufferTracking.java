package net.coderbot.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.coderbot.iris.gl.sampler.SamplerLimits;
import net.coderbot.iris.samplers.DepthBufferTracker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin(GlStateManager.class)
public class MixinGlStateManager_DepthBufferTracking {
	@Inject(method = "_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", at = @At("HEAD"))
	private static void iris$onTexImage2D(int target, int level, int internalformat, int width, int height, int border,
										  int format, int type, @Nullable IntBuffer pixels, CallbackInfo ci) {
		DepthBufferTracker.INSTANCE.trackTexImage2D(RenderSystem.getTextureId(GlStateManagerAccessor.getActiveTexture()), internalformat);
	}

	@Inject(method = "_deleteTexture(I)V", at = @At("HEAD"))
	private static void iris$onDeleteTexture(int id, CallbackInfo ci) {
		DepthBufferTracker.INSTANCE.trackDeleteTextures(id);
	}

	@Inject(method = "_deleteTextures([I)V", at = @At("HEAD"))
	private static void iris$onDeleteTextures(int[] ids, CallbackInfo ci) {
		for (int id : ids) {
			DepthBufferTracker.INSTANCE.trackDeleteTextures(id);
		}
	}

	@ModifyConstant(method = "_getTextureId", constant = @Constant(intValue = 12), require = 1)
	private static int iris$increaseMaximumAllowedTextureIdUnits(int existingValue) {
		return SamplerLimits.get().getMaxTextureUnits();
	}
}
