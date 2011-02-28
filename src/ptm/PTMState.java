package src.ptm;

import java.util.HashMap;

import src.ptm.PTMObject.RecursionException;

/**
 * <p>A PTMState binds values to names.</p>
 * <p><code>val1 | name = val2</code></p>
 * <p><code>val1</code> will be bound to the name <code>1</code>, <code>val2</code> to the name <code>name</code>.</p>
 */
public class PTMState extends HashMap<String, PTMArgumentValueNode> {
	private static final long serialVersionUID = 1L;
	public static final String forwardState = "FORWARD_STATE";

	public PTMState() {}
	
	public PTMState(PTMNode parent) {
		readState(parent);
	}
	
	/**
	 * Reads name/value bindings from a tree containing PTMArgumentNodes.
	 */
	public void readState(PTMNode parent) {
		for (PTMObject o : parent.childTree) {
			if (o instanceof PTMArgumentNode) {
				try {
					put(o.childTree.get(0).evaluate(), (PTMArgumentValueNode)o.childTree.get(1));
				} catch (RecursionException e) {
					put(o.childTree.get(0).getRawContent(), (PTMArgumentValueNode)o.childTree.get(1));
					e.printStackTrace();
				}
			}
		}
	}
	
	/** <p>Manually binds a value to a name. Overwrites existing bindings.</p>
	 * <p>If <code>value == <strong>null</strong> || value.length() == 0</code>, the binding will be removed.</p>
	 * @return The old value as returned by {@link #resolve(String)}, if the binding to this name already existed */
	public String bind(String name, String value) {
		String oldVal = resolve(name);
		if (value != null && value.length() > 0) {
			put(name, new PTMArgumentValueNode(new StringBuffer(value)));
		} else {
			remove(name);
		}
		return oldVal;
	}
	/** 
	 * Abbreviation for {@link #bind(String, String)}, returning <code><strong>this</strong></code>
	 * @return <code><strong>this</strong></code> for usage like <br/><code>sigma.b("a", "alpha").b("b", "beta")...</code>
	 */
	public PTMState b(String name, String value) {
		bind(name, value);
		return this;
	}
	
	/**
	 * @return The value bound to <code>name</code>, or an empty String by default.
	 */
	public String resolve(String name) {
		return resolve(name, "");
	}
	
	public String resolve(String name, String alternative) {
		assert alternative != null;
		
		PTMArgumentValueNode val = get(name);
		
		if (val == null) {
			return alternative;
		} else {
			try {
				return val.evaluate();
			} catch (RecursionException e) {
				e.printStackTrace();
				return val.getRawContent();
			}
		}
	}
	
	public StringBuilder printValues() {
		StringBuilder vals = new StringBuilder();
		
		for (java.util.Map.Entry<String, PTMArgumentValueNode> e : entrySet()) {
			vals.append(e.getKey());
			vals.append("\t --> ");
			vals.append(e.getValue().getRawContent());
			vals.append("\n");
		}
		
		System.out.print(vals);
		
		return vals;
	}
	
	/**
	 * <p>Adds bindings from a string list, with each string of the form<br/>
	 * {@code name=value}</p>
	 */
	public void bindValuesFromList(String... bindings) {
		if (bindings != null) {
			int pos;
			String key;
			String value;
			for (String line : bindings) {
				pos = line.indexOf('=');
				if (pos > 0) {
					key = line.substring(0, pos);
					value = line.substring(pos+1);
					if (key.length() > 0 && value.length() > 0) {
						b(key, value);
						System.out.printf("Bound %s to %s.\n", key, value);
					} else {
						System.out.printf("Did not bind %s to %s.\n", key, value);
					}
				}
			}
		}
	}
}
