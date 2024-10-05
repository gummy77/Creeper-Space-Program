package org.gum.csp.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.client.model.GneepEntityModel;
import org.gum.csp.entity.GneepEntity;

public class GneepEntityRenderer extends MobEntityRenderer<GneepEntity, GneepEntityModel> {
    public GneepEntityRenderer(EntityRendererFactory.Context context){
        super(context, new GneepEntityModel(context.getPart(CspMainClient.GNEEP_MODEL_LAYER)), 0.5f);
        this.shadowRadius = 0.45f;
    }

    @Override
    public Identifier getTexture(GneepEntity entity) {
        return new Identifier(CspMain.MODID, "textures/entity/gneep_texture.png");
    }
}
