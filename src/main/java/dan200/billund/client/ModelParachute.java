
package dan200.billund.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

//Model code generated by MCModeller. All animation, AI,
//and special features are yours to program. Also keep in
//mind that some of these class names may have changed since
//wrote this. Make sure to check for compatibility with the
//latest version of the Minecraft Coder Pack before attempting
//to use this code.

public class ModelParachute extends ModelBase {

    public ModelRenderer Parachute;
    public ModelRenderer LeftWing;
    public ModelRenderer RightWing;
    public ModelRenderer wire;
    public ModelRenderer wire2;
    public ModelRenderer wire3;
    public ModelRenderer wire4;

    private static void setPosition(ModelRenderer m, float x, float y, float z) {
        m.offsetX = x * 0.0625f;
        m.offsetY = y * 0.0625f;
        m.offsetZ = z * 0.0625f;
    }

    private static void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }

    public ModelParachute() {
        Parachute = new ModelRenderer(this, 0, 0);
        Parachute.addBox(-6.0F, -0.5F, -6.0F, 12, 1, 12, 0);
        setPosition(Parachute, 0.0F, -10.0F, -0.0f);
        setRotation(Parachute, 0.0f, 0.0f, 0.0f);

        LeftWing = new ModelRenderer(this, 0, 0);
        LeftWing.addBox(-3.0F, -0.5F, -6.0F, 6, 1, 12, 0);
        setPosition(LeftWing, -8.5F, -8.5F, -0.0f);
        setRotation(LeftWing, 0.0f, 0.0f, -10.0f);

        RightWing = new ModelRenderer(this, 0, 0);
        RightWing.addBox(-3.0F, -0.5F, -6.0F, 6, 1, 12, 0);
        setPosition(RightWing, 8.5F, -8.5F, -0.0f);
        setRotation(RightWing, 0.0f, 0.0f, 10.0f);

        wire = new ModelRenderer(this, 0, 0);
        wire.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0);
        setPosition(wire, -6.0F, -4.0F, -4.0f);

        wire2 = new ModelRenderer(this, 0, 0);
        wire2.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0);
        setPosition(wire2, -6.0F, -4.0F, 4.0f);

        wire3 = new ModelRenderer(this, 0, 0);
        wire3.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0);
        setPosition(wire3, 6.0F, -4.0F, -4.0f);

        wire4 = new ModelRenderer(this, 0, 0);
        wire4.addBox(-0.5F, -5.0F, -0.5F, 1, 10, 1, 0);
        setPosition(wire4, 6.0F, -4.0F, 4.0f);

    }

    public void render(float f5) {
        Parachute.render(f5);
        LeftWing.render(f5);
        RightWing.render(f5);
        //wire.render(f5);
        //wire2.render(f5);
        //wire3.render(f5);
        //wire4.render(f5);
    }
}