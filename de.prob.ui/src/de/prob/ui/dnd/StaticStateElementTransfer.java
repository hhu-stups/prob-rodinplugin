/**
 * 
 */
package de.prob.ui.dnd;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * @author plagge
 * 
 */
public class StaticStateElementTransfer extends ByteArrayTransfer {
	private static final String TYPENAME = "internal_transfer_"
			+ StaticStateElementTransfer.class.getName();
	private static final int TYPEID = registerType(TYPENAME);
	private static StaticStateElementTransfer instance = new StaticStateElementTransfer();

	private static byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private StaticStateElement[] currentElements;

	public static StaticStateElementTransfer getInstance() {
		return instance;
	}

	private StaticStateElementTransfer() {
	}

	@Override
	protected void javaToNative(final Object object,
			final TransferData transferData) {
		System.out.println("javaToNative");
		if (object != null && object instanceof StaticStateElement[]
				&& isSupportedType(transferData)) {
			System.out.println("javaToNative: stored");
			currentElements = (StaticStateElement[]) object;
			super.javaToNative(EMPTY_BYTE_ARRAY, transferData);
		}
	}

	@Override
	protected Object nativeToJava(final TransferData transferData) {
		System.out.println("nativeToJava");
		final StaticStateElement[] result;
		if (isSupportedType(transferData)) {
			result = currentElements;
			System.out.println("nativeToJava: loaded");
		} else {
			result = null;
		}
		return result;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPENAME };
	}
}
