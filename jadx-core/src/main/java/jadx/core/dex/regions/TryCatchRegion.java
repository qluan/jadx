package jadx.core.dex.regions;

import jadx.core.dex.nodes.IBranchRegion;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.trycatch.ExceptionHandler;
import jadx.core.dex.trycatch.TryCatchBlock;
import jadx.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TryCatchRegion extends AbstractRegion implements IBranchRegion {

	private final IContainer tryRegion;
	private Map<ExceptionHandler, IContainer> catchRegions = Collections.emptyMap();
	private IContainer finallyRegion;
	private TryCatchBlock tryCatchBlock;

	public TryCatchRegion(IRegion parent, IContainer tryRegion) {
		super(parent);
		this.tryRegion = tryRegion;
	}

	public void setTryCatchBlock(TryCatchBlock tryCatchBlock) {
		this.tryCatchBlock = tryCatchBlock;
		int count = tryCatchBlock.getHandlersCount();
		this.catchRegions = new LinkedHashMap<ExceptionHandler, IContainer>(count);
		for (ExceptionHandler handler : tryCatchBlock.getHandlers()) {
			IContainer handlerRegion = handler.getHandlerRegion();
			if (handlerRegion != null) {
				if (handler.isFinally()) {
					finallyRegion = handlerRegion;
				} else {
					catchRegions.put(handler, handlerRegion);
				}
			}
		}
	}

	public IContainer getTryRegion() {
		return tryRegion;
	}

	public Map<ExceptionHandler, IContainer> getCatchRegions() {
		return catchRegions;
	}

	public TryCatchBlock getTryCatchBlock() {
		return tryCatchBlock;
	}

	public IContainer getFinallyRegion() {
		return finallyRegion;
	}

	public void setFinallyRegion(IContainer finallyRegion) {
		this.finallyRegion = finallyRegion;
	}

	@Override
	public List<IContainer> getSubBlocks() {
		List<IContainer> all = new ArrayList<IContainer>(2 + catchRegions.size());
		all.add(tryRegion);
		all.addAll(catchRegions.values());
		if (finallyRegion != null) {
			all.add(finallyRegion);
		}
		return Collections.unmodifiableList(all);
	}

	@Override
	public List<IContainer> getBranches() {
		return getSubBlocks();
	}

	@Override
	public String baseString() {
		return tryRegion.baseString();
	}

	@Override
	public String toString() {
		return "Try: " + tryRegion
				+ " catches: " + Utils.listToString(catchRegions.values())
				+ (finallyRegion == null ? "" : " finally: " + finallyRegion);
	}
}
