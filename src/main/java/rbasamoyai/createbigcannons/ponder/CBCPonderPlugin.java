package rbasamoyai.createbigcannons.ponder;

import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.Identifier;
import rbasamoyai.createbigcannons.CreateBigCannons;

public class CBCPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() { return CreateBigCannons.MOD_ID; }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        CBCPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
        CBCPonderTags.register(helper);
    }
}
