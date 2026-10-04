package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link FlameFeatureRenderer} to alter Fire behavior for consistency.
 */
@Mixin(FlameFeatureRenderer.class)
public abstract class FlameFeatureRendererMixin {
  /**
   * Since 26.2 the flame sprites are looked up once per frame in {@code buildGroup} and passed to {@code prepare} for each burning entity.<br>
   * Wraps that {@code prepare} call to assign the correct sprites (0 and 1) for the fire type the entity is burning from.
   *
   * @param instance {@link FlameFeatureRenderer} instance owning the wrapped method.
   * @param submit the flame submission for the burning entity.
   * @param consumer vertex consumer.
   * @param sprite0 original first sprite.
   * @param sprite1 original second sprite.
   * @param original the {@link Operation} for the modified method.
   */
  @WrapOperation(method = "buildGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer;prepare(Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer$Submit;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
  private void wrapPrepare(FlameFeatureRenderer instance, FlameFeatureRenderer.Submit submit, VertexConsumer consumer, TextureAtlasSprite sprite0, TextureAtlasSprite sprite1, Operation<Void> original) {
    Identifier fireType = ((FireTyped) submit.entityRenderState()).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      original.call(instance, submit, consumer, FireClientManager.getSprite0(fireType), FireClientManager.getSprite1(fireType));
      return;
    }
    original.call(instance, submit, consumer, sprite0, sprite1);
  }
}
