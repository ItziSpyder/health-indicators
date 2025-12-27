package io.github.itzispyder.healthindicators.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class RenderLayers {

    public static final Function<Identifier, RenderLayer> TEX_QUADS;
    public static final RenderLayer LINES;

    public static final RenderPipeline PIPELINE_TEX_QUADS = RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
            .withLocation("pipeline/translucent")
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build();

    public static final RenderPipeline PIPELINE_LINES = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation("pipeline/global_lines_pipeline")
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withDepthWrite(true)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build();

    private static RenderSetup emptyParams(RenderPipeline pipeline) {
        return RenderSetup.builder(pipeline)
                .layeringTransform(LayeringTransform.NO_LAYERING)
                .outputTarget(OutputTarget.MAIN_TARGET)
                .build();
    }

    private static RenderSetup textureParams(RenderPipeline pipeline, Identifier id) {
        return RenderSetup.builder(pipeline)
                .texture("Sampler0", id, () -> RenderSystem.getSamplerCache().get(FilterMode.NEAREST))
                .layeringTransform(LayeringTransform.NO_LAYERING)
                .outputTarget(OutputTarget.MAIN_TARGET)
                .build();
    }

    static {
        LINES = RenderLayer.of("cc_layer_lines", emptyParams(PIPELINE_LINES));
        TEX_QUADS = id -> RenderLayer.of("cc_layer_tex_quad", textureParams(PIPELINE_TEX_QUADS, id));
    }
}