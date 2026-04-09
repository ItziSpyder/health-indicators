package io.github.itzispyder.healthindicators.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class RenderLayers {

    public static final Function<Identifier, RenderType> TEX_QUADS;
    public static final RenderType LINES;

    public static final ColorTargetState WITH_BLEND = new ColorTargetState(BlendFunction.TRANSLUCENT);
    public static final DepthStencilState DEPTH_LEQUAL = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true);

    public static final RenderPipeline PIPELINE_TEX_QUADS = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation("pipeline/translucent")
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .withColorTargetState(WITH_BLEND)
            .withCull(false)
            .withDepthStencilState(DEPTH_LEQUAL)
            .build();

    public static final RenderPipeline PIPELINE_LINES = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation("pipeline/global_lines_pipeline")
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
            .withColorTargetState(WITH_BLEND)
            .withCull(false)
            .withDepthStencilState(DEPTH_LEQUAL)
            .build();

    private static RenderSetup emptyParams(RenderPipeline pipeline) {
        return RenderSetup.builder(pipeline)
                .setLayeringTransform(LayeringTransform.NO_LAYERING)
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup();
    }

    private static RenderSetup textureParams(RenderPipeline pipeline, Identifier id) {
        return RenderSetup.builder(pipeline)
                .withTexture("Sampler0", id, () -> RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST))
                .setLayeringTransform(LayeringTransform.NO_LAYERING)
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup();
    }

    static {
        LINES = RenderType.create("cc_layer_lines", emptyParams(PIPELINE_LINES));
        TEX_QUADS = id -> RenderType.create("cc_layer_tex_quad", textureParams(PIPELINE_TEX_QUADS, id));
    }
}