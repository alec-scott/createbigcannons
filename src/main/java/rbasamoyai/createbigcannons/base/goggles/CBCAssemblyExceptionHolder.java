package rbasamoyai.createbigcannons.base.goggles;

import org.jetbrains.annotations.Nullable;

import com.zurrtum.create.content.contraptions.AssemblyException;

/**
 * A block entity whose last assembly failure is shown on the goggle overlay.
 *
 * <p>PORT-NOTE(fabric): stands in for Create's {@code IDisplayAssemblyExceptions}, which is client-only in Create Fly.
 * {@code CBCGoggleTooltip} reads this on the client.</p>
 */
public interface CBCAssemblyExceptionHolder {

	@Nullable AssemblyException getLastAssemblyException();

}
