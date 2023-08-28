package theomenden.polyprolene.mixin.keys;

import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BooleanSupplier;

@Mixin(StickyKeyBinding.class)
public interface StickyKeyAccessor {
    @Accessor
    public BooleanSupplier getToggleGetter();
}
